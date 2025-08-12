package com.qwaecd.paramagic.client.renderbase;

import com.qwaecd.paramagic.client.renderbase.prototype.IShapePrototype;
import com.qwaecd.paramagic.client.renderbase.prototype.SpherePrototype;
import com.qwaecd.paramagic.core.render.IRenderable;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.BaseMaterial;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class Sphere implements IRenderable {
    private final IShapePrototype prototype;
    private final Material material;
    private final Transform transform;

    public Sphere() {
        this.prototype = SpherePrototype.getINSTANCE();
        this.material = new BaseMaterial(ShaderManager.getPositionColorShader());
        this.transform = new Transform();
    }

    public Sphere(Material material) {
        this.prototype = SpherePrototype.getINSTANCE();
        this.material = material;
        this.transform = new Transform();
    }

    @Override
    public Mesh getMesh() {
        return this.prototype.getMesh();
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
