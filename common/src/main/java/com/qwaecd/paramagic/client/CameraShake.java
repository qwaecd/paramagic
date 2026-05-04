package com.qwaecd.paramagic.client;

import com.qwaecd.paramagic.mixin.accessor.CameraAccessor;
import com.qwaecd.paramagic.tools.TimeProvider;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

public final class CameraShake {
    private static float intensity;
    private static float duration;
    private static float elapsedTime;
    private static float phaseX;
    private static float phaseY;

    private static final float PITCH_FREQUENCY = 64.0f;
    private static final float YAW_FREQUENCY = 83.0f;
    private static final float SECONDARY_FREQUENCY = 137.0f;

    private CameraShake() {
    }

    public static void shake(float intensity, float duration) {
        CameraShake.intensity = intensity;
        CameraShake.duration = Math.max(duration, 0.0f);
        CameraShake.elapsedTime = 0.0f;
        CameraShake.phaseX = (float) (Math.random() * Math.PI * 2.0);
        CameraShake.phaseY = (float) (Math.random() * Math.PI * 2.0);
    }

    public static void applyShake(Camera camera, Minecraft minecraft) {
        if (duration <= 0.0f || elapsedTime >= duration) {
            return;
        }
        final float deltaTime = TimeProvider.getDeltaTime(minecraft);
        elapsedTime = Math.min(elapsedTime + deltaTime, duration);
        float progress = elapsedTime / duration;
        float envelope = 1.0f - progress;
        envelope *= envelope;

        float t = elapsedTime;
        float pitch = (
                (float) Math.sin(t * PITCH_FREQUENCY + phaseX)
                + 0.35f * (float) Math.sin(t * SECONDARY_FREQUENCY + phaseY)
        ) * intensity * envelope;
        float yaw = (
                (float) Math.sin(t * YAW_FREQUENCY + phaseY)
                + 0.35f * (float) Math.sin(t * SECONDARY_FREQUENCY * 0.73f + phaseX)
        ) * intensity * envelope;

        ((CameraAccessor) camera).setRotationMethod(camera.getYRot() + yaw, camera.getXRot() + pitch);
    }
}
