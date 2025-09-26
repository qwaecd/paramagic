package com.qwaecd.paramagic.client.renderbase.factory;

import com.qwaecd.paramagic.client.renderbase.Sphere;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;

public class SphereFactory extends ShapeFactory {
    private AbstractMaterial material = null;
    @Override
    public IRenderable createInstance() {
        if (this.material != null) {
            return new Sphere(this.material);
        }
        return new Sphere();
    }

    public SphereFactory withMaterial(AbstractMaterial material) {
        this.material = material;
        return this;
    }
}
