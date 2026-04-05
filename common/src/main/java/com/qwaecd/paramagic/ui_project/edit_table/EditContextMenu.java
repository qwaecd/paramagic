package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.data.para.struct.ParaComponentType;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseLeave;
import com.qwaecd.paramagic.ui.event.impl.MouseOver;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.ContextMenu;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaEditCache;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaStruct;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaStructEditWindow;
import com.qwaecd.paramagic.ui_project.edit_table.leftside.NodeIndexPath;
import com.qwaecd.paramagic.ui_project.edit_table.leftside.ParaPathNode;
import com.qwaecd.paramagic.ui_project.edit_table.leftside.ParaStructEditNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EditContextMenu extends ContextMenu {
    private static final float minMenuWidth = 30.0f;

    @Nonnull
    private final ParaStructEditNode structEditNode;

    public EditContextMenu(double mouseX, double mouseY, @Nonnull ParaStructEditNode structEditNode) {
        this.localRect.setXY((float) mouseX, (float) mouseY);
        this.localRect.w = minMenuWidth;
        this.structEditNode = structEditNode;

        // 添加参量路径
        this.addChild(new Content(
                Component.translatable("gui.paramagic.spell_edit_table.context_menu.add_path"),
                this::addPathAction
        ));
        // 移除参量路径
        this.addChild(new Content(
                Component.translatable("gui.paramagic.spell_edit_table.context_menu.remove_path"),
                this::removePathAction
        ));
        // 打开编辑窗口
        this.addChild(new Content(
                Component.translatable("gui.paramagic.spell_edit_table.context_menu.open_window"),
                this::openWindowAction
        ));
    }

    private void addPathAction(UIEventContext<MouseClick> context) {
        context.manager.cancelContextMenu();
        context.consumeAndStopPropagation();

        ParaPathNode cachedSelected = this.resolveSelectedCacheNode("addPathAction");
        if (cachedSelected == null) {
            return;
        }

        // 创建默认 VoidPara 类型的新子节点
        ParaPathNode newChild = new ParaPathNode(Component.literal("new"));
        ParaStruct newStruct = newChild.getStruct();
        newStruct.setComponentType(ParaComponentType.VOID.ID());
        cachedSelected.addChildNode(newChild);

        ParaEditCache editCache = ParaEditCache.getCache();
        if (editCache != null) {
            editCache.markDirty();
        }
        this.structEditNode.refreshDisplay();
    }

    private void removePathAction(UIEventContext<MouseClick> context) {
        context.manager.cancelContextMenu();
        context.consumeAndStopPropagation();

        ParaPathNode cachedSelected = this.resolveSelectedCacheNode("removePathAction");
        if (cachedSelected == null) {
            return;
        }

        // 尝试删除根节点时忽略操作（父节点为 null）
        UINode parentNode = cachedSelected.getParent();
        if (!(parentNode instanceof ParaPathNode parentPathNode)) {
            Paramagic.LOG.debug("removePathAction: cannot remove root node, operation ignored.");
            return;
        }

        parentPathNode.removeChildNode(cachedSelected);
        this.structEditNode.notifyRemovePathNode(cachedSelected);
        ParaEditCache editCache = ParaEditCache.getCache();
        if (editCache != null) {
            editCache.markDirty();
        }
        this.structEditNode.refreshDisplay();
    }

    private void openWindowAction(UIEventContext<MouseClick> context) {
        context.manager.cancelContextMenu();
        context.consumeAndStopPropagation();

        ParaPathNode cachedSelected = this.resolveSelectedCacheNode("openWindowAction");
        if (cachedSelected == null) {
            return;
        }

        ParaStructEditWindow window = new ParaStructEditWindow(cachedSelected.getStruct());
        this.structEditNode.createParaEditWindow(window);
    }

    @Nullable
    private ParaPathNode resolveSelectedCacheNode(@Nonnull String actionName) {
        ParaPathNode selectedNode = this.structEditNode.getCurrentSelectedNode();
        if (selectedNode == null) {
            Paramagic.LOG.debug("{}: no node selected, operation ignored.", actionName);
            return null;
        }

        NodeIndexPath path = this.structEditNode.getDisplayedNodePath(selectedNode);
        if (path == null) {
            Paramagic.LOG.debug("{}: selected node path could not be resolved.", actionName);
            return null;
        }

        boolean hasCacheBefore = ParaEditCache.hasCache();
        ParaEditCache editCache = this.structEditNode.ensureEditCacheFromSeedRoot();
        if (editCache == null) {
            Paramagic.LOG.debug("{}: no cache seed root, operation ignored.", actionName);
            return null;
        }

        ParaPathNode cachedSelected = path.resolve(editCache.getRootNode());
        if (cachedSelected == null) {
            Paramagic.LOG.debug("{}: selected node path not found in cache.", actionName);
            return null;
        }

        if (!hasCacheBefore) {
            this.structEditNode.refreshDisplay();
        }
        this.structEditNode.selectPathNode(cachedSelected);
        return cachedSelected;
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        final float textOffsetX = 8.0f;
        final float textGapY = 2.0f;
        float childY = textGapY * 2.0f;
        float maxChildW = minMenuWidth;
        for (UINode child : this.children) {
            if (child instanceof Content content) {
                content.localRect.setXY(textOffsetX, childY);
                childY += content.localRect.h + textGapY;
                maxChildW = Math.max(maxChildW, content.localRect.w + textOffsetX * 2.0f);
            }
        }
        this.localRect.setWH(maxChildW, childY + textGapY);
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    public void cancel() {
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        super.onMouseClick(context);
    }

    static class Content extends UINode {
        private final Consumer<UIEventContext<MouseClick>> clickAction;
        boolean hovered = false;
        final Component text;
        Content(Component text, Consumer<UIEventContext<MouseClick>> clickAction) {
            this.clickAction = clickAction;
            this.text = text;
            Font font = Minecraft.getInstance().font;
            this.localRect.setWH(font.width(this.text), font.lineHeight);
        }

        @Override
        protected void onMouseOver(UIEventContext<MouseOver> context) {
            this.hovered = true;
        }

        @Override
        protected void onMouseLeave(UIEventContext<MouseLeave> context) {
            this.hovered = false;
        }

        @Override
        protected void onMouseClick(UIEventContext<MouseClick> context) {
            this.clickAction.accept(context);
        }

        @Override
        protected void render(@Nonnull UIRenderContext context) {
            if (!isVisible()) {
                return;
            }
            UIColor color;
            if (this.hovered) {
                color = UIColor.of(255, 231, 136, 255);
            } else {
                color = UIColor.WHITE;
            }
            float textY = this.worldRect.y + (this.worldRect.h - context.getLineHeight()) / 2.0f;
            context.drawText(this.text, this.worldRect.x, textY, color);
//            context.renderOutline(this.worldRect, UIColor.RED);
        }
    }
}
