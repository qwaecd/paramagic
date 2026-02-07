package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.MenuContent;
import com.qwaecd.paramagic.ui.api.*;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.api.event.UIEventKey;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.UIEvent;
import com.qwaecd.paramagic.ui.event.impl.*;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;
import com.qwaecd.paramagic.ui.overlay.OverlayRoot;
import lombok.Getter;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.function.Consumer;

/**
 * 一个 screen 对应一个 UIManager 实例，管理整个 UI 树的布局和渲染
 */
public class UIManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIManager.class);
    @Nonnull
    private final MenuContent menuContent;
    private final MouseStateMachine mouseStateMachine;
    @Getter
    @Nonnull
    private final TooltipRenderer tooltipRenderer;

    @Nullable
    private UINode capturedNode;

    @Getter
    public final UINode rootNode;

    @Nullable
    private UINode mouseOver = null;

    @Getter
    private final OverlayRoot overlayRoot;

    public UIManager(UINode rootNode, @Nonnull TooltipRenderer tooltipRenderer) {
        this.menuContent = new MenuContent();
        this.rootNode = rootNode;
        this.mouseStateMachine = new MouseStateMachine();
        this.tooltipRenderer = tooltipRenderer;
        this.overlayRoot = new OverlayRoot(this);
    }

    public void init() {
        this.rootNode.layout(this.rootNode.localRect.x, this.rootNode.localRect.y, this.rootNode.localRect.w, this.rootNode.localRect.h);
    }

    public void render(UIRenderContext context) {
        this.rootNode.renderTree(context);
        this.overlayRoot.renderOverlay(context);
    }

    /**
     * 处理鼠标点击事件
     * @return 事件是否被消费
     */
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        boolean accepted = this.mouseStateMachine.onMouseClick(mouseX, mouseY, button);
        if (!accepted) {
            return false;
        }

        if (this.mouseStateMachine.isDoubleClick()) {
            DoubleClick event = new DoubleClick(mouseX, mouseY, button);
            return this.dispatchEvent(AllUIEvents.MOUSE_DOUBLE_CLICK, event, mouseX, mouseY);
        }

        MouseClick event = new MouseClick(mouseX, mouseY, button);
        return this.dispatchEvent(AllUIEvents.MOUSE_CLICK, event, mouseX, mouseY);
    }

    /**
     * 处理鼠标释放事件
     * @return 事件是否被消费
     */
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        boolean accepted = this.mouseStateMachine.onMouseRelease(mouseX, mouseY, button);
        if (!accepted) {
            return false;
        }

        MouseRelease event = new MouseRelease(mouseX, mouseY, button);
        return this.dispatchEvent(AllUIEvents.MOUSE_RELEASE, event, mouseX, mouseY);
    }

    /**
     * 处理鼠标滚轮事件
     * @return 事件是否被消费
     */
    public boolean onMouseScroll(double mouseX, double mouseY, double delta) {
        WheelEvent event = new WheelEvent(mouseX, mouseY, delta);
        return this.dispatchEvent(AllUIEvents.WHEEL, event, mouseX, mouseY);
    }

    /**
     * 处理鼠标移动事件
     */
    public void onMouseMove(double mouseX, double mouseY) {
        this.mouseStateMachine.onMouseMove(mouseX, mouseY);
        if (this.capturedNode != null) {
            this.capturedNode.onMouseMove(mouseX, mouseY, this.mouseStateMachine);
        } else {
            // 如果当前没有捕获的节点，那么进行一次 over/leave 判定
            this.processMouseOverAndLeave(mouseX, mouseY);
        }
    }

    private void processMouseOverAndLeave(double mouseX, double mouseY) {
        UINode newOver = this.rootNode.getMouseOverNode((float) mouseX, (float) mouseY);
        if (this.mouseOver == null && newOver == null) {
            // 上一帧和当前帧都是 null
            return;
        }

        if (this.mouseOver == newOver) {
            // 在元素内移动
            return;
        }

        if (this.mouseOver != null) {
            this.mouseOver.onMouseLeave(
                    new UIEventContext<>(this, AllUIEvents.MOUSE_LEAVE, new MouseLeave(mouseX, mouseY))
            );
        }

        this.mouseOver = newOver;

        if (this.mouseOver != null) {
            this.mouseOver.onMouseOver(
                    new UIEventContext<>(this, AllUIEvents.MOUSE_OVER, new MouseOver(mouseX, mouseY))
            );
        }
    }

    /**
     * 捕获阶段 -> 目标阶段 -> 冒泡阶段
     * @param eventKey 事件类型
     * @param event 事件实例
     * @return 事件是否被消费
     */
    private <E extends UIEvent> boolean dispatchEvent(UIEventKey<E> eventKey, E event, double mouseX, double mouseY) {
        UIEventContext<E> context = new UIEventContext<>(this, eventKey, event);

        if (this.capturedNode != null) {
            // 不存局部变量会因为当前帧就立即释放而 NullPointerException 的喵~
            UINode captured = this.capturedNode;
            captured.handleEvent(context, EventPhase.CAPTURING);
            if (!context.isPropagationStopped()) {
                captured.dispatchTargetEvent(context);
            }
            if (!context.isPropagationStopped()) {
                captured.handleEvent(context, EventPhase.BUBBLING);
            }
        } else {
            UIHitResult hitResult = this.createHitPath(mouseX, mouseY);
            List<UINode> hitPath = hitResult.getHitPath();

            // 捕获阶段：从根到目标, 包括目标（索引 0 是根，索引越大越深）
            for (int i = 0; i < hitPath.size() && !context.isPropagationStopped(); i++) {
                hitPath.get(i).handleEvent(context, EventPhase.CAPTURING);
            }

            // 目标阶段
            if (!hitPath.isEmpty() && !context.isPropagationStopped()) {
                UINode target = hitPath.get(hitPath.size() - 1);
                target.dispatchTargetEvent(context);
            }

            // 冒泡阶段：从目标到根, 包括目标
            for (int i = hitPath.size() - 1; i >= 0 && !context.isPropagationStopped(); i--) {
                hitPath.get(i).handleEvent(context, EventPhase.BUBBLING);
            }
        }

        return context.isConsumed();
    }

    /**
     * 获取完整的命中路径, 类似于函数调用栈, 列表末尾(栈顶)是最深层的节点<br>
     * 所有路径上的节点都会被加入到命中路径中, 包括没有经过命中测试的节点.
     */
    @Nonnull
    public UIHitResult createHitPath(double mouseX, double mouseY) {
        UINode topmost = this.rootNode.getTopmostHitNode((float) mouseX, (float) mouseY);
        UIHitResult hitResult = UIHitResult.createEmpty();

        if (topmost != null) {
            hitResult.pushNode(topmost);
            UINode node = topmost.getParent();
            while (node != null) {
                hitResult.pushNode(node);
                node = node.getParent();
            }
        }

        // 相信标准库, 不要尝试自己写算法 :)
        // 反转列表, 使得索引越大越深
        hitResult.reverse();
        return hitResult;
    }

    public void captureNode(@Nullable UINode node) {
        this.capturedNode = node;
    }

    public void releaseCapture() {
        this.capturedNode = null;
    }

    /**
     * 遍历所有 UI 节点, 包括根节点, 请不要在该 lambda 内修改节点树的结构.
     * @throws ConcurrentModificationException 如果在遍历过程中修改了节点树结构.
     */
    public void forEachUINode(Consumer<UINode> action) {
        try {
            this.rootNode.forEachNodeSafe(action);
        } catch (ConcurrentModificationException cme) {
            LOGGER.error("Concurrent modification detected when traversing UI nodes. Make sure not to modify the UI tree structure within the action.", cme);
        } catch (Exception e) {
            LOGGER.error("Exception occurred when traversing UI nodes.", e);
        }
    }

    @Nonnull
    public MenuContent getMenuContent() {
        return this.menuContent;
    }

    public static boolean hasShiftKeyDown() {
        return Screen.hasShiftDown();
    }

    public static boolean hasCtrlKeyDown() {
        return Screen.hasControlDown();
    }

    public static boolean hasAltKeyDown() {
        return Screen.hasAltDown();
    }
}
