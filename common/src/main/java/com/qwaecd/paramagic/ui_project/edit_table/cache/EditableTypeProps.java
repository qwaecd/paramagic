package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;

import javax.annotation.Nonnull;

/**
 * Strongly-typed editable cache for component-type-specific properties.
 * <p>
 * Each {@link com.qwaecd.paramagic.data.para.struct.ParaComponentType} that carries
 * extra parameters beyond the common base fields should have a corresponding
 * implementation (e.g. {@code EditableRingProps}, {@code EditablePolygonProps}).
 * <p>
 * VOID has no extra properties and uses {@code null} instead of an implementation.
 */
public interface EditableTypeProps {

    /**
     * Extracts type-specific field values from the given real data object.
     * Implementations must cast to the expected subclass.
     */
    void updateFrom(@Nonnull ParaComponentData data);

    /**
     * Creates a new {@link ParaComponentData} with type-specific fields
     * populated from this props object.  Base properties (position, rotation,
     * scale, color, intensity, name) are <b>not</b> set by this method —
     * the caller is responsible for applying them afterwards.
     */
    @Nonnull
    ParaComponentData createComponentData();

    /**
     * Returns a deep copy that shares no mutable state with this instance.
     */
    @Nonnull
    EditableTypeProps deepCopy();
}
