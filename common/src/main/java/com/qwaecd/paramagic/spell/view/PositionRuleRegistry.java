package com.qwaecd.paramagic.spell.view;

import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.TransformSample;
import com.qwaecd.paramagic.spell.view.position.PositionRule;
import com.qwaecd.paramagic.spell.view.position.PositionRuleContext;
import com.qwaecd.paramagic.spell.view.position.PositionRuleSpec;
import com.qwaecd.paramagic.spell.view.position.PositionRuleType;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class PositionRuleRegistry {
    private static final Map<PositionRuleType, PositionRuleFactory> REGISTRY = new EnumMap<>(PositionRuleType.class);

    static {
        register(PositionRuleType.FOLLOW_CASTER_FEET, spec -> new FollowAnchorRule(spec, Anchor.FEET));
        register(PositionRuleType.FOLLOW_CASTER_EYE, spec -> new FollowAnchorRule(spec, Anchor.EYE));
        register(PositionRuleType.FIXED_AT_CASTER_FEET, spec -> new FixedAnchorRule(spec, Anchor.FEET));
        register(PositionRuleType.IN_FRONT_OF_CASTER, BillboardRule::new);
    }

    private PositionRuleRegistry() {
    }

    public static void register(PositionRuleType type, PositionRuleFactory factory) {
        REGISTRY.put(
                Objects.requireNonNull(type, "type must not be null"),
                Objects.requireNonNull(factory, "factory must not be null")
        );
    }

    public static PositionRule create(PositionRuleSpec spec) {
        PositionRuleFactory factory = REGISTRY.get(spec.getType());
        if (factory == null) {
            throw new IllegalArgumentException("No PositionRule registered for type: " + spec.getType());
        }
        return factory.create(spec);
    }

    public enum Anchor {
        FEET, EYE
    }

    @FunctionalInterface
    public interface PositionRuleFactory {
        PositionRule create(PositionRuleSpec spec);
    }

    public static class FollowAnchorRule implements PositionRule {
        private final Anchor anchor;
        private final Vector3f offset;

        FollowAnchorRule(PositionRuleSpec spec, Anchor anchor) {
            this.anchor = anchor;
            this.offset = spec.getOffset();
        }

        @Override
        public void onAttach(PositionRuleContext ctx) {
            this.apply(ctx);
        }

        @Override
        public void apply(PositionRuleContext ctx) {
            TransformSample sample = ctx.casterTransform();
            Vector3f base = anchor == Anchor.EYE ? sample.eyePosition : sample.position;
            ctx.circle().getTransform().setPosition(
                    base.x + offset.x,
                    base.y + offset.y,
                    base.z + offset.z
            );
        }

        @Override
        public boolean needsContinuousUpdate() {
            return true;
        }
    }

    public static class FixedAnchorRule implements PositionRule {
        private final Anchor anchor;
        private final Vector3f offset;
        private final Vector3f cachedPosition = new Vector3f();
        private boolean initialized = false;

        FixedAnchorRule(PositionRuleSpec spec, Anchor anchor) {
            this.anchor = anchor;
            this.offset = spec.getOffset();
        }

        @Override
        public void onAttach(PositionRuleContext ctx) {
            bake(ctx.casterTransform());
            ctx.circle().getTransform().setPosition(cachedPosition);
        }

        @Override
        public void apply(PositionRuleContext ctx) {
            if (!initialized) {
                onAttach(ctx);
            } else {
                ctx.circle().getTransform().setPosition(cachedPosition);
            }
        }

        @Override
        public boolean needsContinuousUpdate() {
            return false;
        }

        private void bake(TransformSample sample) {
            Vector3f base = anchor == Anchor.EYE ? sample.eyePosition : sample.position;
            this.cachedPosition.set(base).add(offset);
            this.initialized = true;
        }
    }

    public static class BillboardRule implements PositionRule {
        private final boolean lockRotation;
        private final float distance;

        BillboardRule(PositionRuleSpec spec) {
            this.lockRotation = spec.isLockRotation();
            this.distance = spec.getOffset().length();
        }

        @Override
        public void onAttach(PositionRuleContext ctx) {
            this.apply(ctx);
        }

        @Override
        public void apply(PositionRuleContext ctx) {
            TransformSample sample = ctx.casterTransform();
            Vector3f lookDir = sample.forward;
            Transform transform = ctx.circle().getTransform()
                    .setPosition(sample.eyePosition)
                    .translate(
                            lookDir.x * distance,
                            lookDir.y * distance,
                            lookDir.z * distance
                    );
            if (!lockRotation) {
                Quaternionf temped = ctx.tempQuat();
                transform.getRotation(temped);
                temped.rotationTo(0, 1, 0, lookDir.x, lookDir.y, lookDir.z);
                transform.setRotation(temped);
            }
        }

        @Override
        public boolean needsContinuousUpdate() {
            return !lockRotation;
        }
    }
}
