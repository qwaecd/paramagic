package com.qwaecd.paramagic.core.render;

import org.joml.Vector3f;

public interface EmissiveProvider {
    String EMISSIVE_COLOR_UNIFORM = "u_emissiveColor";
    String EMISSIVE_INTENSITY_UNIFORM = "u_emissiveIntensity";
    Vector3f getEmissiveColor();
    float getEmissiveIntensity();
}
