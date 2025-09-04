package com.qwaecd.paramagic.core.render.context.impl;

import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.things.BasePoseStack;
import com.qwaecd.paramagic.core.render.things.ICamera;
import com.qwaecd.paramagic.core.render.things.IPoseStack;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3d;

public class BasedContext implements RenderContext {
    private final Camera mcCamera;
    private final Matrix4f viewMatrix;
    private final Matrix4f projectionMatrix;

    public BasedContext(Camera mcCamera, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        this.mcCamera = mcCamera;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
    }
    @Override
    public ICamera getCamera() {
        Vec3 mcPos = mcCamera.getPosition();
        Vector3d pos = new Vector3d(mcPos.x, mcPos.y, mcPos.z);
        return new BasedCamera(pos, mcCamera.rotation());
    }

    @Override
    public IPoseStack getPoseStack() {
        return new BasePoseStack() {
            @Override
            public IPoseStack getPoseStack() {
                return null;
            }

            @Override
            public Pose getLastPose() {
                return null;
            }

            @Override
            public Matrix4f getViewMatrix() {
                return viewMatrix;
            }
        };
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
