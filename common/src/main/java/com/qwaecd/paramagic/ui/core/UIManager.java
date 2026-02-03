package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.hit.UIHitResult;
import com.qwaecd.paramagic.ui.io.mouse.MouseEvent;
import com.qwaecd.paramagic.ui.io.mouse.MouseEventType;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;
import com.qwaecd.paramagic.ui.overlay.OverlayRoot;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
     * 处理鼠标事件
     * @param e 鼠标事件 (可能会升级为双击或其他)
     * @return 事件是否被框架消费
     */
    public boolean handleMouseEvent(MouseEvent e) {
        boolean accepted = this.mouseStateMachine.updateState(e);
        if (!accepted) {
            return false;
        }

        MouseEvent mouseEvent = this.checkDoubleClick(e);

        UIEventContext context = new UIEventContext(this, mouseEvent);

//        this.debugOnly(mouseEvent);

        if (this.capturedNode != null) {
            this.capturedNode.processEvent(context);
        } else {
            UIHitResult hitPath = this.rootNode.createHitPath(
                    (float) mouseEvent.mouseX, (float) mouseEvent.mouseY, UIHitResult.createEmpty()
            );
            while (!hitPath.isEmpty() && !context.isConsumed()) {
                hitPath.popNode().processEvent(context);
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

    private void debugOnly(MouseEvent event) {
//        UIHitResult hitResult = this.rootNode.createHitPath(
//                (float) mouseEvent.mouseX, (float) mouseEvent.mouseY, UIHitResult.createEmpty()
//        );

//        if (mouseEvent.type == MouseEventType.CLICK) {
//            System.out.println("命中" + hitResult.getHitPath().size() + "个节点");
//            while (!hitResult.isEmpty()) {
//                UINode uiNode = hitResult.getHitPath().pop();
//                System.out.println("Hit Node: " + uiNode.worldRect.x + ", " + uiNode.worldRect.y + ", " + uiNode.worldRect.w + ", " + uiNode.worldRect.h);
//            }
//        }

        if (event.isClickOrDouble()) {
            System.out.println("clicked at (" + event.mouseX + ", " + event.mouseY + "), is " + event.type);
        }
    }

    private MouseEvent checkDoubleClick(MouseEvent maybeDouble) {
        if (maybeDouble.type == MouseEventType.CLICK && this.mouseStateMachine.isDoubleClick()) {
            return new MouseEvent.DoubleClick((MouseEvent.Click) maybeDouble);
        }

        return maybeDouble;
    }

    public void mouseMove(double mouseX, double mouseY) {
        this.mouseStateMachine.onMouseMove(mouseX, mouseY);
        if (this.capturedNode != null) {
            this.capturedNode.onMouseMove(mouseX, mouseY);
        }
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
