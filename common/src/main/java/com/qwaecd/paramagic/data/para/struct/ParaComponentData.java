package com.qwaecd.paramagic.data.para.struct;

import lombok.Getter;
import lombok.Setter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Also can be called MagicCircleComponentData. Base class for all magic circle element components.
 * <p>
 * 也可以叫 MagicCircleComponentData，是所有法阵元素组件的基类.
 */
public abstract class ParaComponentData {
    /**
     * 单个ParaData下的树路径，也可作为唯一ID。
     */
    @Getter
    protected String componentId;
    /**
     * 可以任意指定的组件名称，需要手动确保唯一性。
     */
    @Getter
    @Setter
    protected String name;
    public final List<ParaComponentData> children;
    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale;

    public Vector4f color;
    @Getter
    @Setter
    protected float intensity;

    protected ParaComponentData() {
        this.children = new ArrayList<>();

        this.position = new Vector3f(0.0f, 0.0f, 0.0f);
        this.rotation = new Quaternionf().identity();
        this.scale = new Vector3f(1.0f, 1.0f, 1.0f);

        this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.intensity = 0.0f;
    }
    public void addChild(ParaComponentData child) {
        this.children.add(child);
    }
}
