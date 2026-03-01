package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaComponentType;
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

    public void updateFromParaComponent(ParaComponentData data) {
        this.componentType = data.getComponentType();
        this.name = data.getName();
        this.position.set(data.position);
        this.rotation.set(data.rotation);
        this.scale.set(data.scale);
        this.color.set(data.color);
        this.intensity = data.getIntensity();
    }

    public void applyToParaComponent(ParaComponentData data) {
        data.setName(this.name);
        data.position.set(this.position);
        data.rotation.set(this.rotation);
        data.scale.set(this.scale);
        data.color.set(this.color);
        data.setIntensity(this.intensity);
    }
}
