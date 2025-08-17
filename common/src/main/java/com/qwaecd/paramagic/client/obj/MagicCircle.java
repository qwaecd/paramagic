package com.qwaecd.paramagic.client.obj;

import com.qwaecd.paramagic.core.render.IRenderable;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class MagicCircle implements IRenderable {
    private final Mesh mesh;
    private final AbstractMaterial material;
    private final Transform transform = new Transform();

    public MagicCircle(Mesh planeMesh, AbstractMaterial material) {
        this.mesh = planeMesh;
        this.material = material;
    }
    @Override
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public AbstractMaterial getMaterial() {
        return material;
    }

    @Override
    public Transform getTransform() {
        return transform;
    }
}
