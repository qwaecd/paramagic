package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

public interface IRenderable {

    Mesh getMesh();
    Material getMaterial();
    Transform getTransform();
}
