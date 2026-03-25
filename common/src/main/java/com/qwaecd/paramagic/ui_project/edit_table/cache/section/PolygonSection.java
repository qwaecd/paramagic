package com.qwaecd.paramagic.ui_project.edit_table.cache.section;

import com.qwaecd.paramagic.ui_project.edit_table.cache.LabelTexts;
import com.qwaecd.paramagic.ui_project.edit_table.cache.label.LabeledFloatRow;
import com.qwaecd.paramagic.ui_project.edit_table.cache.label.LabeledIntRow;
import com.qwaecd.paramagic.ui_project.edit_table.cache.props.EditablePolygonProps;
import com.qwaecd.paramagic.ui_project.edit_table.util.EditInputRules;
import net.minecraft.client.gui.Font;

public class PolygonSection extends EditSection {
    private final LabeledFloatRow radiusRow;
    private final LabeledIntRow sidesRow;
    private final LabeledFloatRow startAngleRow;
    private final LabeledFloatRow lineWidthRow;

    public PolygonSection() {
        this.radiusRow = new LabeledFloatRow(LabelTexts.radiusRowText, 16);
        this.addChild(this.radiusRow);
        this.radiusRow.bind(
                () -> this.struct != null ? polygonProps().getRadius() : 0,
                v -> { if (this.struct != null) polygonProps().setRadius(v); });

        this.sidesRow = new LabeledIntRow(LabelTexts.sidesRowText, 8);
        this.addChild(this.sidesRow);
        this.sidesRow.bind(
                () -> this.struct != null ? polygonProps().getSides() : 0,
                v -> { if (this.struct != null) polygonProps().setSides(v); },
                v -> EditInputRules.clampMinInt(v, 3));

        this.startAngleRow = new LabeledFloatRow(LabelTexts.startAngleRowText, 16);
        this.addChild(this.startAngleRow);
        this.startAngleRow.bind(
                () -> this.struct != null ? polygonProps().getStartAngle() : 0,
                v -> { if (this.struct != null) polygonProps().setStartAngle(v); });

        this.lineWidthRow = new LabeledFloatRow(LabelTexts.lineWidthRowText, 16);
        this.addChild(this.lineWidthRow);
        this.lineWidthRow.bind(
                () -> this.struct != null ? polygonProps().getLineWidth() : 0,
                v -> { if (this.struct != null) polygonProps().setLineWidth(v); });
    }

    private EditablePolygonProps polygonProps() {
        return (EditablePolygonProps) this.struct.getTypeProps();
    }

    @Override
    public void layoutContent(Font font, float contentW) {
        float y = 0;

        this.radiusRow.localRect.set(0, y, contentW, 0);
        this.radiusRow.layoutContent(font, contentW);
        y += this.radiusRow.localRect.h + ROW_GAP;

        this.sidesRow.localRect.set(0, y, contentW, 0);
        this.sidesRow.layoutContent(font, contentW);
        y += this.sidesRow.localRect.h + ROW_GAP;

        this.startAngleRow.localRect.set(0, y, contentW, 0);
        this.startAngleRow.layoutContent(font, contentW);
        y += this.startAngleRow.localRect.h + ROW_GAP;

        this.lineWidthRow.localRect.set(0, y, contentW, 0);
        this.lineWidthRow.layoutContent(font, contentW);
        y += this.lineWidthRow.localRect.h;

        this.localRect.h = y;
    }

    @Override
    public void syncFromStruct() {
        if (this.struct == null) {
            return;
        }
        EditablePolygonProps props = polygonProps();
        this.radiusRow.sync(props.getRadius());
        this.sidesRow.sync(props.getSides());
        this.startAngleRow.sync(props.getStartAngle());
        this.lineWidthRow.sync(props.getLineWidth());
    }
}
