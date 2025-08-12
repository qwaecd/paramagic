package com.qwaecd.paramagic.client.renderbase.factory;

import com.qwaecd.paramagic.client.renderbase.Sphere;
import com.qwaecd.paramagic.core.render.IRenderable;
import com.qwaecd.paramagic.core.render.texture.Material;

public class SphereFactory extends ShapeFactory {
    private Material material = null;
    @Override
    public IRenderable createInstance() {
        if (this.material != null) {
            return new Sphere(this.material);
        }
        return new Sphere();
    }

    public SphereFactory withMaterial(Material material) {
        this.material = material;
        return this;
    }
}
