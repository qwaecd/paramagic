package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.UIEvent;
import com.qwaecd.paramagic.ui.event.api.AllUIEvents;
import com.qwaecd.paramagic.ui.event.api.UIEventContext;
import com.qwaecd.paramagic.ui.event.api.UIEventKey;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.event.impl.WheelEvent;
import com.qwaecd.paramagic.ui.hit.UIHitResult;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;
import com.qwaecd.paramagic.ui.overlay.OverlayRoot;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 一个 screen 对应一个 UIManager 实例，管理整个 UI 树的布局和渲染
 */
public class UIManager {
    private final MouseStateMachine mouseStateMachine;
    @Nonnull
    private final TooltipRenderer tooltipRenderer;

    @Nullable
    private UINode capturedNode;

    @Getter
    public final UINode rootNode;
    @Getter
    private final OverlayRoot overlayRoot;

    public UIManager(UINode rootNode, @Nonnull TooltipRenderer tooltipRenderer) {
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
            this.capturedNode.onMouseMove(mouseX, mouseY);
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
            UIHitResult hitResult = this.rootNode.createHitPath((float) mouseX, (float) mouseY, UIHitResult.createEmpty());
            List<UINode> hitPath = hitResult.getHitPath();

            // 捕获阶段：从根到目标（索引 0 是根，索引越大越深）
            for (int i = 0; i < hitPath.size() && !context.isPropagationStopped(); i++) {
                hitPath.get(i).handleEvent(context, EventPhase.CAPTURING);
            }

            // 目标阶段
            if (!hitPath.isEmpty() && !context.isPropagationStopped()) {
                UINode target = hitPath.get(hitPath.size() - 1);
                target.dispatchTargetEvent(context);
            }

            // 冒泡阶段：从目标到根
            for (int i = hitPath.size() - 1; i >= 0 && !context.isPropagationStopped(); i--) {
                hitPath.get(i).handleEvent(context, EventPhase.BUBBLING);
            }
        }

        return context.isConsumed();
    }

    public void captureNode(@Nullable UINode node) {
        this.capturedNode = node;
    }

    public void releaseCapture() {
        this.capturedNode = null;
    }

    // 由 UIRenderContext 调用
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.tooltipRenderer.renderTooltip(guiGraphics, mouseX, mouseY);
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
