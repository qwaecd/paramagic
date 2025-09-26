package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class TestObj implements IRenderable {
    private final Mesh mesh;
    private final AbstractMaterial material;
    private final Transform transform;


    public TestObj(Mesh mesh, AbstractMaterial material) {
        this.mesh = mesh;
        this.material = material;
        this.transform = new Transform();
    }

    @Override
    public Mesh getMesh() {
        return this.mesh;
    }

    @Override
    public AbstractMaterial getMaterial() {
        return this.material;
    }

    @Override
    public Transform getTransform() {
        return this.transform;
    }
}
