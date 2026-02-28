package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.tools.ModRL;
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
import com.qwaecd.paramagic.ui.util.NineSliceSprite;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.UIScrollView;
import com.qwaecd.paramagic.ui_project.edit_table.EditTableSprite;
import com.qwaecd.paramagic.ui_project.edit_table.SpellEditTableUI;
import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaStructEditNode extends UIScrollView {
    @Nullable
    private ParaPathNode rootPathNode;
    private final float rootPathNodeOffsetY = 16.0f;

    private final StructHeader header;

    @Nullable
    private UIAnimator<Float> scrollAnimator;

    public ParaStructEditNode() {
        super(false);
        this.header = new StructHeader();
        this.header.localRect.setWH(this.localRect.w, rootPathNodeOffsetY);
        this.addChild(this.header);

        this.sensitivity = 64.0f;
    }

    public static class StructHeader extends UINode {
        static final NineSliceSprite headerBackGround =
                NineSliceSprite.builder(ModRL.inModSpace("textures/gui/edit_table.png"), EditTableSprite.TEX_W, EditTableSprite.TEX_H)
                        .slice(0, 0,  224, 16, 16)
                        .slice(1, 16, 224, 32, 16)
                        .slice(2, 48, 224, 16, 16)
                        .slice(3, 0,  240, 16, 16)
                        .slice(4, 16, 240, 32, 16)
                        .slice(5, 48, 240, 16, 16)
                        .slice(6, 0,  256, 16, 16)
                        .slice(7, 16, 256, 32, 16)
                        .slice(8, 48, 256, 16, 16)
                        .build();
        private final UILabel label;
        public StructHeader() {
            this.label = new UILabel(Component.translatable("gui.paramagic.spell_edit_table.para_struct"));
            this.label.getLayoutParams().center();
            this.addChild(this.label);
        }

        @Override
        protected void render(@Nonnull UIRenderContext context) {
        }

        @Override
        public void layout(float parentX, float parentY, float parentW, float parentH) {
            super.layout(parentX, parentY, parentW, parentH);
        }

        @Override
        protected void renderBackGround(UIRenderContext context) {
//            context.fill(
//                    this.worldRect.x, this.worldRect.y,
//                    this.worldRect.x + this.worldRect.w,
//                    this.worldRect.y + this.worldRect.h,
//                    UIColor.fromRGBA(141, 85, 55, 255)
//            );
            context.renderNineSliceSprite(
                    headerBackGround,
                    (int) this.worldRect.x,
                    (int) this.worldRect.y,
                    (int) (this.worldRect.x + this.worldRect.w),
                    (int) (this.worldRect.y + this.worldRect.h)
            );
        }
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
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
        this.rootPathNode.localRect.setXY(0.0f, rootPathNodeOffsetY);
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
        this.header.localRect.setWH(this.localRect.w, rootPathNodeOffsetY);
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
