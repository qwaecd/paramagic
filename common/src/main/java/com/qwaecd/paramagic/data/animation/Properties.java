package com.qwaecd.paramagic.data.animation;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Properties {
    public static final PropertyType ROTATION = new PropertyType("rotation", Quaternionf.class);
    public static final PropertyType POSITION = new PropertyType("position", Vector3f.class);
    public static final PropertyType SCALE = new PropertyType("scale", Vector3f.class);
}
