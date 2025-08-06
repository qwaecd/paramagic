package com.qwaecd.paramagic.core.render.things;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

public abstract class BasePoseStack implements IPoseStack{

    public static final class Pose {
        final Matrix4f pose;
        final Matrix3f normal;

        public Pose(Matrix4f pose, Matrix3f normal) {
            this.pose = pose;
            this.normal = normal;
        }

        public Matrix4f pose() {
            return this.pose;
        }

        public Matrix3f normal() {
            return this.normal;
        }
    }
}
