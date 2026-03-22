package com.qwaecd.paramagic.ui_project.edit_table.cache;

import net.minecraft.client.gui.Font;

class TransformSection extends EditSection {
    private final LabeledVecRow positionRow;
    private final LabeledVecRow rotationRow;
    private final LabeledVecRow scaleRow;

    TransformSection() {
        this.positionRow = new LabeledVecRow("Position:", "x:", "y:", "z:");
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

        this.rotationRow = new LabeledVecRow("Rotation:", "x:", "y:", "z:", "w:");
        this.addChild(this.rotationRow);
        this.rotationRow.bind(0,
                () -> this.struct != null ? this.struct.getRotation().x : 0,
                v -> { if (this.struct != null) this.struct.getRotation().x = v; });
        this.rotationRow.bind(1,
                () -> this.struct != null ? this.struct.getRotation().y : 0,
                v -> { if (this.struct != null) this.struct.getRotation().y = v; });
        this.rotationRow.bind(2,
                () -> this.struct != null ? this.struct.getRotation().z : 0,
                v -> { if (this.struct != null) this.struct.getRotation().z = v; });
        this.rotationRow.bind(3,
                () -> this.struct != null ? this.struct.getRotation().w : 0,
                v -> { if (this.struct != null) this.struct.getRotation().w = v; });

        this.scaleRow = new LabeledVecRow("Scale:", "x:", "y:", "z:");
        this.addChild(this.scaleRow);
        this.scaleRow.bind(0,
                () -> this.struct != null ? this.struct.getScale().x : 0,
                v -> { if (this.struct != null) this.struct.getScale().x = v; });
        this.scaleRow.bind(1,
                () -> this.struct != null ? this.struct.getScale().y : 0,
                v -> { if (this.struct != null) this.struct.getScale().y = v; });
        this.scaleRow.bind(2,
                () -> this.struct != null ? this.struct.getScale().z : 0,
                v -> { if (this.struct != null) this.struct.getScale().z = v; });
    }

    @Override
    void layoutContent(Font font, float contentW) {
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
    void syncFromStruct() {
        if (this.struct == null) return;
        this.positionRow.sync(
                this.struct.getPosition().x,
                this.struct.getPosition().y,
                this.struct.getPosition().z);
        this.rotationRow.sync(
                this.struct.getRotation().x,
                this.struct.getRotation().y,
                this.struct.getRotation().z,
                this.struct.getRotation().w);
        this.scaleRow.sync(
                this.struct.getScale().x,
                this.struct.getScale().y,
                this.struct.getScale().z);
    }
}
