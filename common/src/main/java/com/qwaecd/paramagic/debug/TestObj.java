package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.core.render.IRenderable;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class TestObj implements IRenderable {
    private final Mesh mesh;
    private final Material material;
    private final Transform transform;


    public TestObj(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
        this.transform = new Transform();
    }

    @Override
    public Mesh getMesh() {
        return this.mesh;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public Transform getTransform() {
        return this.transform;
    }
}
