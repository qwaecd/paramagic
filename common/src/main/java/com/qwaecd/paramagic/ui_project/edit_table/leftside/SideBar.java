package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.*;
import com.qwaecd.paramagic.ui.event.impl.MouseLeave;
import com.qwaecd.paramagic.ui.event.impl.MouseOver;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.inventory.ContainerHolder;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.ui.io.mouse.CursorType;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.node.MouseCaptureNode;
import com.qwaecd.paramagic.ui_project.edit_table.BarState;
import com.qwaecd.paramagic.ui_project.edit_table.SpellEditTableUI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SideBar extends UINode {

    @Nonnull
    private final ParaStructEditNode structEditNode;

    @Nonnull
    private final ResizeNode resizeNode;

    @Nonnull
    private BarState state;

    public SideBar() {
        this.state = BarState.NULL;
        this.clipMod = ClipMod.RECT;
        this.localRect.set(
                0.0f, 0.0f,
                94.3f, this.getWindowHeight() / this.getGuiScale()
        );

        this.structEditNode = new ParaStructEditNode();
        this.resizeNode = new ResizeNode(this);
        this.addChild(this.resizeNode);
    }

    public static final class ResizeNode extends MouseCaptureNode {
        @Nonnull
        private final SideBar sideBar;
        public ResizeNode(@Nonnull SideBar sideBar) {
            this.sideBar = sideBar;
            this.localRect.w = 4.0f;
        }

        @Override
        public void onMouseMove(double mouseX, double mouseY, MouseStateMachine mouseState) {
            if (this.ignoreTransform) {
                return;
            }
            // 计算鼠标在父节点坐标系下的位置
            UINode parent = this.getParent();
            if (parent == null) {
                this.localRect.x = (float) mouseX - this.grabOffsetX;
//                this.localRect.y = (float) mouseY - this.grabOffsetY;
                this.layout(
                        0.0f, 0.0f,
                        this.localRect.w, this.localRect.h
                );
            } else {
                this.localRect.x = (float) mouseX - parent.getWorldRect().x - this.grabOffsetX;
//                this.localRect.y = (float) mouseY - parent.getWorldRect().y - this.grabOffsetY;

                this.layout(
                        parent.getWorldRect().x, parent.getWorldRect().y,
                        parent.getWorldRect().w, parent.getWorldRect().h
                );
            }
            final float newWidth = (float) (this.sideBar.localRect.w + mouseState.deltaX());
            this.sideBar.resize(newWidth);
            UIManager manager = UIManager.getInstance();
            if (manager != null) {
                manager.offerDeferredTask(UITask.create(UIManager::layoutAll, TaskStage.AFTER_EVENT));
            }
        }

        @Override
        protected void onMouseRelease(UIEventContext<MouseRelease> context) {
            super.onMouseRelease(context);
        }

        @Override
        protected void onMouseOver(UIEventContext<MouseOver> context) {
            if (context.isConsumed()) {
                return;
            }
            CursorType.setCursor(CursorType.RESIZE_HORIZONTAL);
            context.consume();
        }

        @Override
        protected void onMouseLeave(UIEventContext<MouseLeave> context) {
            if (context.isConsumed()) {
                return;
            }
            CursorType.setCursor(CursorType.ARROW);
            context.consume();
        }

        @Override
        protected void render(@Nonnull UIRenderContext context) {
            if (!this.visible) {
                return;
            }
            context.fill(worldRect.x, worldRect.y, worldRect.x + worldRect.w, worldRect.y + worldRect.h, 0x80FFFFFF);
        }
    }

    @Override
    @Nullable
    public UINode getMouseOverNode(float mouseX, float mouseY) {
        UINode node = this.resizeNode.getMouseOverNode(mouseX, mouseY);
        if (node != null) {
            return node;
        }
        return super.getMouseOverNode(mouseX, mouseY);
    }

    @Override
    @Nullable
    public UINode getTopmostHitNode(float mouseX, float mouseY) {
        UINode node = this.resizeNode.getTopmostHitNode(mouseX, mouseY);
        if (node != null) {
            return node;
        }
        return super.getTopmostHitNode(mouseX, mouseY);
    }

    /**
     * 在 SpellEditTableUI.init() 中调用，用于初始化容器内已有物品的数据。
     */
    public void initContainer(SpellEditTableUI mainUI, ContainerHolder container, UISlot containerSlot) {
        this.structEditNode.onContainerChanged(mainUI, container, containerSlot);
    }

    public void changeToParaSelectBar() {
        if (this.state == BarState.CRYSTAL_EDIT) {
            this.removeChild(this.structEditNode);
        }
        this.state = BarState.PARA_SELECT;
    }

    public void changeToCrystalEdit() {
        if (this.state != BarState.CRYSTAL_EDIT) {
            this.addChild(this.structEditNode);
        }
        this.state = BarState.CRYSTAL_EDIT;
    }

    public void changeToNull() {
        if (this.state == BarState.CRYSTAL_EDIT) {
            this.removeChild(this.structEditNode);
        }
        this.state = BarState.NULL;
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        this.localRect.h = this.getWindowHeight() / this.getGuiScale();
        this.structEditNode.localRect.set(
                this.localRect.x, this.localRect.y,
                this.localRect.w - this.resizeNode.localRect.w, this.localRect.h
        );
        this.resizeNode.localRect.set(
                this.localRect.w - resizeNode.localRect.w,
                0.0f,
                resizeNode.localRect.w,
                this.localRect.h
        );
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        if (!this.isVisible()) {
            return;
        }
        context.fill(
                worldRect.x,
                worldRect.y,
                worldRect.x + worldRect.w, worldRect.y + worldRect.h,
                UIColor.fromRGBA(50, 50, 50, 255)
        );
    }

    @Nonnull
    public BarState getBarState() {
        return this.state;
    }

    public void onContainerChanged(SpellEditTableUI mainUI, InventoryHolder container, UISlot slot) {
        this.structEditNode.onContainerChanged(mainUI, container, slot);
    }

    void resize(float newWidth) {
        final float delta = newWidth - this.localRect.w;
        this.localRect.w = newWidth;
        this.worldRect.w += delta;
    }
}
