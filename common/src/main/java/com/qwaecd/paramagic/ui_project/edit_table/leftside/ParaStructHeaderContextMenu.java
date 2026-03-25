package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseLeave;
import com.qwaecd.paramagic.ui.event.impl.MouseOver;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.ContextMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

final class ParaStructHeaderContextMenu extends ContextMenu {
    private static final float MIN_MENU_WIDTH = 72.0f;

    ParaStructHeaderContextMenu(double mouseX, double mouseY, @Nonnull ParaStructEditNode structEditNode) {
        this.localRect.setXY((float) mouseX, (float) mouseY);
        this.localRect.w = MIN_MENU_WIDTH;

        this.addChild(new MenuItem(
                Component.translatable("gui.paramagic.spell_edit_table.header_menu.create_cache"),
                structEditNode::canCreateCacheFromSeedRoot,
                context -> structEditNode.createCacheFromSeedRoot()
        ));
        this.addChild(new MenuItem(
                Component.translatable("gui.paramagic.spell_edit_table.header_menu.rebuild_cache"),
                structEditNode::canRebuildCacheFromSeedRoot,
                context -> structEditNode.rebuildCacheFromSeedRoot()
        ));
        this.addChild(new MenuItem(
                Component.translatable("gui.paramagic.spell_edit_table.header_menu.submit_struct"),
                structEditNode::canSubmitCurrentCache,
                context -> structEditNode.submitCurrentCache()
        ));
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        float childY = 4.0f;
        float maxChildW = MIN_MENU_WIDTH;
        for (UINode child : this.children) {
            if (child instanceof MenuItem item) {
                item.localRect.setXY(8.0f, childY);
                childY += item.localRect.h + 2.0f;
                maxChildW = Math.max(maxChildW, item.localRect.w + 16.0f);
            }
        }
        this.localRect.setWH(maxChildW, childY + 2.0f);
        super.layout(parentX, parentY, parentW, parentH);
    }

    private static final class MenuItem extends UINode {
        @Nonnull
        private final Component text;
        @Nonnull
        private final BooleanSupplier enabledSupplier;
        @Nonnull
        private final Consumer<UIEventContext<MouseClick>> action;
        private boolean hovered = false;

        private MenuItem(
                @Nonnull Component text,
                @Nonnull BooleanSupplier enabledSupplier,
                @Nonnull Consumer<UIEventContext<MouseClick>> action
        ) {
            this.text = text;
            this.enabledSupplier = enabledSupplier;
            this.action = action;
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
            context.manager.cancelContextMenu();
            context.consumeAndStopPropagation();
            if (!this.enabledSupplier.getAsBoolean()) {
                return;
            }
            this.action.accept(context);
        }

        @Override
        protected void render(@Nonnull UIRenderContext context) {
            if (!this.isVisible()) {
                return;
            }
            UIColor color;
            if (!this.enabledSupplier.getAsBoolean()) {
                color = UIColor.of(160, 160, 160, 255);
            } else if (this.hovered) {
                color = UIColor.of(255, 231, 136, 255);
            } else {
                color = UIColor.WHITE;
            }
            float textY = this.worldRect.y + (this.worldRect.h - context.getLineHeight()) / 2.0f;
            context.drawText(this.text, this.worldRect.x, textY, color);
        }
    }
}
