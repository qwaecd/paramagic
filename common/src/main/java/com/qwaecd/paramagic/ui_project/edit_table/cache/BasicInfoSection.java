package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.node.TypingBox;
import net.minecraft.client.gui.Font;

class BasicInfoSection extends EditSection {
    private final UILabel nameLabel;
    private final TypingBox nameBox;

    BasicInfoSection() {
        this.nameLabel = createLabel("Name:");
        this.nameBox = createInputBox(128);

        this.nameBox.setFocusChangeListener(focused -> {
            if (!focused && this.struct != null) {
                String text = this.nameBox.getText();
                this.struct.setName(text.isEmpty() ? null : text);
            }
        });
    }

    @Override
    void layoutContent(Font font, float contentW) {
        float textVOffset = (INPUT_HEIGHT - font.lineHeight) / 2.0f;
        float nameLabelW = font.width("Name:") + 2;

        this.nameLabel.localRect.setXY(0, textVOffset);
        this.nameBox.localRect.set(nameLabelW, 0, contentW - nameLabelW, INPUT_HEIGHT);

        this.localRect.h = INPUT_HEIGHT;
    }

    @Override
    void syncFromStruct() {
        if (this.struct == null) return;
        String name = this.struct.getName();
        this.nameBox.setText(name != null ? name : "");
    }
}
