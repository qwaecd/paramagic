package com.qwaecd.paramagic.client.renderbase;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class Sphere implements IRenderable {
    private final Mesh mesh;
    private final AbstractMaterial material;
    private final Transform transform;

    public Sphere() {
        this.mesh = SharedMeshes.sphere();
        this.material = new Material(ShaderManager.getInstance().getPositionColorShader());
        this.transform = new Transform();
    }

    public Sphere(AbstractMaterial material) {
        this.mesh = SharedMeshes.sphere();
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
