package com.qwaecd.paramagic.data.para;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Also can be called MagicCircleComponentData.
 * <p>
 * 也可以叫 MagicCircleComponentData.
 * */
public abstract class ParaComponentData {
    public final String componentId;
    public final List<ParaComponentData> children;
    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale;

    public Vector4f color;

    protected ParaComponentData(String componentId) {
        this.componentId = componentId;
        this.children = new ArrayList<>();

        this.position = new Vector3f(0.0f, 0.0f, 0.0f);
        this.rotation = new Quaternionf().identity();
        this.scale = new Vector3f(1.0f, 1.0f, 1.0f);
        this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    public void addChild(ParaComponentData child) {
        this.children.add(child);
    }
}
