package com.qwaecd.paramagic.ui_project.edit_table.cache.label;

import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui_project.edit_table.cache.section.EditSection;
import com.qwaecd.paramagic.ui_project.edit_table.util.EditInputRules;
import com.qwaecd.paramagic.ui_project.edit_table.util.EditNumericInputSupport;
import com.qwaecd.paramagic.ui_project.edit_table.util.EditRotationAngleHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class EulerRotationRow extends UINode {
    private static final float INPUT_HEIGHT = EditSection.INPUT_HEIGHT;
    private static final float LABEL_HEIGHT = EditSection.LABEL_HEIGHT;
    private static final float COMPONENT_GAP = EditSection.COMPONENT_GAP;

    private final UILabel titleLabel;
    private final UILabel[] axisLabels;
    private final NumericTypingBox[] boxes;
    private final Vector3f displayEulerDegrees = new Vector3f();
    private final Quaternionf oldRotation = new Quaternionf();

    public EulerRotationRow(Component title) {
        this.setHitTestable(false);

        this.titleLabel = new UILabel(title);
        this.titleLabel.setHitTestable(false);
        this.addChild(this.titleLabel);

        this.axisLabels = new UILabel[3];
        this.boxes = new NumericTypingBox[3];
        String[] labelTexts = {"x:", "y:", "z:"};

        for (int i = 0; i < this.boxes.length; i++) {
            UILabel label = new UILabel(labelTexts[i]);
            label.setHitTestable(false);
            this.addChild(label);
            this.axisLabels[i] = label;

            NumericTypingBox box = new NumericTypingBox();
            box.localRect.set(0, 0, 0, INPUT_HEIGHT);
            box.setMaxLength(16);
            this.addChild(box);
            this.boxes[i] = box;
        }
    }

    public void bind(@Nonnull RotationGetter getter, @Nonnull RotationSetter setter) {
        for (int i = 0; i < this.boxes.length; i++) {
            int axis = i;
            NumericTypingBox box = this.boxes[i];
            box.setFocusChangeListener(focused -> {
                if (focused) {
                    return;
                }
                try {
                    this.setAxisValue(axis, EditInputRules.parseFiniteFloat(box.getText()));
                    this.commitDisplayAngles(getter, setter);
                } catch (NumberFormatException e) {
                    this.refreshDisplayTexts();
                }
            });
            box.setWheelHandler((numericBox, scrollDelta) -> {
                int direction = EditNumericInputSupport.getScrollDirection(scrollDelta);
                if (direction == 0) {
                    return false;
                }

                this.setAxisValue(axis, this.getAxisValue(axis) + direction * EditNumericInputSupport.getFloatStep());
                this.commitDisplayAngles(getter, setter);
                return true;
            });
        }
    }

    public void syncFromQuaternion(@Nonnull Quaternionf rotation) {
        EditRotationAngleHelper.quaternionToEulerDegrees(rotation, this.displayEulerDegrees);
        this.refreshDisplayTexts();
    }

    public void layoutContent(Font font, float contentW) {
        int compLabelW = font.width("z:") + 1;
        float textVOffset = (INPUT_HEIGHT - font.lineHeight) / 2.0f;
        float y = 0;

        this.titleLabel.localRect.setXY(0, y);
        y += LABEL_HEIGHT + 1;

        int count = this.boxes.length;
        float boxW = (contentW - count * compLabelW - (count - 1) * COMPONENT_GAP) / count;
        float x = 0;
        for (int i = 0; i < count; i++) {
            this.axisLabels[i].localRect.setXY(x, y + textVOffset);
            this.boxes[i].localRect.set(x + compLabelW, y, boxW, INPUT_HEIGHT);
            x += compLabelW + boxW + COMPONENT_GAP;
        }
        y += INPUT_HEIGHT;

        this.localRect.h = y;
    }

    private void commitDisplayAngles(@Nonnull RotationGetter getter, @Nonnull RotationSetter setter) {
        this.oldRotation.set(getter.getRotation());
        setter.setDegrees(this.displayEulerDegrees.x, this.displayEulerDegrees.y, this.displayEulerDegrees.z);
        this.refreshDisplayTexts();

        Quaternionf currentRotation = getter.getRotation();
        if (!sameQuaternion(this.oldRotation, currentRotation)) {
            EditSection.markCacheDirty();
        }
    }

    private void refreshDisplayTexts() {
        this.boxes[0].setText(EditNumericInputSupport.formatFloat(this.displayEulerDegrees.x));
        this.boxes[1].setText(EditNumericInputSupport.formatFloat(this.displayEulerDegrees.y));
        this.boxes[2].setText(EditNumericInputSupport.formatFloat(this.displayEulerDegrees.z));
    }

    private float getAxisValue(int axis) {
        return switch (axis) {
            case 0 -> this.displayEulerDegrees.x;
            case 1 -> this.displayEulerDegrees.y;
            default -> this.displayEulerDegrees.z;
        };
    }

    private void setAxisValue(int axis, float value) {
        switch (axis) {
            case 0 -> this.displayEulerDegrees.x = value;
            case 1 -> this.displayEulerDegrees.y = value;
            case 2 -> this.displayEulerDegrees.z = value;
            default -> throw new IllegalArgumentException("Invalid axis index: " + axis);
        }
    }

    private static boolean sameQuaternion(@Nonnull Quaternionf left, @Nonnull Quaternionf right) {
        return Float.compare(left.x, right.x) == 0
                && Float.compare(left.y, right.y) == 0
                && Float.compare(left.z, right.z) == 0
                && Float.compare(left.w, right.w) == 0;
    }

    @FunctionalInterface
    public interface RotationGetter {
        Quaternionf getRotation();
    }

    @FunctionalInterface
    public interface RotationSetter {
        void setDegrees(float xDeg, float yDeg, float zDeg);
    }
}
