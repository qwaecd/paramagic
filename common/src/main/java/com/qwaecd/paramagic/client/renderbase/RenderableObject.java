package com.qwaecd.paramagic.client.renderbase;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class RenderableObject implements IRenderable {
    protected final Mesh mesh;
    protected final AbstractMaterial material;
    protected final Transform transform;

    public RenderableObject(Mesh mesh, AbstractMaterial material) {
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
