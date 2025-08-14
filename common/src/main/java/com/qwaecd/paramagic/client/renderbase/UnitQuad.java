package com.qwaecd.paramagic.client.renderbase;

import com.qwaecd.paramagic.client.renderbase.prototype.IShapePrototype;
import com.qwaecd.paramagic.client.renderbase.prototype.UnitQuadPrototype;
import com.qwaecd.paramagic.core.render.IRenderable;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class UnitQuad implements IRenderable {
    private final IShapePrototype prototype;
    private final AbstractMaterial material;
    private final Transform transform;

    public UnitQuad() {
        this.prototype = UnitQuadPrototype.getINSTANCE();
        this.material = new Material(ShaderManager.getPositionColorShader());
        this.transform = new Transform();
    }

    public UnitQuad(AbstractMaterial material) {
        this.prototype = UnitQuadPrototype.getINSTANCE();
        this.material = material;
        this.transform = new Transform();
    }

    @Override
    public Mesh getMesh() {
        return this.prototype.getMesh();
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
