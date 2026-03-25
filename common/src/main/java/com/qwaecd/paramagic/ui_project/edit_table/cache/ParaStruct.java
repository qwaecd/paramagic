package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaComponentType;
import com.qwaecd.paramagic.data.para.struct.components.CurvyStarParaData;
import com.qwaecd.paramagic.data.para.struct.components.PolygonParaData;
import com.qwaecd.paramagic.data.para.struct.components.RingParaData;
import com.qwaecd.paramagic.data.para.struct.components.VoidParaData;
import com.qwaecd.paramagic.ui_project.edit_table.cache.props.EditableCurvyStarProps;
import com.qwaecd.paramagic.ui_project.edit_table.cache.props.EditablePolygonProps;
import com.qwaecd.paramagic.ui_project.edit_table.cache.props.EditableRingProps;
import com.qwaecd.paramagic.ui_project.edit_table.cache.props.EditableTypeProps;
import lombok.Getter;
import lombok.Setter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ParaStruct {
    private int componentType;
    @Nullable
    @Getter
    @Setter
    private String name = null;

    @Getter
    @Setter
    private Vector3f position;
    @Getter
    @Setter
    private Quaternionf rotation;
    @Getter
    @Setter
    private Vector3f scale;
    @Getter
    @Setter
    private Vector4f color;
    @Getter
    @Setter
    private float intensity;

    @Nullable
    private EditableTypeProps typeProps;

    public ParaStruct() {
        this.position = new Vector3f();
        this.rotation = new Quaternionf();
        this.scale = new Vector3f();
        this.color = new Vector4f();
        this.intensity = 0.5f;
    }

    public ParaStruct(ParaComponentData data) {
        this();
        this.updateFromParaComponent(data);
    }

    @Nonnull
    public String getTypeName() {
        for (ParaComponentType value : ParaComponentType.values()) {
            if (value.ID() == this.componentType) {
                return value.name();
            }
        }
        return "UnknownType(" + this.componentType + ")";
    }

    /**
     * Returns the type-specific editable props, or {@code null} for VOID / unknown types.
     */
    @Nullable
    public EditableTypeProps getTypeProps() {
        return this.typeProps;
    }

    public void updateFromParaComponent(ParaComponentData data) {
        this.componentType = data.getComponentType();
        this.name = data.getName();
        this.position.set(data.position);
        this.rotation.set(data.rotation);
        this.scale.set(data.scale);
        this.color.set(data.color);
        this.intensity = data.getIntensity();

        this.typeProps = createTypePropsFor(this.componentType);
        if (this.typeProps != null) {
            this.typeProps.updateFrom(data);
        }
    }

    /**
     * Writes only the common base properties back to an existing data object.
     * Type-specific final fields are <b>not</b> touched — use
     * {@link #rebuildComponentData()} when type-specific changes need to be committed.
     */
    public void applyToParaComponent(ParaComponentData data) {
        data.setName(this.name);
        data.position.set(this.position);
        data.rotation.set(this.rotation);
        data.scale.set(this.scale);
        data.color.set(this.color);
        data.setIntensity(this.intensity);
    }

    /**
     * Rebuilds a complete {@link ParaComponentData} from this edit cache.
     * Type-specific final fields are set at construction via {@link EditableTypeProps#createComponentData()};
     * base properties are applied on top via {@link #applyToParaComponent(ParaComponentData)}.
     */
    @Nonnull
    public ParaComponentData rebuildComponentData() {
        ParaComponentData data;
        if (this.typeProps != null) {
            data = this.typeProps.createComponentData();
        } else {
            data = new VoidParaData();
        }
        this.applyToParaComponent(data);
        return data;
    }

    public int getComponentType() {
        return this.componentType;
    }

    public void setComponentType(int componentType) {
        this.componentType = componentType;
        this.typeProps = createTypePropsFor(componentType);
        if (this.typeProps != null) {
            this.typeProps.updateFrom(createDefaultComponentDataFor(componentType));
        }
    }

    @Nonnull
    public ParaStruct deepCopy() {
        ParaStruct copy = new ParaStruct();
        copy.componentType = this.componentType;
        copy.name = this.name;
        copy.position = new Vector3f(this.position);
        copy.rotation = new Quaternionf(this.rotation);
        copy.scale = new Vector3f(this.scale);
        copy.color = new Vector4f(this.color);
        copy.intensity = this.intensity;
        copy.typeProps = this.typeProps != null ? this.typeProps.deepCopy() : null;
        return copy;
    }

    @Nullable
    private static EditableTypeProps createTypePropsFor(int componentType) {
        if (componentType == ParaComponentType.RING.ID()) return new EditableRingProps();
        if (componentType == ParaComponentType.POLYGON.ID()) return new EditablePolygonProps();
        if (componentType == ParaComponentType.CURVY_STAR.ID()) return new EditableCurvyStarProps();
        return null;
    }

    @Nonnull
    private static ParaComponentData createDefaultComponentDataFor(int componentType) {
        if (componentType == ParaComponentType.RING.ID()) return new RingParaData(0.0f, 0.0f, 3);
        if (componentType == ParaComponentType.POLYGON.ID()) return new PolygonParaData(0.0f, 3);
        if (componentType == ParaComponentType.CURVY_STAR.ID()) return new CurvyStarParaData(0.0f, 3);
        return new VoidParaData();
    }
}
