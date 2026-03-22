package com.qwaecd.paramagic.ui_project.edit_table.cache;

import net.minecraft.client.gui.Font;

class MaterialSection extends EditSection {
    private final LabeledVecRow colorRow;
    private final LabeledFloatRow intensityRow;

    MaterialSection() {
        this.colorRow = new LabeledVecRow("Color:", "r:", "g:", "b:", "a:");
        this.addChild(this.colorRow);
        this.colorRow.bind(0,
                () -> this.struct != null ? this.struct.getColor().x : 0,
                v -> { if (this.struct != null) this.struct.getColor().x = v; });
        this.colorRow.bind(1,
                () -> this.struct != null ? this.struct.getColor().y : 0,
                v -> { if (this.struct != null) this.struct.getColor().y = v; });
        this.colorRow.bind(2,
                () -> this.struct != null ? this.struct.getColor().z : 0,
                v -> { if (this.struct != null) this.struct.getColor().z = v; });
        this.colorRow.bind(3,
                () -> this.struct != null ? this.struct.getColor().w : 0,
                v -> { if (this.struct != null) this.struct.getColor().w = v; });

        this.intensityRow = new LabeledFloatRow("Intensity:", 16);
        this.addChild(this.intensityRow);
        this.intensityRow.bind(
                () -> this.struct != null ? this.struct.getIntensity() : 0,
                v -> { if (this.struct != null) this.struct.setIntensity(v); });
    }

    @Override
    void layoutContent(Font font, float contentW) {
        float y = 0;

        this.colorRow.localRect.set(0, y, contentW, 0);
        this.colorRow.layoutContent(font, contentW);
        y += this.colorRow.localRect.h + ROW_GAP;

        this.intensityRow.localRect.set(0, y, contentW, 0);
        this.intensityRow.layoutContent(font, contentW);
        y += this.intensityRow.localRect.h;

        this.localRect.h = y;
    }

    @Override
    void syncFromStruct() {
        if (this.struct == null) return;
        this.colorRow.sync(
                this.struct.getColor().x,
                this.struct.getColor().y,
                this.struct.getColor().z,
                this.struct.getColor().w);
        this.intensityRow.sync(this.struct.getIntensity());
    }
}
