package com.qwaecd.paramagic.data.animation;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class KeyframeProperties {
    public static final KeyframePropertyType ROTATION = new KeyframePropertyType("rotation", Quaternionf.class);
    public static final KeyframePropertyType POSITION = new KeyframePropertyType("position", Vector3f.class);
    public static final KeyframePropertyType SCALE = new KeyframePropertyType("scale", Vector3f.class);
    public static final KeyframePropertyType COLOR = new KeyframePropertyType("color", Vector4f.class);
    public static final KeyframePropertyType EMISSIVE_COLOR = new KeyframePropertyType("emissiveColor", Vector3f.class);
    public static final KeyframePropertyType EMISSIVE_INTENSITY = new KeyframePropertyType("emissiveIntensity", Float.class);
}
