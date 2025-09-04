package com.qwaecd.paramagic.core.render.things;

import org.joml.Quaternionf;
import org.joml.Vector3d;

public interface ICamera {
    Vector3d position();
    Quaternionf rotation();
}
