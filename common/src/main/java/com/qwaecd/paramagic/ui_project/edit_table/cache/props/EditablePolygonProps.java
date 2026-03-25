package com.qwaecd.paramagic.ui_project.edit_table.cache.props;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.components.PolygonParaData;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

/**
 * Editable cache for {@link PolygonParaData} type-specific properties.
 */
@Getter
@Setter
public final class EditablePolygonProps implements EditableTypeProps {
    private float radius;
    private int sides;
    private float startAngle;
    private float lineWidth;

    @Override
    public void updateFrom(@Nonnull ParaComponentData data) {
        PolygonParaData polygon = (PolygonParaData) data;
        this.radius = polygon.radius;
        this.sides = polygon.sides;
        this.startAngle = polygon.startAngle;
        this.lineWidth = polygon.lineWidth;
    }

    @Nonnull
    @Override
    public ParaComponentData createComponentData() {
        return new PolygonParaData(this.radius, this.sides, this.startAngle, this.lineWidth);
    }

    @Nonnull
    @Override
    public EditableTypeProps deepCopy() {
        EditablePolygonProps copy = new EditablePolygonProps();
        copy.radius = this.radius;
        copy.sides = this.sides;
        copy.startAngle = this.startAngle;
        copy.lineWidth = this.lineWidth;
        return copy;
    }
}
