package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.ui.api.TooltipContent;
import com.qwaecd.paramagic.ui.api.TooltipQuery;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HiddenSubTreeNode extends UINode {
    private static final TooltipContent tooltip = TooltipContent.of(
            Component.translatable("gui.paramagic.spell_edit_table.para_tree.tooltip.expand_sub_path")
                    .withStyle(ChatFormatting.GRAY)
    );

    public HiddenSubTreeNode(TreeNode parent) {
        super(parent);
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        return MeasureResult.of(WEAssets.HIDDEN_NODE.width, WEAssets.HIDDEN_NODE.height);
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
        if (this.getParent() instanceof TreeNode parent) {
            context.renderSpriteWithAlpha(
                    WEAssets.HIDDEN_NODE,
                    this.presentationRect.x,
                    this.presentationRect.y,
                    parent.getEffectiveRenderAlpha()
            );
        }
    }

    @Override
    public @Nullable TooltipContent getTooltip(@NotNull TooltipQuery query) {
        return tooltip;
    }
}
