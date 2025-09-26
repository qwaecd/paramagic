package com.qwaecd.paramagic.client.renderbase;

import com.qwaecd.paramagic.client.renderbase.prototype.IShapePrototype;
import com.qwaecd.paramagic.client.renderbase.prototype.SpherePrototype;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class Sphere implements IRenderable {
    private final IShapePrototype prototype;
    private final AbstractMaterial material;
    private final Transform transform;

    public Sphere() {
        this.prototype = SpherePrototype.getINSTANCE();
        this.material = new Material(ShaderManager.getInstance().getPositionColorShader());
        this.transform = new Transform();
    }

    public Sphere(AbstractMaterial material) {
        this.prototype = SpherePrototype.getINSTANCE();
        this.material = material;
        this.transform = new Transform();
    }

    @Override
    public Mesh getMesh() {
        return this.prototype.getMesh();
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
