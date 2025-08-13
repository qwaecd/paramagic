package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public interface IRenderable {

    Mesh getMesh();
    AbstractMaterial getMaterial();
    Transform getTransform();
}
