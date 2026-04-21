package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.impl.PointEmitter;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticleFacingModeStates;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticlePrimitiveTypeStates;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

/**
 * 用于验证「移动发射器 + 低速粒子」是否可以逐步绘制出法阵轮廓。
 */
public final class DebugGpuMagicCircleEffect {
    private static final float PARTICLES_PER_SECOND = 300.0f;
    private static final float BLOOM_LEVEL = 0.3f;
    /**
     * 绘制速度倍率（>1 更快，<1 更慢），用于快速调节“画法阵”的节奏。
     */
    public static float DRAW_SPEED_MULTIPLIER = 2.2f;

    private static final float OUTER_RING_RADIUS = 8.0f;
    private static final float STAR_SQUARE_CIRCUMRADIUS = 5.2f;
    private static final float INNER_RING_RADIUS_LARGE = 2.8f;
    private static final float INNER_RING_RADIUS_SMALL = 1.8f;

    private DebugGpuMagicCircleEffect() {
    }

    public static GPUParticleEffect create(Vector3f worldPosition) {
        PointEmitter penEmitter = new PointEmitter(new Vector3f(OUTER_RING_RADIUS, 0.0f, 0.0f), PARTICLES_PER_SECOND);
        penEmitter.getProperty(BASE_VELOCITY).modify(v -> v.set(0.0f, 0.1f, 0.0f));
        penEmitter.trySet(VELOCITY_MODE, VelocityModeStates.RANDOM);
        penEmitter.getProperty(VELOCITY_SPREAD).set(180.0f);
        penEmitter.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(5.0f, 8.0f));
        penEmitter.getProperty(SIZE_RANGE).modify(v -> v.set(0.14f, 0.22f));
        penEmitter.getProperty(COLOR).modify(v -> v.set(0.25f, 0.4f, 0.7f, 1.0f));
        penEmitter.getProperty(BLOOM_INTENSITY).set(BLOOM_LEVEL);
        penEmitter.trySet(PARTICLE_FACING_MODE, ParticleFacingModeStates.NORMAL_FACING);
        penEmitter.trySet(PARTICLE_PRIMITIVE_TYPE, ParticlePrimitiveTypeStates.QUAD);

        GPUParticleEffect effect = new GPUParticleEffect(
                List.of(penEmitter),
                250_000,
                300.0f,
                new PhysicsParamBuilder()
                        .primaryForceEnabled(false)
                        .secondaryForceEnabled(false)
                        .sinusoidalForceEnabled(false)
                        .linearForceEnabled(false)
                        .dragCoefficient(1.0f)
                        .build()
        );
        effect.getTransform().setPosition(worldPosition).setScale(1.0f, 1.0f, 1.0f);

        List<PathSegment> segments = buildPathSegments();
        PathAnimator animator = new PathAnimator(segments, true);
        Vector3f localPos = animator.sample(0.0f, new Vector3f());
        penEmitter.getProperty(POSITION).modify(v -> v.set(localPos));
        Vector3f yAxis = new Vector3f(0.0f, 1.0f, 0.0f);
        effect.setConsumer((e, deltaTime) -> {
            e.getTransform().rotate(0.5f * deltaTime, yAxis);
            animator.advanceAndSample(deltaTime, localPos);
            penEmitter.getProperty(POSITION).modify(v -> v.set(localPos));
        });
        return effect;
    }

    private static List<PathSegment> buildPathSegments() {
        List<PathSegment> segments = new ArrayList<>();
        // 外圈圆环
        segments.add(new ArcSegment(OUTER_RING_RADIUS, 0.0f, (float) (Math.PI * 2.0), duration(2.6f)));

        // 两个互成 45 度角的正方形（八芒星）
        addSquareSegments(segments, STAR_SQUARE_CIRCUMRADIUS, (float) (Math.PI * 0.25), duration(0.7f));
        addSquareSegments(segments, STAR_SQUARE_CIRCUMRADIUS, 0.0f, duration(0.7f));

        // 八芒星内部两个同心圆环
        segments.add(new ArcSegment(INNER_RING_RADIUS_LARGE, 0.0f, (float) (Math.PI * 2.0), duration(1.3f)));
        segments.add(new ArcSegment(INNER_RING_RADIUS_SMALL, 0.0f, (float) (Math.PI * 2.0), duration(1.1f)));
        return segments;
    }

    private static float duration(float baseSeconds) {
        return baseSeconds / Math.max(0.05f, DRAW_SPEED_MULTIPLIER);
    }

    private static void addSquareSegments(List<PathSegment> segments, float circumradius, float rotationRad, float sideDuration) {
        float[] xs = new float[4];
        float[] zs = new float[4];
        for (int i = 0; i < 4; i++) {
            float angle = rotationRad + i * ((float) Math.PI * 0.5f);
            xs[i] = (float) Math.cos(angle) * circumradius;
            zs[i] = (float) Math.sin(angle) * circumradius;
        }
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) & 3;
            segments.add(new LineSegment(xs[i], zs[i], xs[next], zs[next], sideDuration));
        }
    }

    private interface PathSegment {
        float duration();

        Vector3f sample(float t, Vector3f out);
    }

    private static final class ArcSegment implements PathSegment {
        private final float radius;
        private final float startAngle;
        private final float endAngle;
        private final float duration;

        private ArcSegment(float radius, float startAngle, float endAngle, float duration) {
            this.radius = radius;
            this.startAngle = startAngle;
            this.endAngle = endAngle;
            this.duration = duration;
        }

        @Override
        public float duration() {
            return this.duration;
        }

        @Override
        public Vector3f sample(float t, Vector3f out) {
            float angle = this.startAngle + (this.endAngle - this.startAngle) * t;
            out.set(
                    (float) Math.cos(angle) * this.radius,
                    0.0f,
                    (float) Math.sin(angle) * this.radius
            );
            return out;
        }
    }

    private static final class LineSegment implements PathSegment {
        private final float x0;
        private final float z0;
        private final float x1;
        private final float z1;
        private final float duration;

        private LineSegment(float x0, float z0, float x1, float z1, float duration) {
            this.x0 = x0;
            this.z0 = z0;
            this.x1 = x1;
            this.z1 = z1;
            this.duration = duration;
        }

        @Override
        public float duration() {
            return this.duration;
        }

        @Override
        public Vector3f sample(float t, Vector3f out) {
            out.set(
                    this.x0 + (this.x1 - this.x0) * t,
                    0.0f,
                    this.z0 + (this.z1 - this.z0) * t
            );
            return out;
        }
    }

    private static final class PathAnimator {
        private final List<PathSegment> segments;
        private final boolean loop;
        private final float totalDuration;
        private float elapsed;

        private PathAnimator(List<PathSegment> segments, boolean loop) {
            this.segments = segments;
            this.loop = loop;
            float durationSum = 0.0f;
            for (PathSegment segment : segments) {
                durationSum += segment.duration();
            }
            this.totalDuration = durationSum;
            this.elapsed = 0.0f;
        }

        private Vector3f advanceAndSample(float deltaTime, Vector3f out) {
            this.elapsed += Math.max(deltaTime, 0.0f);
            return sample(this.elapsed, out);
        }

        private Vector3f sample(float time, Vector3f out) {
            if (this.segments.isEmpty() || this.totalDuration <= 0.0f) {
                return out.set(0.0f);
            }

            float localTime = time;
            if (this.loop) {
                localTime = localTime % this.totalDuration;
            } else if (localTime >= this.totalDuration) {
                return this.segments.get(this.segments.size() - 1).sample(1.0f, out);
            }

            float accumulated = 0.0f;
            for (PathSegment segment : this.segments) {
                float end = accumulated + segment.duration();
                if (localTime <= end) {
                    float segmentDuration = Math.max(segment.duration(), 1.0e-6f);
                    float t = (localTime - accumulated) / segmentDuration;
                    float clampedT = Math.max(0.0f, Math.min(1.0f, t));
                    return segment.sample(clampedT, out);
                }
                accumulated = end;
            }
            return this.segments.get(this.segments.size() - 1).sample(1.0f, out);
        }
    }
}
