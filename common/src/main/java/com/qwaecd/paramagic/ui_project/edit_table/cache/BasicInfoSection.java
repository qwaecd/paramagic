package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.data.para.AllParaComponentData;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseLeave;
import com.qwaecd.paramagic.ui.event.impl.MouseOver;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.UIButton;
import com.qwaecd.paramagic.ui.widget.ContextMenu;
import com.qwaecd.paramagic.ui.widget.node.TypingBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public class BasicInfoSection extends EditSection {
    private final UILabel nameLabel;
    private final TypingBox nameBox;
    private final UILabel typeLabel;
    private final TypePickerButton typeButton;
    private final Runnable onTypeChanged;

    public BasicInfoSection(@Nonnull Runnable onTypeChanged) {
        this.onTypeChanged = onTypeChanged;
        this.nameLabel = createLabel(LabelTexts.nameLabelText);
        this.nameBox = createInputBox(128);
        this.typeLabel = createLabel(LabelTexts.typeLabelText);
        this.typeButton = new TypePickerButton(this::openTypeMenu);
        this.addChild(this.typeButton);

        this.nameBox.setFocusChangeListener(focused -> {
            if (!focused && this.struct != null) {
                String validated = validateStringInput(this.nameBox.getText());
                String newName = validated.isEmpty() ? null : validated;
                String oldName = this.struct.getName();
                this.struct.setName(newName);
                this.nameBox.setText(validated);
                if (!Objects.equals(oldName, this.struct.getName())) {
                    markCacheDirty();
                }
            }
        });
    }

    @Override
    void layoutContent(Font font, float contentW) {
        float textVOffset = (INPUT_HEIGHT - font.lineHeight) / 2.0f;
        float nameLabelW = font.width(LabelTexts.nameLabelText) + 2;
        float typeLabelW = font.width(LabelTexts.typeLabelText) + 2;

        float y = 0.0f;
        this.nameLabel.localRect.setXY(0, y + textVOffset);
        this.nameBox.localRect.set(nameLabelW, y, contentW - nameLabelW, INPUT_HEIGHT);
        y += INPUT_HEIGHT + ROW_GAP;

        this.typeLabel.localRect.setXY(0, y + textVOffset);
        this.typeButton.localRect.set(typeLabelW, y, contentW - typeLabelW, INPUT_HEIGHT);

        this.localRect.h = y + INPUT_HEIGHT;
    }

    @Override
    void syncFromStruct() {
        if (this.struct == null) return;
        String name = this.struct.getName();
        this.nameBox.setText(name != null ? name : "");
        AllParaComponentData.Entry entry = AllParaComponentData.get(this.struct.getComponentType());
        if (entry != null) {
            this.typeButton.setText(entry.getDisplayName());
        } else {
            this.typeButton.setText(Component.literal(this.struct.getTypeName()));
        }
    }

    private void openTypeMenu(@Nonnull UIEventContext<MouseRelease> context) {
        if (this.struct == null) {
            return;
        }
        float menuX = this.typeButton.getWorldRect().x;
        float menuY = this.typeButton.getWorldRect().y + this.typeButton.getWorldRect().h;
        context.getManager().displayContextMenu(new TypeSelectMenu(
                menuX,
                menuY,
                this.struct.getComponentType(),
                selectedType -> {
                    if (this.struct == null || this.struct.getComponentType() == selectedType) {
                        return;
                    }
                    this.struct.setComponentType(selectedType);
                    markCacheDirty();
                    this.onTypeChanged.run();
                }
        ));
    }

    private static final class TypePickerButton extends UIButton {
        @Nullable
        private Component text;
        @Nonnull
        private final Consumer<UIEventContext<MouseRelease>> releaseAction;

        private TypePickerButton(@Nonnull Consumer<UIEventContext<MouseRelease>> releaseAction) {
            this.releaseAction = releaseAction;
        }

        void setText(@Nonnull Component text) {
            this.text = text;
        }

        @Override
        protected void onMouseRelease(UIEventContext<MouseRelease> context) {
            boolean trigger = this.pressed && this.hitTest((float) context.event.mouseX, (float) context.event.mouseY);
            super.onMouseRelease(context);
            if (trigger) {
                this.releaseAction.accept(context);
            }
        }

        @Override
        public void render(@Nonnull UIRenderContext context) {
            super.render(context);
            if (this.text != null) {
                float textX = this.worldRect.x + 4.0f;
                float textY = this.worldRect.y + (this.worldRect.h - context.getLineHeight()) / 2.0f;
                context.drawText(this.text, textX, textY, UIColor.WHITE);
            }
        }
    }

    private static final class TypeSelectMenu extends ContextMenu {
        private static final float MIN_MENU_WIDTH = 48.0f;
        @Nonnull
        private final Collection<AllParaComponentData.Entry> entries;

        private TypeSelectMenu(float x, float y, int currentTypeId, @Nonnull Consumer<Integer> selectAction) {
            this.localRect.setXY(x, y);
            this.localRect.w = MIN_MENU_WIDTH;
            this.entries = AllParaComponentData.getEntriesView();
            for (AllParaComponentData.Entry entry : this.entries) {
                this.addChild(new TypeOption(entry, entry.getComponentType() == currentTypeId, selectAction));
            }
        }

        @Override
        public void layout(float parentX, float parentY, float parentW, float parentH) {
            float childY = 4.0f;
            float maxChildW = MIN_MENU_WIDTH;
            for (UINode child : this.children) {
                if (child instanceof TypeOption option) {
                    option.localRect.setXY(8.0f, childY);
                    childY += option.localRect.h + 2.0f;
                    maxChildW = Math.max(maxChildW, option.localRect.w + 16.0f);
                }
            }
            this.localRect.setWH(maxChildW, childY + 2.0f);
            super.layout(parentX, parentY, parentW, parentH);
        }

        @Override
        public void cancel() {
        }
    }

    private static final class TypeOption extends UINode {
        @Nonnull
        private final AllParaComponentData.Entry entry;
        private final boolean selected;
        @Nonnull
        private final Consumer<Integer> selectAction;
        private boolean hovered = false;

        private TypeOption(
                @Nonnull AllParaComponentData.Entry entry,
                boolean selected,
                @Nonnull Consumer<Integer> selectAction
        ) {
            this.entry = entry;
            this.selected = selected;
            this.selectAction = selectAction;
            Font font = Minecraft.getInstance().font;
            this.localRect.setWH(font.width(this.entry.getDisplayName()), font.lineHeight);
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
            this.selectAction.accept(this.entry.getComponentType());
        }

        @Override
        protected void render(@Nonnull UIRenderContext context) {
            if (!this.isVisible()) {
                return;
            }
            UIColor color;
            if (this.selected) {
                color = UIColor.GREEN;
            } else if (this.hovered) {
                color = UIColor.of(255, 231, 136, 255);
            } else {
                color = UIColor.WHITE;
            }
            float textY = this.worldRect.y + (this.worldRect.h - context.getLineHeight()) / 2.0f;
            context.drawText(this.entry.getDisplayName(), this.worldRect.x, textY, color);
        }
    }
}
