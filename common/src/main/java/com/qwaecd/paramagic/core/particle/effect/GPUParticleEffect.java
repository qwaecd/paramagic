package com.qwaecd.paramagic.core.particle.effect;

import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.render.Transform;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;


/**
 * GPU 粒子效果实例。<br>
 * Spatial semantics: particles/emitter requests/physics params are all interpreted in effect-local space.<br>
 * 空间语义：粒子状态、发射请求、物理参数都以 effect 局部坐标解释。<br>
 * 可通过 {@link #getTransform()} 控制 effect 的整体变换；渲染阶段使用该矩阵完成局部到世界的映射。<br>
 */
public class GPUParticleEffect {
    @Getter
    private final float maxLifeTime;
    @Getter
    private float currentLifeTime = 0.0f;
    private final List<Emitter> emitters;
    @Getter
    private final int maxParticleCount;
    @Getter
    private int effectId = -1; // 效果的唯一 ID（非位掩码）
    @Getter
    private final EffectPhysicsParameter physicsParameter;

    private final Transform transform;

    @Getter
    @Setter
    private int effectFlag = EffectFlags.IS_ALIVE.get(); // 效果的状态标志位掩码（bitmask）

    private final List<EmissionRequest> emissionRequests;
    private final ConcurrentLinkedQueue<EmissionRequest> externalEmissionRequests;
    private static final float NON_INVERTIBLE_EPSILON = 1.0e-8f;

    public GPUParticleEffect(
            List<Emitter> emitters,
            int maxParticleCount,
            float maxLifeTime
    ) {
        this.maxLifeTime = maxLifeTime;
        this.emitters = emitters;
        this.maxParticleCount = maxParticleCount;
        this.emissionRequests = new ArrayList<>(emitters.size());
        this.externalEmissionRequests = new ConcurrentLinkedQueue<>();
        this.physicsParameter = new EffectPhysicsParameter();
        this.transform = new Transform();
    }

    public GPUParticleEffect(
            List<Emitter> emitters,
            int maxParticleCount,
            float maxLifeTime,
            EffectPhysicsParameter physicsParameter
    ) {
        this.maxLifeTime = maxLifeTime;
        this.emitters = emitters;
        this.maxParticleCount = maxParticleCount;
        this.emissionRequests = new ArrayList<>(emitters.size());
        this.externalEmissionRequests = new ConcurrentLinkedQueue<>();
        this.physicsParameter = physicsParameter;
        this.transform = new Transform();
    }

    public Transform getTransform() {
        return this.transform;
    }

    /**
     * Local-space center force position.
     */
    public GPUParticleEffect setCenterForceLocal(float x, float y, float z) {
        this.physicsParameter.setCFPos(x, y, z);
        return this;
    }

    /**
     * Local-space center force position.
     */
    public GPUParticleEffect setCenterForceLocal(Vector3f localPos) {
        this.physicsParameter.setCFPos(localPos);
        return this;
    }

    /**
     * World-space center force position. Internally converted to local-space before storing.
     */
    public GPUParticleEffect setCenterForceWorld(float x, float y, float z) {
        Vector3f localPos = worldPositionToLocal(x, y, z);
        this.physicsParameter.setCFPos(localPos);
        return this;
    }

    /**
     * World-space center force position. Internally converted to local-space before storing.
     */
    public GPUParticleEffect setCenterForceWorld(Vector3f worldPos) {
        return this.setCenterForceWorld(worldPos.x, worldPos.y, worldPos.z);
    }

    /**
     * Local-space linear force direction/intensity.
     */
    public GPUParticleEffect setLinearForceLocal(float x, float y, float z) {
        this.physicsParameter.setLinearForce(x, y, z);
        return this;
    }

    /**
     * Local-space linear force direction/intensity.
     */
    public GPUParticleEffect setLinearForceLocal(Vector3f localForce) {
        this.physicsParameter.setLinearForce(localForce);
        return this;
    }

    /**
     * World-space linear force direction/intensity. Internally converted to local-space before storing.
     */
    public GPUParticleEffect setLinearForceWorld(float x, float y, float z) {
        Vector3f localForce = worldDirectionToLocal(x, y, z);
        this.physicsParameter.setLinearForce(localForce);
        return this;
    }

    /**
     * World-space linear force direction/intensity. Internally converted to local-space before storing.
     */
    public GPUParticleEffect setLinearForceWorld(Vector3f worldForce) {
        return this.setLinearForceWorld(worldForce.x, worldForce.y, worldForce.z);
    }

    public GPUParticleEffect setLinearForceEnabled(boolean enabled) {
        this.physicsParameter.setLinearForceEnabled(enabled);
        return this;
    }

    public GPUParticleEffect setDragCoefficient(float dragCoefficient) {
        this.physicsParameter.setDragCoefficient(dragCoefficient);
        return this;
    }

    public GPUParticleEffect setPrimaryForce(float A, float B, float maxRadius, boolean enabled) {
        this.physicsParameter.setPrimaryForceParam(A, B);
        this.physicsParameter.setPrimaryForceMaxRadius(maxRadius);
        this.physicsParameter.setPrimaryForceEnabled(enabled);
        return this;
    }

    public GPUParticleEffect setSecondaryForce(float C, float D, float maxRadius, boolean enabled) {
        this.physicsParameter.setSecondaryForceParam(C, D);
        this.physicsParameter.setSecondaryForceMaxRadius(maxRadius);
        this.physicsParameter.setSecondaryForceEnabled(enabled);
        return this;
    }

    public GPUParticleEffect setSinusoidalForce(float E, float F, float G, float phase, float maxRadius, boolean enabled) {
        this.physicsParameter.setSinusoidalForceParam(E, F, G);
        this.physicsParameter.setSinusoidalExtraParam(phase);
        this.physicsParameter.setSinusoidalForceMaxRadius(maxRadius);
        this.physicsParameter.setSinusoidalForceEnabled(enabled);
        return this;
    }

    /**
     * Call this function only within the rendering loop, not in the game logic loop.<br>
     * 仅在渲染循环内调用该函数，不要在游戏逻辑循环内调用。<br>
     * 更新粒子效果的状态，包括其所有发射器的状态。<br>
     * @param deltaTime Seconds of time increment (time since last frame).<br>
     * 时间增量，单位秒（为距离上一帧的时间）<br>
     */
    public void update(float deltaTime) {
        this.currentLifeTime += deltaTime;
        for (Emitter e : emitters) {
            e.update(deltaTime);
        }
    }

    public List<EmissionRequest> getEmissionRequests() {
        this.emissionRequests.clear();
        for (Emitter e : emitters) {
            EmissionRequest req = e.getEmissionRequest();
            if (req != null && req.getCount() > 0) {
                req.setEffectId(this.effectId);
                this.emissionRequests.add(req);
            }
        }
        EmissionRequest externalRequest;
        while ((externalRequest = this.externalEmissionRequests.poll()) != null) {
            externalRequest.setEffectId(this.effectId);
            this.emissionRequests.add(externalRequest);
        }
        return this.emissionRequests;
    }

    public void addExternalEmissionRequest(@Nonnull EmissionRequest request) {
        if (request.getCount() > 0) {
            this.externalEmissionRequests.add(request);
        }
    }

    /**
     * 判断粒子效果当前是否处于存活状态。<br>
     * 规则：当未设置 KILL_ALL 标志，且寿命未超出或寿命无限（maxLifeTime <= 0），则认为存活。<br>
     * 注意：in() 的入参是位掩码，应传入 {@link #effectFlag}，而不是 {@link #effectId}。
     */
    public boolean isAlive() {
        return (!EffectFlags.KILL_ALL.in(this.effectFlag))
                && (this.maxLifeTime <= 0.0f || this.currentLifeTime < this.maxLifeTime);
    }

    public void forEachEmitter(Consumer<Emitter> action) {
        for (Emitter e : emitters) {
            action.accept(e);
        }
    }

    private Matrix4f getInverseModelMatrix() {
        Matrix4f modelMatrix = this.transform.getModelMatrix();
        if (Math.abs(modelMatrix.determinant()) <= NON_INVERTIBLE_EPSILON) {
            throw new IllegalStateException("Effect transform is non-invertible. Cannot convert world-space values to local-space.");
        }
        return new Matrix4f(modelMatrix).invert();
    }

    private Vector3f worldPositionToLocal(float x, float y, float z) {
        return getInverseModelMatrix().transformPosition(x, y, z, new Vector3f());
    }

    private Vector3f worldDirectionToLocal(float x, float y, float z) {
        return getInverseModelMatrix().transformDirection(x, y, z, new Vector3f());
    }

    final void setEffectId(int effectId) {
        if (this.effectId != -1) {
            throw new IllegalStateException("Effect ID has already been assigned.");
        }
        this.effectId = effectId;
    }
}
