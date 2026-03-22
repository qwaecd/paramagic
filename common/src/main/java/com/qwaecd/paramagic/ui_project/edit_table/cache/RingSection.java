package com.qwaecd.paramagic.ui_project.edit_table.cache;

import net.minecraft.client.gui.Font;

class RingSection extends EditSection {
    private final LabeledFloatRow innerRadiusRow;
    private final LabeledFloatRow outerRadiusRow;
    private final LabeledIntRow segmentsRow;

    RingSection() {
        this.innerRadiusRow = new LabeledFloatRow("Inner Radius:", 16);
        this.addChild(this.innerRadiusRow);
        this.innerRadiusRow.bind(
                () -> this.struct != null ? ringProps().getInnerRadius() : 0,
                v -> { if (this.struct != null) ringProps().setInnerRadius(v); });

        this.outerRadiusRow = new LabeledFloatRow("Outer Radius:", 16);
        this.addChild(this.outerRadiusRow);
        this.outerRadiusRow.bind(
                () -> this.struct != null ? ringProps().getOuterRadius() : 0,
                v -> { if (this.struct != null) ringProps().setOuterRadius(v); });

        this.segmentsRow = new LabeledIntRow("Segments:", 8);
        this.addChild(this.segmentsRow);
        this.segmentsRow.bind(
                () -> this.struct != null ? ringProps().getSegments() : 0,
                v -> { if (this.struct != null) ringProps().setSegments(v); });
    }

    private EditableRingProps ringProps() {
        return (EditableRingProps) this.struct.getTypeProps();
    }

    @Override
    void layoutContent(Font font, float contentW) {
        float y = 0;

        this.innerRadiusRow.localRect.set(0, y, contentW, 0);
        this.innerRadiusRow.layoutContent(font, contentW);
        y += this.innerRadiusRow.localRect.h + ROW_GAP;

        this.outerRadiusRow.localRect.set(0, y, contentW, 0);
        this.outerRadiusRow.layoutContent(font, contentW);
        y += this.outerRadiusRow.localRect.h + ROW_GAP;

        this.segmentsRow.localRect.set(0, y, contentW, 0);
        this.segmentsRow.layoutContent(font, contentW);
        y += this.segmentsRow.localRect.h;

        this.localRect.h = y;
    }

    @Override
    void syncFromStruct() {
        if (this.struct == null) return;
        EditableRingProps props = ringProps();
        this.innerRadiusRow.sync(props.getInnerRadius());
        this.outerRadiusRow.sync(props.getOuterRadius());
        this.segmentsRow.sync(props.getSegments());
    }
}
