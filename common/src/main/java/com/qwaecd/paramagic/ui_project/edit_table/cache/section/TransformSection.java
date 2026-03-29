package com.qwaecd.paramagic.ui_project.edit_table.cache.section;

import com.qwaecd.paramagic.ui_project.edit_table.cache.LabelTexts;
import com.qwaecd.paramagic.ui_project.edit_table.cache.label.EulerRotationRow;
import com.qwaecd.paramagic.ui_project.edit_table.cache.label.LabeledVecRow;
import com.qwaecd.paramagic.ui_project.edit_table.util.EditRotationAngleHelper;
import net.minecraft.client.gui.Font;
import org.joml.Quaternionf;

public class TransformSection extends EditSection {
    private final LabeledVecRow positionRow;
    private final EulerRotationRow rotationRow;
    private final LabeledVecRow scaleRow;
    private final Quaternionf tempRotation = new Quaternionf();

    public TransformSection() {
        this.positionRow = new LabeledVecRow(LabelTexts.positionRowText, "x:", "y:", "z:");
        this.addChild(this.positionRow);
        this.positionRow.bind(0,
                () -> this.struct != null ? this.struct.getPosition().x : 0,
                v -> { if (this.struct != null) this.struct.getPosition().x = v; });
        this.positionRow.bind(1,
                () -> this.struct != null ? this.struct.getPosition().y : 0,
                v -> { if (this.struct != null) this.struct.getPosition().y = v; });
        this.positionRow.bind(2,
                () -> this.struct != null ? this.struct.getPosition().z : 0,
                v -> { if (this.struct != null) this.struct.getPosition().z = v; });

        this.rotationRow = new EulerRotationRow(LabelTexts.rotationDegreesRowText);
        this.addChild(this.rotationRow);
        this.rotationRow.bind(
                () -> this.struct != null ? this.struct.getRotation() : this.tempRotation.identity(),
                (xDeg, yDeg, zDeg) -> {
                    if (this.struct != null) {
                        EditRotationAngleHelper.eulerDegreesToQuaternion(xDeg, yDeg, zDeg, this.struct.getRotation());
                    }
                }
        );

        this.scaleRow = new LabeledVecRow(LabelTexts.scaleRowText, "x:", "y:", "z:");
        this.addChild(this.scaleRow);
        this.scaleRow.bind(0,
                () -> this.struct != null ? this.struct.getScale().x : 1.0f,
                v -> { if (this.struct != null) this.struct.getScale().x = v; });
        this.scaleRow.bind(1,
                () -> this.struct != null ? this.struct.getScale().y : 1.0f,
                v -> { if (this.struct != null) this.struct.getScale().y = v; });
        this.scaleRow.bind(2,
                () -> this.struct != null ? this.struct.getScale().z : 1.0f,
                v -> { if (this.struct != null) this.struct.getScale().z = v; });
    }

    @Override
    public void layoutContent(Font font, float contentW) {
        float y = 0;

        this.positionRow.localRect.set(0, y, contentW, 0);
        this.positionRow.layoutContent(font, contentW);
        y += this.positionRow.localRect.h + ROW_GAP;

        this.rotationRow.localRect.set(0, y, contentW, 0);
        this.rotationRow.layoutContent(font, contentW);
        y += this.rotationRow.localRect.h + ROW_GAP;

        this.scaleRow.localRect.set(0, y, contentW, 0);
        this.scaleRow.layoutContent(font, contentW);
        y += this.scaleRow.localRect.h;

        this.localRect.h = y;
    }

    @Override
    public void syncFromStruct() {
        if (this.struct == null) return;
        this.positionRow.sync(
                this.struct.getPosition().x,
                this.struct.getPosition().y,
                this.struct.getPosition().z);
        this.rotationRow.syncFromQuaternion(this.struct.getRotation());
        this.scaleRow.sync(
                this.struct.getScale().x,
                this.struct.getScale().y,
                this.struct.getScale().z);
    }
}
