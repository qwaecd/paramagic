package com.qwaecd.paramagic.ui_project.edit_table.cache.label;

import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui_project.edit_table.cache.section.EditSection;
import com.qwaecd.paramagic.ui_project.edit_table.util.EditInputRules;
import com.qwaecd.paramagic.ui_project.edit_table.util.EditNumericInputSupport;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class LabeledAngleRow extends UINode {
    private static final float INPUT_HEIGHT = EditSection.INPUT_HEIGHT;
    private static final float LABEL_HEIGHT = EditSection.LABEL_HEIGHT;

    private final UILabel titleLabel;
    private final NumericTypingBox box;

    public LabeledAngleRow(Component title, int maxLength) {
        this.setHitTestable(false);

        this.titleLabel = new UILabel(title);
        this.titleLabel.setHitTestable(false);
        this.addChild(this.titleLabel);

        this.box = new NumericTypingBox();
        this.box.localRect.set(0, 0, 0, INPUT_HEIGHT);
        this.box.setMaxLength(maxLength);
        this.addChild(this.box);
    }

    public void bind(EditSection.FloatSupplier radiansGetter, EditSection.FloatConsumer radiansSetter) {
        this.box.setFocusChangeListener(focused -> {
            if (focused) {
                return;
            }
            float oldRadians = radiansGetter.get();
            try {
                float degrees = EditInputRules.parseFiniteFloat(this.box.getText());
                radiansSetter.accept((float) Math.toRadians(degrees));
                float currentRadians = radiansGetter.get();
                this.box.setText(EditNumericInputSupport.formatFloat((float) Math.toDegrees(currentRadians)));
                if (Float.compare(oldRadians, currentRadians) != 0) {
                    EditSection.markCacheDirty();
                }
            } catch (NumberFormatException e) {
                this.box.setText(EditNumericInputSupport.formatFloat((float) Math.toDegrees(oldRadians)));
            }
        });
        this.box.setWheelHandler((box, scrollDelta) -> {
            int direction = EditNumericInputSupport.getScrollDirection(scrollDelta);
            if (direction == 0) {
                return false;
            }

            float oldRadians = radiansGetter.get();
            float oldDegrees = (float) Math.toDegrees(oldRadians);
            radiansSetter.accept((float) Math.toRadians(oldDegrees + direction * EditNumericInputSupport.getFloatStep()));
            float currentRadians = radiansGetter.get();
            box.setText(EditNumericInputSupport.formatFloat((float) Math.toDegrees(currentRadians)));
            if (Float.compare(oldRadians, currentRadians) != 0) {
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

    public void syncRadians(float radians) {
        this.box.setText(EditNumericInputSupport.formatFloat((float) Math.toDegrees(radians)));
    }
}
