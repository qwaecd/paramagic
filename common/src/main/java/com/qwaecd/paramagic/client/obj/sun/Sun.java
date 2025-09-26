package com.qwaecd.paramagic.client.obj.sun;

import com.qwaecd.paramagic.client.material.SunMaterial;
import com.qwaecd.paramagic.client.renderbase.prototype.SpherePrototype;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class Sun implements IRenderable {
    private final Mesh mesh;
    private final AbstractMaterial material;
    private final Transform transform;

    public Sun(Shader shader) {
        this.mesh = SpherePrototype.getINSTANCE().getMesh();
        this.transform = new Transform();
        this.material = new SunMaterial(shader);
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
