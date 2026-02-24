package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import com.qwaecd.paramagic.ui.animation.UIAnimationSystem;
import com.qwaecd.paramagic.ui.animation.UIAnimator;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.impl.WheelEvent;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.ui.widget.UIScrollView;
import com.qwaecd.paramagic.ui_project.edit_table.SpellEditTableUI;
import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaStructEditNode extends UIScrollView {
    @Nullable
    private ParaPathNode rootPathNode;

    @Nullable
    private UIAnimator<Float> scrollAnimator;

    public ParaStructEditNode() {
        super(false);
        this.sensitivity = 64.0f;
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
    }

    public void updateFromParaData(@Nonnull ParaData paraData) {
        ParaComponentData root = paraData.rootComponent;
        ParaPathNode rootNode = new ParaPathNode(root.getComponentId());
        this.buildComponentNode(root, rootNode);
        this.flushPathNode(rootNode);
    }

    private void flushPathNode(@Nonnull ParaPathNode pathNode) {
        if (this.rootPathNode != null) {
            this.removeChild(this.rootPathNode);
        }
        this.rootPathNode = pathNode;
        this.addChild(pathNode);
        this.reLayoutPathNode();
        pathNode.addListener(
                AllUIEvents.MOUSE_DOUBLE_CLICK,
                EventPhase.BUBBLING,
                context -> this.reLayoutPathNode()
        );
        pathNode.addListener(
                AllUIEvents.WHEEL,
                EventPhase.BUBBLING,
                this::onMouseScroll
        );
    }

    private void reLayoutPathNode() {
        if (this.rootPathNode != null) {
            this.recalculateContentExtent();
            this.clampViewOffset();
            this.layoutChildren();
        }
    }

    private void buildComponentNode(ParaComponentData parentData, ParaPathNode parentNode) {
        for (ParaComponentData child : parentData.getChildren()) {
            ParaPathNode childNode = new ParaPathNode(child.getComponentId());
            parentNode.addChild(childNode);
            this.buildComponentNode(child, childNode);
        }
        for (int i = 0; i < 10; i++) {
            ParaPathNode childNode = new ParaPathNode(Component.literal("test" + i));
            parentNode.addChild(childNode);
        }
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void onMouseScroll(UIEventContext<WheelEvent> context) {
        final float start = this.viewOffset;
        super.onMouseScroll(context);
        if (this.scrollAnimator != null) {
            if (!this.scrollAnimator.isFinished()) {
                return;
            }
            UIAnimationSystem.getInstance().removeAnimator(this.scrollAnimator);
        }
        UIManager manager = context.manager;
        this.scrollAnimator = this.animate(
                start, this.viewOffset, 0.1f,
                Interpolation::easeOutSine,
                (interpolationValue -> this.viewOffset = interpolationValue)
        ).setOnUpdate(offset -> {
            manager.offerOveringTestTask();
            this.clampViewOffset();
            this.recalculateContentExtent();
            this.layoutChildren();
        });
    }

    @Override
    protected void recalculateContentExtent() {
        float maxExtent = 0.0f;
        for (UINode child : this.children) {
            if (child instanceof ParaPathNode paraPathNode) {
                float end = this.isHorizontal
                        ? paraPathNode.worldRect.x + paraPathNode.worldRect.w
                        : child.getLocalRect().y + paraPathNode.getSubtreeHeight();
                if (end > maxExtent) {
                    maxExtent = end;
                }
                continue;
            }

            float end = this.isHorizontal
                    ? child.getLocalRect().x + child.getLocalRect().w
                    : child.getLocalRect().y + child.getLocalRect().h;
            if (end > maxExtent) {
                maxExtent = end;
            }
        }
        this.contentExtent = maxExtent;
    }

    void onContainerChanged(SpellEditTableUI mainUI, InventoryHolder container, UISlot slot) {
        ItemStack item = slot.getItem();
        if (!(item.getItem() instanceof ParaCrystalItem)) {
            this.removePathNode();
            return;
        }
        if (!mainUI.getContainerNode().isItemStack(item)) {
            this.removePathNode();
            return;
        }
        ParaCrystalData crystalData = CrystalComponentUtils.getComponentFromItemStack(item);
        if (crystalData == null) {
            this.removePathNode();
            return;
        }
        ParaData paraData = crystalData.getParaData();
        this.updateFromParaData(paraData);
    }

    private void removePathNode() {
        if (this.rootPathNode != null) {
            this.removeChild(this.rootPathNode);
            this.rootPathNode = null;
        }
    }
}
