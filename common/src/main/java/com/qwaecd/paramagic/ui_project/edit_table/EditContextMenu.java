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

        ParaPathNode selectedNode = this.structEditNode.getCurrentSelectedNode();
        if (selectedNode == null) {
            Paramagic.LOG.debug("addPathAction: no node selected, operation ignored.");
            return;
        }

        ParaPathNode realRoot = this.structEditNode.getRealRootPathNode();
        if (realRoot == null) {
            Paramagic.LOG.debug("addPathAction: no real root node, operation ignored.");
            return;
        }

        boolean hasCacheBefore = ParaEditCache.hasCache();

        ParaEditCache editCache = ParaEditCache.ensureCache(realRoot);

        if (!hasCacheBefore) {
            this.structEditNode.refreshDisplay();
        }

        // 在缓存中找到对应的选中节点并添加子节点
        ParaPathNode cachedSelected = this.findNodeInCache(editCache.getRootNode(), selectedNode);
        if (cachedSelected == null) {
            Paramagic.LOG.debug("addPathAction: selected node not found in cache.");
            return;
        }

        // 创建默认 VoidPara 类型的新子节点
        ParaPathNode newChild = new ParaPathNode(Component.literal("new"));
        ParaStruct newStruct = newChild.getStruct();
        newStruct.setComponentType(ParaComponentType.VOID.ID());
        cachedSelected.addChildNode(newChild);

        editCache.markDirty();
        this.structEditNode.refreshDisplay();
    }

    private void removePathAction(UIEventContext<MouseClick> context) {
        context.manager.cancelContextMenu();
        context.consumeAndStopPropagation();

        ParaPathNode selectedNode = this.structEditNode.getCurrentSelectedNode();
        if (selectedNode == null) {
            Paramagic.LOG.debug("removePathAction: no node selected, operation ignored.");
            return;
        }

        ParaPathNode realRoot = this.structEditNode.getRealRootPathNode();
        if (realRoot == null) {
            Paramagic.LOG.debug("removePathAction: no real root node, operation ignored.");
            return;
        }

        boolean hasCacheBefore = ParaEditCache.hasCache();

        ParaEditCache editCache = ParaEditCache.ensureCache(realRoot);

        if (!hasCacheBefore) {
            this.structEditNode.refreshDisplay();
        }

        // 在缓存中找到对应的选中节点
        ParaPathNode cachedSelected = this.findNodeInCache(editCache.getRootNode(), selectedNode);
        if (cachedSelected == null) {
            Paramagic.LOG.debug("removePathAction: selected node not found in cache.");
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
        editCache.markDirty();
        this.structEditNode.refreshDisplay();
    }

    private void openWindowAction(UIEventContext<MouseClick> context) {
        context.manager.cancelContextMenu();
        context.consumeAndStopPropagation();

        ParaPathNode selectedNode = this.structEditNode.getCurrentSelectedNode();
        if (selectedNode == null) {
            Paramagic.LOG.debug("openWindowAction: no node selected, operation ignored.");
            return;
        }

        ParaPathNode realRoot = this.structEditNode.getRealRootPathNode();
        if (realRoot == null) {
            return;
        }

        boolean hasCacheBefore = ParaEditCache.hasCache();
        ParaEditCache editCache = ParaEditCache.ensureCache(realRoot);

        if (!hasCacheBefore) {
            this.structEditNode.refreshDisplay();
            // 缓存是新创建的，这里选中的依然是原始节点，无法在缓存中找到对应节点，不允许打开编辑窗口
            return;
        }

        ParaPathNode cachedSelected = this.findNodeInCache(editCache.getRootNode(), selectedNode);
        if (cachedSelected == null) {
            return;
        }

        ParaStructEditWindow window = new ParaStructEditWindow(cachedSelected.getStruct());
        this.structEditNode.createParaEditWindow(window);
    }

    /**
     * 在缓存树中查找与原始选中节点对应的节点。
     * 由于缓存是从真实数据深拷贝的，通过路径位置进行匹配。
     */
    @Nullable
    private ParaPathNode findNodeInCache(@Nonnull ParaPathNode cacheRoot, @Nonnull ParaPathNode targetNode) {
        // 如果目标节点就是缓存中的节点（已经在缓存树中），直接返回
        if (cacheRoot == targetNode) {
            return cacheRoot;
        }
        // 递归搜索缓存树
        for (UINode child : cacheRoot.getChildren()) {
            if (child instanceof ParaPathNode childPathNode) {
                if (childPathNode == targetNode) {
                    return childPathNode;
                }
                ParaPathNode found = this.findNodeInCache(childPathNode, targetNode);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
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
