package com.qwaecd.paramagic.ui_project.edit_table.cache.label;

import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui_project.edit_table.cache.section.EditSection;
import com.qwaecd.paramagic.ui_project.edit_table.util.EditInputRules;
import com.qwaecd.paramagic.ui_project.edit_table.util.EditNumericInputSupport;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.function.IntUnaryOperator;

/**
 * A row control that displays a title label on one line
 * followed by a single full-width integer input box on the next line.
 */
public class LabeledIntRow extends UINode {
    private static final float INPUT_HEIGHT = EditSection.INPUT_HEIGHT;
    private static final float LABEL_HEIGHT = EditSection.LABEL_HEIGHT;

    private final UILabel titleLabel;
    private final NumericTypingBox box;

    public LabeledIntRow(Component title, int maxLength) {
        this.setHitTestable(false);

        this.titleLabel = new UILabel(title);
        this.titleLabel.setHitTestable(false);
        this.addChild(this.titleLabel);

        this.box = new NumericTypingBox();
        this.box.localRect.set(0, 0, 0, INPUT_HEIGHT);
        this.box.setMaxLength(maxLength);
        this.addChild(this.box);
    }

    public void bind(EditSection.IntSupplier getter, EditSection.IntConsumer setter) {
        this.bind(getter, setter, value -> value);
    }

    public void bind(EditSection.IntSupplier getter, EditSection.IntConsumer setter, IntUnaryOperator validator) {
        this.box.setFocusChangeListener(focused -> {
            if (focused) return;
            int oldValue = getter.get();
            try {
                int value = EditInputRules.parseClampedInt(this.box.getText());
                setter.accept(validator.applyAsInt(value));
                int currentValue = getter.get();
                this.box.setText(String.valueOf(currentValue));
                if (oldValue != currentValue) {
                    EditSection.markCacheDirty();
                }
            } catch (NumberFormatException e) {
                this.box.setText(String.valueOf(oldValue));
            }
        });
        this.box.setWheelHandler((box, scrollDelta) -> {
            int direction = EditNumericInputSupport.getScrollDirection(scrollDelta);
            if (direction == 0) {
                return false;
            }

            int oldValue = getter.get();
            setter.accept(validator.applyAsInt(oldValue + direction * EditNumericInputSupport.getIntStep()));
            int currentValue = getter.get();
            box.setText(String.valueOf(currentValue));
            if (oldValue != currentValue) {
                EditSection.markCacheDirty();
            }
            return true;
        });
    }

    public void layoutContent(Font font, float contentW) {
        float y = 0;
        this.titleLabel.localRect.setXY(0, y);
        y += LABEL_HEIGHT + 1;
        this.box.localRect.set(0, y, contentW, INPUT_HEIGHT);
        y += INPUT_HEIGHT;
        this.localRect.h = y;
    }

    public void sync(int value) {
        this.box.setText(String.valueOf(value));
    }
}
