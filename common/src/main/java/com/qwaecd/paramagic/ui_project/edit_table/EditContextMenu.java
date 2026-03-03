package com.qwaecd.paramagic.ui_project.edit_table;

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
import java.util.function.Consumer;

public class EditContextMenu extends ContextMenu {
    private static final float minMenuWidth = 30.0f;
    public EditContextMenu(double mouseX, double mouseY) {
        this.localRect.setXY((float) mouseX, (float) mouseY);
        this.localRect.w = minMenuWidth;

        this.addChild(new Content(
                Component.translatable("gui.paramagic.spell_edit_table.context_menu.add_path"),
                this::addPathAction
        ));
        this.addChild(new Content(
                Component.translatable("gui.paramagic.spell_edit_table.context_menu.remove_path"),
                this::removePathAction
        ));
        this.addChild(new Content(
                Component.translatable("gui.paramagic.spell_edit_table.context_menu.open_window"),
                this::openWindowAction
        ));
    }

    private void addPathAction(UIEventContext<MouseClick> context) {
        context.manager.cancelContextMenu();
        context.consumeAndStopPropagation();
    }

    private void removePathAction(UIEventContext<MouseClick> context) {
        context.manager.cancelContextMenu();
        context.consumeAndStopPropagation();
    }

    private void openWindowAction(UIEventContext<MouseClick> context) {
        context.manager.cancelContextMenu();
        context.consumeAndStopPropagation();
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
            context.renderOutline(this.worldRect, UIColor.RED);
        }
    }
}
