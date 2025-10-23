package com.qwaecd.paramagic.data.para.util;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.components.VoidParaData;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public final class ParaComponentBuilder {
    // 当前正在构建的组件实例
    private final ParaComponentData instance;
    // 指向父节点的 Builder
    private final ParaComponentBuilder parentBuilder;
    // 存储子节点的 Builders
    private final List<ParaComponentBuilder> childBuilders  = new ArrayList<>();

    /**
     * 创建一个根节点 Para.
     */
    public ParaComponentBuilder() {
        this(new VoidParaData());
    }

    /**
     * 创建指定的根节点 Para.
     * @param componentData 指定的根节点数据实例.
     */
    public ParaComponentBuilder(ParaComponentData componentData) {
        this.instance = componentData;
        this.parentBuilder = null;
    }

    private ParaComponentBuilder(ParaComponentBuilder parentBuilder) {
        this.instance = new VoidParaData();
        this.parentBuilder = parentBuilder;
    }

    private ParaComponentBuilder(ParaComponentData componentData, ParaComponentBuilder parentBuilder) {
        this.instance = componentData;
        this.parentBuilder = parentBuilder;
    }

    public ParaComponentBuilder beginChild() {
        ParaComponentBuilder childBuilder = new ParaComponentBuilder(this);
        this.childBuilders.add(childBuilder);
        return childBuilder;
    }

    public ParaComponentBuilder beginChild(ParaComponentData componentData) {
        ParaComponentBuilder childBuilder = new ParaComponentBuilder(componentData, this);
        this.childBuilders.add(childBuilder);
        return childBuilder;
    }

    public ParaComponentBuilder endChild() {
        if (this.parentBuilder == null) {
            throw new IllegalStateException("Cannot pop from root builder.");
        }
        return this.parentBuilder;
    }

    public ParaComponentBuilder withName(String name) {
        this.instance.setName(name);
        return this;
    }

    public ParaComponentBuilder withPosition(float x, float y, float z) {
        this.instance.position.set(x, y, z);
        return this;
    }

    public ParaComponentBuilder withPosition(Vector3f v) {
        this.withPosition(v.x, v.y, v.z);
        return this;
    }

    public ParaComponentBuilder withRotation(Quaternionf rotation) {
        this.instance.rotation.set(rotation);
        return this;
    }

    public ParaComponentBuilder withScale(float x, float y, float z) {
        this.instance.scale.set(x, y, z);
        return this;
    }

    public ParaComponentBuilder withScale(float s) {
        this.withScale(s, s, s);
        return this;
    }

    public ParaComponentBuilder withColor(float r, float g, float b, float a) {
        this.instance.color.set(r, g, b, a);
        return this;
    }

    public ParaComponentBuilder withColor(Vector4f v) {
        this.withColor(v.x, v.y, v.z, v.w);
        return this;
    }

    public ParaComponentBuilder withIntensity(float intensity) {
        this.instance.setIntensity(intensity);
        return this;
    }

    /**
     * 递归地构建所有的子节点，并将它们添加到父节点的children列表中。
     * @return 返回构建完成的、包含完整树结构的根节点对象。
     */
    public ParaComponentData build() {
        // 只有根Builder才能调用build()
        if (this.parentBuilder != null) {
            throw new IllegalStateException("build() can only be called on the root builder.");
        }
        return buildRecursive();
    }

    private ParaComponentData buildRecursive() {
        for (ParaComponentBuilder childBuilder : this.childBuilders) {
            this.instance.addChild(childBuilder.buildRecursive());
        }
        return this.instance;
    }
}
