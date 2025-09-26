package com.qwaecd.paramagic.core.render.api;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import org.joml.Matrix4f;

import java.util.Optional;

public interface IRenderable {

    Mesh getMesh();
    AbstractMaterial getMaterial();
    Transform getTransform();
    /**
     * If this object has a precomputed world transformation matrix, return it.
     * @return an Optional containing the world matrix, or empty if not precomputed.
     */
    default Optional<Matrix4f> getPrecomputedWorldTransform() {
        return Optional.empty();
    }
}
