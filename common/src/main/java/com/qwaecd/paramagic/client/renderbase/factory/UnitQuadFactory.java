package com.qwaecd.paramagic.client.renderbase.factory;

import com.qwaecd.paramagic.client.renderbase.UnitQuad;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;

public class UnitQuadFactory extends ShapeFactory {
    private AbstractMaterial material = null;

    @Override
    public IRenderable createInstance() {
        if (this.material != null) {
            return new UnitQuad(material);
        }
        return new UnitQuad();
    }

    public UnitQuadFactory withMaterial(AbstractMaterial material) {
        this.material = material;
        return this;
    }
}
