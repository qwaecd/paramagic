package com.qwaecd.paramagic.ui_project.edit_table.cache;

import net.minecraft.client.gui.Font;

class CurvyStarSection extends EditSection {
    private final LabeledFloatRow radiusRow;
    private final LabeledIntRow sidesRow;
    private final LabeledFloatRow curvatureRow;
    private final LabeledFloatRow startAngleRow;
    private final LabeledFloatRow lineWidthRow;

    CurvyStarSection() {
        this.radiusRow = new LabeledFloatRow("Radius:", 16);
        this.addChild(this.radiusRow);
        this.radiusRow.bind(
                () -> this.struct != null ? starProps().getRadius() : 0,
                v -> { if (this.struct != null) starProps().setRadius(v); });

        this.sidesRow = new LabeledIntRow("Sides:", 8);
        this.addChild(this.sidesRow);
        this.sidesRow.bind(
                () -> this.struct != null ? starProps().getSides() : 0,
                v -> { if (this.struct != null) starProps().setSides(v); });

        this.curvatureRow = new LabeledFloatRow("Curvature:", 16);
        this.addChild(this.curvatureRow);
        this.curvatureRow.bind(
                () -> this.struct != null ? starProps().getCurvature() : 0,
                v -> { if (this.struct != null) starProps().setCurvature(v); });

        this.startAngleRow = new LabeledFloatRow("Start Angle:", 16);
        this.addChild(this.startAngleRow);
        this.startAngleRow.bind(
                () -> this.struct != null ? starProps().getStartAngle() : 0,
                v -> { if (this.struct != null) starProps().setStartAngle(v); });

        this.lineWidthRow = new LabeledFloatRow("Line Width:", 16);
        this.addChild(this.lineWidthRow);
        this.lineWidthRow.bind(
                () -> this.struct != null ? starProps().getLineWidth() : 0,
                v -> { if (this.struct != null) starProps().setLineWidth(v); });
    }

    private EditableCurvyStarProps starProps() {
        return (EditableCurvyStarProps) this.struct.getTypeProps();
    }

    @Override
    void layoutContent(Font font, float contentW) {
        float y = 0;

        this.radiusRow.localRect.set(0, y, contentW, 0);
        this.radiusRow.layoutContent(font, contentW);
        y += this.radiusRow.localRect.h + ROW_GAP;

        this.sidesRow.localRect.set(0, y, contentW, 0);
        this.sidesRow.layoutContent(font, contentW);
        y += this.sidesRow.localRect.h + ROW_GAP;

        this.curvatureRow.localRect.set(0, y, contentW, 0);
        this.curvatureRow.layoutContent(font, contentW);
        y += this.curvatureRow.localRect.h + ROW_GAP;

        this.startAngleRow.localRect.set(0, y, contentW, 0);
        this.startAngleRow.layoutContent(font, contentW);
        y += this.startAngleRow.localRect.h + ROW_GAP;

        this.lineWidthRow.localRect.set(0, y, contentW, 0);
        this.lineWidthRow.layoutContent(font, contentW);
        y += this.lineWidthRow.localRect.h;

        this.localRect.h = y;
    }

    @Override
    void syncFromStruct() {
        if (this.struct == null) return;
        EditableCurvyStarProps props = starProps();
        this.radiusRow.sync(props.getRadius());
        this.sidesRow.sync(props.getSides());
        this.curvatureRow.sync(props.getCurvature());
        this.startAngleRow.sync(props.getStartAngle());
        this.lineWidthRow.sync(props.getLineWidth());
    }
}
