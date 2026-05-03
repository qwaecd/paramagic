package com.qwaecd.paramagic.client.renderbase;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class UnitQuad implements IRenderable {
    private final Mesh mesh;
    private final AbstractMaterial material;
    private final Transform transform;

    public UnitQuad() {
        this.mesh = SharedMeshes.unitQuad();
        this.material = new Material(ShaderManager.getInstance().getPositionColorShader());
        this.transform = new Transform();
    }

    public UnitQuad(AbstractMaterial material) {
        this.mesh = SharedMeshes.unitQuad();
        this.material = material;
        this.transform = new Transform();
    }

    @Override
    public Mesh getMesh() {
        return this.mesh;
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
