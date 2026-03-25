package com.qwaecd.paramagic.ui_project.edit_table.cache.props;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.components.CurvyStarParaData;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

/**
 * Editable cache for {@link CurvyStarParaData} type-specific properties.
 */
@Getter
@Setter
public final class EditableCurvyStarProps implements EditableTypeProps {
    private float radius;
    private int sides;
    private float curvature;
    private float startAngle;
    private float lineWidth;

    @Override
    public void updateFrom(@Nonnull ParaComponentData data) {
        CurvyStarParaData star = (CurvyStarParaData) data;
        this.radius = star.radius;
        this.sides = star.sides;
        this.curvature = star.curvature;
        this.startAngle = star.startAngle;
        this.lineWidth = star.lineWidth;
    }

    @Nonnull
    @Override
    public ParaComponentData createComponentData() {
        return new CurvyStarParaData(
                this.radius, this.sides, this.curvature, this.startAngle, this.lineWidth);
    }

    @Nonnull
    @Override
    public EditableTypeProps deepCopy() {
        EditableCurvyStarProps copy = new EditableCurvyStarProps();
        copy.radius = this.radius;
        copy.sides = this.sides;
        copy.curvature = this.curvature;
        copy.startAngle = this.startAngle;
        copy.lineWidth = this.lineWidth;
        return copy;
    }
}
