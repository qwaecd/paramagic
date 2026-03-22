package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.node.TypingBox;
import net.minecraft.client.gui.Font;

/**
 * A row control that displays a title label followed by N float input boxes
 * with component labels (e.g. "Position:" then [x:][___][y:][___][z:][___]).
 */
class LabeledVecRow extends UINode {
    private static final float INPUT_HEIGHT = EditSection.INPUT_HEIGHT;
    private static final float LABEL_HEIGHT = EditSection.LABEL_HEIGHT;
    private static final float COMPONENT_GAP = EditSection.COMPONENT_GAP;

    private final UILabel titleLabel;
    private final UILabel[] compLabels;
    private final TypingBox[] boxes;

    LabeledVecRow(String title, String... compLabelTexts) {
        this.setHitTestable(false);

        this.titleLabel = new UILabel(title);
        this.titleLabel.setHitTestable(false);
        this.addChild(this.titleLabel);

        int count = compLabelTexts.length;
        this.compLabels = new UILabel[count];
        this.boxes = new TypingBox[count];

        for (int i = 0; i < count; i++) {
            UILabel label = new UILabel(compLabelTexts[i]);
            label.setHitTestable(false);
            this.addChild(label);
            this.compLabels[i] = label;

            TypingBox box = new TypingBox();
            box.localRect.set(0, 0, 0, INPUT_HEIGHT);
            box.setMaxLength(16);
            this.addChild(box);
            this.boxes[i] = box;
        }
    }

    void bind(int index, EditSection.FloatSupplier getter, EditSection.FloatConsumer setter) {
        TypingBox box = this.boxes[index];
        box.setFocusChangeListener(focused -> {
            if (focused) return;
            String text = box.getText();
            try {
                float value = Float.parseFloat(text);
                if (Float.isFinite(value)) {
                    setter.accept(value);
                } else {
                    box.setText(String.valueOf(getter.get()));
                }
            } catch (NumberFormatException e) {
                box.setText(String.valueOf(getter.get()));
            }
        });
    }

    void layoutContent(Font font, float contentW) {
        int compLabelW = font.width("w:") + 1;
        float textVOffset = (INPUT_HEIGHT - font.lineHeight) / 2.0f;
        float y = 0;

        this.titleLabel.localRect.setXY(0, y);
        y += LABEL_HEIGHT + 1;

        int count = this.boxes.length;
        float boxW = (contentW - count * compLabelW - (count - 1) * COMPONENT_GAP) / count;
        float x = 0;
        for (int i = 0; i < count; i++) {
            this.compLabels[i].localRect.setXY(x, y + textVOffset);
            this.boxes[i].localRect.set(x + compLabelW, y, boxW, INPUT_HEIGHT);
            x += compLabelW + boxW + COMPONENT_GAP;
        }
        y += INPUT_HEIGHT;

        this.localRect.h = y;
    }

    void sync(float... values) {
        for (int i = 0; i < values.length && i < this.boxes.length; i++) {
            this.boxes[i].setText(String.valueOf(values[i]));
        }
    }
}
