package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.node.TypingBox;
import net.minecraft.client.gui.Font;

/**
 * A row control that displays a title label on one line
 * followed by a single full-width integer input box on the next line.
 */
class LabeledIntRow extends UINode {
    private static final float INPUT_HEIGHT = EditSection.INPUT_HEIGHT;
    private static final float LABEL_HEIGHT = EditSection.LABEL_HEIGHT;

    private final UILabel titleLabel;
    private final TypingBox box;

    LabeledIntRow(String title, int maxLength) {
        this.setHitTestable(false);

        this.titleLabel = new UILabel(title);
        this.titleLabel.setHitTestable(false);
        this.addChild(this.titleLabel);

        this.box = new TypingBox();
        this.box.localRect.set(0, 0, 0, INPUT_HEIGHT);
        this.box.setMaxLength(maxLength);
        this.addChild(this.box);
    }

    void bind(EditSection.IntSupplier getter, EditSection.IntConsumer setter) {
        this.box.setFocusChangeListener(focused -> {
            if (focused) return;
            String text = this.box.getText();
            try {
                int value = Integer.parseInt(text);
                setter.accept(value);
            } catch (NumberFormatException e) {
                this.box.setText(String.valueOf(getter.get()));
            }
        });
    }

    void layoutContent(Font font, float contentW) {
        float y = 0;
        this.titleLabel.localRect.setXY(0, y);
        y += LABEL_HEIGHT + 1;
        this.box.localRect.set(0, y, contentW, INPUT_HEIGHT);
        y += INPUT_HEIGHT;
        this.localRect.h = y;
    }

    void sync(int value) {
        this.box.setText(String.valueOf(value));
    }
}
