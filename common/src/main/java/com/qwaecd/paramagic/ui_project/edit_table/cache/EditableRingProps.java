package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.components.RingParaData;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

/**
 * Editable cache for {@link RingParaData} type-specific properties.
 */
@Getter
@Setter
final class EditableRingProps implements EditableTypeProps {
    private float innerRadius;
    private float outerRadius;
    private int segments;

    @Override
    public void updateFrom(@Nonnull ParaComponentData data) {
        RingParaData ring = (RingParaData) data;
        this.innerRadius = ring.innerRadius;
        this.outerRadius = ring.outerRadius;
        this.segments = ring.segments;
    }

    @Nonnull
    @Override
    public ParaComponentData createComponentData() {
        return new RingParaData(this.innerRadius, this.outerRadius, this.segments);
    }

    @Nonnull
    @Override
    public EditableTypeProps deepCopy() {
        EditableRingProps copy = new EditableRingProps();
        copy.innerRadius = this.innerRadius;
        copy.outerRadius = this.outerRadius;
        copy.segments = this.segments;
        return copy;
    }
}
