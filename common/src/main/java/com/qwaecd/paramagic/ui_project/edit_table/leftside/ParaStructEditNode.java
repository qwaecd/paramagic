package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaComponentType;
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
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.event.impl.WheelEvent;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.ui.io.mouse.MouseButton;
import com.qwaecd.paramagic.ui.util.NineSliceSprite;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.UIScrollView;
import com.qwaecd.paramagic.ui_project.edit_table.*;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaEditCache;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaStructEditWindow;
import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaStructEditNode extends UIScrollView {
    private final SideBar sideBar;
    @Nullable
    private ParaPathNode rootPathNode;
    private final float rootPathNodeOffsetY = 16.0f;

    /**
     * 真实数据构建的根节点，用于在缓存不存在时作为 fallback 显示。
     */
    @Nullable
    private ParaPathNode realRootPathNode;
    /**
     * 用于“创建缓存/重建缓存”的种子根节点。
     * 有真实数据时等同于真实根节点；无 NBT 的空白水晶时为默认 VOID 根节点。
     */
    @Nullable
    private ParaPathNode cacheSeedRootPathNode;

    private final StructHeader header;

    @Nullable
    private UIAnimator<Float> scrollAnimator;

    @Nullable
    private ParaPathNode currentSelectedNode;

    public ParaStructEditNode(SideBar sideBar) {
        super(false);
        this.sideBar = sideBar;
        this.header = new StructHeader(this);
        this.header.localRect.setWH(this.localRect.w, rootPathNodeOffsetY);
        this.addChild(this.header);

        this.sensitivity = 64.0f;
    }

    public void createParaEditWindow(ParaStructEditWindow window) {
        this.sideBar.createParaEditWindow(window);
    }

    public void closeParaEditWindow() {
        this.sideBar.closeParaEditWindow();
    }

    @Nullable
    public ParaPathNode getCurrentSelectedNode() {
        return this.currentSelectedNode;
    }

    /**
     * 获取当前真实数据根节点（非缓存）。
     */
    @Nullable
    public ParaPathNode getRealRootPathNode() {
        return this.realRootPathNode;
    }

    @Nullable
    public ParaPathNode getCacheSeedRootPathNode() {
        return this.cacheSeedRootPathNode;
    }

    @Nullable
    public ParaEditCache ensureEditCacheFromSeedRoot() {
        ParaEditCache cache = ParaEditCache.getCache();
        if (cache != null) {
            return cache;
        }
        if (this.cacheSeedRootPathNode == null) {
            return null;
        }
        return ParaEditCache.createCache(this.cacheSeedRootPathNode);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
    }

    @Override
    public void removeChild(UINode child) {
        if (child == this.currentSelectedNode) {
            this.currentSelectedNode = null;
        }
        super.removeChild(child);
    }

    public void notifyRemovePathNode(ParaPathNode node) {
        if (node == this.currentSelectedNode) {
            this.sideBar.closeParaEditWindow();
            this.currentSelectedNode = null;
        }
    }

    public void updateFromParaData(@Nonnull ParaData paraData) {
        ParaComponentData root = paraData.rootComponent;
        ParaPathNode realNode = new ParaPathNode(root);
        this.buildComponentNode(root, realNode);
        this.realRootPathNode = realNode;
        this.cacheSeedRootPathNode = realNode;

        // 缓存优先：如果存在缓存则使用缓存的根节点，否则使用真实数据
        if (ParaEditCache.hasCache()) {
            ParaEditCache editCache = ParaEditCache.getCache();
            //noinspection DataFlowIssue
            this.flushPathNode(editCache.getRootNode());
        } else {
            this.flushPathNode(realNode);
        }
    }

    /**
     * 刷新显示的节点树。在缓存变更后调用以更新 UI。
     */
    public void refreshDisplay() {
        if (ParaEditCache.hasCache()) {
            ParaEditCache editCache = ParaEditCache.getCache();
            //noinspection DataFlowIssue
            this.flushPathNode(editCache.getRootNode());
        } else if (this.realRootPathNode != null) {
            this.flushPathNode(this.realRootPathNode);
        }
        this.reLayoutPathNode();
    }

    public boolean canCreateCacheFromSeedRoot() {
        return ParaEditCacheRules.canCreateCache(this.cacheSeedRootPathNode != null, ParaEditCache.getCache());
    }

    public boolean canRebuildCacheFromSeedRoot() {
        return ParaEditCacheRules.canRebuildCache(this.cacheSeedRootPathNode != null, ParaEditCache.getCache());
    }

    public boolean canSubmitCurrentCache() {
        ParaEditCache cache = ParaEditCache.getCache();
        return cache != null && ParaEditSubmitController.canSubmit(cache);
    }

    public void createCacheFromSeedRoot() {
        if (!this.canCreateCacheFromSeedRoot()) {
            return;
        }
        this.clearCurrentSelection();
        //noinspection DataFlowIssue
        ParaEditCache.createCache(this.cacheSeedRootPathNode);
        this.refreshDisplay();
    }

    public void rebuildCacheFromSeedRoot() {
        if (!this.canRebuildCacheFromSeedRoot()) {
            return;
        }
        this.clearCurrentSelection();
        //noinspection DataFlowIssue
        ParaEditCache.createCache(this.cacheSeedRootPathNode);
        this.refreshDisplay();
    }

    public void submitCurrentCache() {
        ParaEditCache cache = ParaEditCache.getCache();
        if (cache == null) {
            return;
        }
        ParaEditSubmitController.submit(cache);
    }

    private void handleReleaseOnPathNode(UIEventContext<MouseRelease> context) {
        MouseRelease event = context.event;
        if (event.button != MouseButton.RIGHT.code) {
            return;
        }
        this.createRightClickMenu(context.manager, (int) event.mouseX, (int) event.mouseY);
        context.consume();
    }

    private void createRightClickMenu(UIManager manager, float mouseX, float mouseY) {
        EditContextMenu menu = new EditContextMenu(mouseX, mouseY, this);
        manager.displayContextMenu(menu);
    }

    private void clearCurrentSelection() {
        if (this.currentSelectedNode != null) {
            this.currentSelectedNode.setSelected(false);
            this.currentSelectedNode = null;
        }
        this.sideBar.closeParaEditWindow();
    }


    private void handlePathNodeClicked(UIEventContext<MouseClick> context) {
        if (!(context.targetNode instanceof ParaPathNode pathNode)) {
            return;
        }
        if (this.currentSelectedNode != null) {
            this.currentSelectedNode.setSelected(false);
        }
        pathNode.setSelected(true);
        this.currentSelectedNode = pathNode;
        this.sideBar.changeEditStruct(pathNode);
    }

    private void flushPathNode(@Nonnull ParaPathNode pathNode) {
        if (this.rootPathNode != null) {
            this.removeChild(this.rootPathNode);
        }
        this.rootPathNode = pathNode;
        this.addChild(pathNode);
        this.rootPathNode.localRect.setXY(0.0f, rootPathNodeOffsetY);
        this.reLayoutPathNode();

        pathNode.addListener(AllUIEvents.MOUSE_CLICK, EventPhase.BUBBLING, this::handlePathNodeClicked);
        pathNode.addListener(AllUIEvents.MOUSE_RELEASE, EventPhase.BUBBLING, this::handleReleaseOnPathNode);

        pathNode.addListener(AllUIEvents.MOUSE_DOUBLE_CLICK, EventPhase.BUBBLING, context -> this.reLayoutPathNode());
        pathNode.addListener(AllUIEvents.WHEEL, EventPhase.BUBBLING, this::onMouseScroll);
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
            ParaPathNode childNode = new ParaPathNode(child);
            parentNode.addChild(childNode);
            this.buildComponentNode(child, childNode);
        }
//        for (int i = 0; i < 10; i++) {
//            ParaPathNode childNode = new ParaPathNode(Component.literal("test" + i));
//            parentNode.addChild(childNode);
//        }
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
        ParaEditCache.clearCache();
        this.clearCurrentSelection();
        ItemStack item = slot.getItem();
        if (!(item.getItem() instanceof ParaCrystalItem)) {
            this.realRootPathNode = null;
            this.cacheSeedRootPathNode = null;
            this.removePathNode();
            return;
        }
        if (!mainUI.getContainerNode().isItemStack(item)) {
            this.realRootPathNode = null;
            this.cacheSeedRootPathNode = null;
            this.removePathNode();
            return;
        }
        ParaCrystalData crystalData = CrystalComponentUtils.getComponentFromItemStack(item);
        if (crystalData == null) {
            this.realRootPathNode = null;
            this.cacheSeedRootPathNode = this.createDefaultCacheSeedRootNode();
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

    @Nonnull
    private ParaPathNode createDefaultCacheSeedRootNode() {
        ParaPathNode rootNode = new ParaPathNode(Component.literal(ParaData.PARENT_ID));
        rootNode.getStruct().setComponentType(ParaComponentType.VOID.ID());
        return rootNode;
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
        public static final Component STATUS_NO_CACHE =
                Component.translatable("gui.paramagic.spell_edit_table.header_status.no_cache");
        public static final Component STATUS_UNSUBMITTED =
                Component.translatable("gui.paramagic.spell_edit_table.header_status.unsubmitted");
        public static final Component STATUS_PENDING =
                Component.translatable("gui.paramagic.spell_edit_table.header_status.pending");
        public static final Component STATUS_SUBMITTED =
                Component.translatable("gui.paramagic.spell_edit_table.header_status.submitted");

        @Nonnull
        private final ParaStructEditNode owner;
        private final UILabel label;
        private final UILabel statusLabel;

        public StructHeader(@Nonnull ParaStructEditNode owner) {
            this.owner = owner;
            this.label = new UILabel(Component.translatable("gui.paramagic.spell_edit_table.para_struct"));
            this.label.getLayoutParams().center();
            this.label.setHitTestable(false);
            this.statusLabel = new UILabel(STATUS_NO_CACHE);
            this.statusLabel.setHitTestable(false);
            this.statusLabel.setColor(UIColor.of(255, 231, 136, 255));
            this.addChild(this.label);
            this.addChild(this.statusLabel);
        }

        @Override
        protected void render(@Nonnull UIRenderContext context) {
            this.syncStatusLabelLayout();
        }

        @Override
        protected void onMouseRelease(UIEventContext<MouseRelease> context) {
            MouseRelease event = context.event;
            if (event.button != MouseButton.RIGHT.code) {
                return;
            }
            context.manager.displayContextMenu(new ParaStructHeaderContextMenu(event.mouseX, event.mouseY, this.owner));
            context.consumeAndStopPropagation();
        }

        @Override
        public void layout(float parentX, float parentY, float parentW, float parentH) {
            super.layout(parentX, parentY, parentW, parentH);
            this.syncStatusLabelLayout();
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

        @Nonnull
        private Component getStatusText() {
            ParaEditCacheState state = ParaEditCacheRules.resolveState(ParaEditCache.getCache());
            return switch (state) {
                case NO_CACHE -> STATUS_NO_CACHE;
                case PENDING_CONFIRMATION -> STATUS_PENDING;
                case SUBMITTED -> STATUS_SUBMITTED;
                case UNSUBMITTED -> STATUS_UNSUBMITTED;
            };
        }

        private void syncStatusLabelLayout() {
            this.statusLabel.setLabel(this.getStatusText());
            this.statusLabel.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
            float statusX = Math.max(4.0f, this.localRect.w - this.statusLabel.localRect.w - 6.0f);
            float statusY = Math.max(0.0f, (this.localRect.h - this.statusLabel.localRect.h) / 2.0f);
            this.statusLabel.localRect.setXY(statusX, statusY);
            this.statusLabel.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
        }
    }
}
