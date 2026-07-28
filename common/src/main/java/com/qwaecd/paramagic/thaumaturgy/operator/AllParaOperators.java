package com.qwaecd.paramagic.thaumaturgy.operator;

import com.qwaecd.paramagic.thaumaturgy.operator.alpha.DoubleCastOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.alpha.QuadrupleCastOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.alpha.TripleCastOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.flow.BranchOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.flow.CycleOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.flow.VoidOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.AccelerateOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.BounceOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.ExtendLifetimeOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.GradualAccelerationOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.HeavyOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.PierceOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.ShortTrackingOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.ShortenLifetimeOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.TrackingOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.WeightlessOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.projectile.GravityCollapseOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.projectile.LaserOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.projectile.MagicArrowOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class AllParaOperators {
    private static final Map<ParaOpId, Supplier<ParaOperator>> OPERATORS = new HashMap<>();
    private static final Map<String, ParaOpId> ID_MAP = new HashMap<>();

    private AllParaOperators() {}

    public static synchronized void registerOperator(ParaOpId opId, @Nonnull Supplier<ParaOperator> factory) {
        if (OPERATORS.containsKey(opId)) {
            throw new IllegalArgumentException("ParaOperator with id " + opId.getId() + " is already registered.");
        }
        ID_MAP.put(opId.getId().toString(), opId);
        OPERATORS.put(opId, factory);
    }

    public static void registerAll() {
        // flow
        OperatorRegistration
                .builder("void_operator", OperatorType.FLOW)
                .transmissionDelay(-0.025f).cycleCooldown(0.02f).manaCost(1)
                .display("Void Operator", "空参量")
                .describe("Does nothing", "什么也不做")
                .operator(VoidOperator::new)
                .build();
        OperatorRegistration
                .builder("branch_operator", OperatorType.FLOW)
                .transmissionDelay(0.25f).cycleCooldown(0.15f).manaCost(200)
                .display("Branch", "分支")
                .describe("Replays prepared spells in a separate branch", "在独立分支中重演已准备的法术")
                .operator(BranchOperator::new)
                .build();
        OperatorRegistration
                .builder("cycle_operator", OperatorType.FLOW)
                .transmissionDelay(0.0f).cycleCooldown(0.25f).manaCost(100)
                .display("Cycle", "回转")
                .describe("Restarts a completed branch after its delays", "使分支在传导延迟归零后自动重启一次")
                .operator(CycleOperator::new)
                .build();
        // alpha
        OperatorRegistration
                .builder("double_cast_operator", OperatorType.ALPHA)
                .transmissionDelay(0.0f).cycleCooldown(0.0f).manaCost(0)
                .display("Double Cast", "二重施法")
                .describe("Can cast two spells simultaneously", "可以同时释放多个法术")
                .operator(DoubleCastOperator::new)
                .build();
        OperatorRegistration
                .builder("triple_cast_operator", OperatorType.ALPHA)
                .transmissionDelay(0.0f).cycleCooldown(0.0f).manaCost(1)
                .display("Triple Cast", "三重施法")
                .describe("Can cast three spells simultaneously", "可以同时释放多个法术")
                .operator(TripleCastOperator::new)
                .build();
        OperatorRegistration
                .builder("quadruple_cast_operator", OperatorType.ALPHA)
                .transmissionDelay(0.0f).cycleCooldown(0.0f).manaCost(1)
                .display("Quadruple Cast", "四重施法")
                .describe("Can cast four spells simultaneously", "可以同时释放多个法术")
                .operator(QuadrupleCastOperator::new)
                .build();
        // modifier
        OperatorRegistration
                .builder("accelerate_operator", OperatorType.MODIFIER)
                .transmissionDelay(0.04f).cycleCooldown(0.4f).manaCost(35)
                .display("Accelerate", "加速")
                .describe("Accelerates the launch of projectiles", "使投射物加速射出")
                .operator(AccelerateOperator::new)
                .build();
        OperatorRegistration
                .builder("gradual_acceleration_operator", OperatorType.MODIFIER)
                .transmissionDelay(0.08f).cycleCooldown(0.8f).manaCost(90)
                .display("Gradual Acceleration", "逐渐加速")
                .describe("Continuously accelerates projectiles to a higher speed", "让投射物持续向更高速度加速")
                .operator(GradualAccelerationOperator::new)
                .build();
        OperatorRegistration
                .builder("shorten_lifetime_operator", OperatorType.MODIFIER)
                .transmissionDelay(-0.02f).cycleCooldown(-0.02f).manaCost(15)
                .display("Shorten Lifetime", "缩短存在时间")
                .describe("Shortens projectile lifetime", "缩短投射物存在时间")
                .operator(ShortenLifetimeOperator::new)
                .build();
        OperatorRegistration
                .builder("extend_lifetime_operator", OperatorType.MODIFIER)
                .transmissionDelay(0.06f).cycleCooldown(0.4f).manaCost(40)
                .display("Extend Lifetime", "延长存在时间")
                .describe("Extends projectile lifetime", "延长投射物存在时间")
                .operator(ExtendLifetimeOperator::new)
                .build();
        OperatorRegistration
                .builder("heavy_operator", OperatorType.MODIFIER)
                .transmissionDelay(0.05f).cycleCooldown(0.5f).manaCost(45)
                .display("Heavy", "沉重")
                .describe("Makes projectiles fall faster and appear heavier", "让投射物更快下坠并显得更加沉重")
                .operator(HeavyOperator::new)
                .build();
        OperatorRegistration
                .builder("weightless_operator", OperatorType.MODIFIER)
                .transmissionDelay(0.05f).cycleCooldown(0.6f).manaCost(60)
                .display("Weightless", "失重")
                .describe("Significantly reduces the gravity applied to projectiles", "显著降低投射物受到的重力")
                .operator(WeightlessOperator::new)
                .build();
        OperatorRegistration
                .builder("short_tracking_operator", OperatorType.MODIFIER)
                .transmissionDelay(0.14f).cycleCooldown(1.8f).manaCost(170)
                .display("Short Tracking", "短距离追踪")
                .describe("Strongly bends projectiles to track close-range targets", "强力扭转投射物以追向近距离目标")
                .operator(ShortTrackingOperator::new)
                .build();
        OperatorRegistration
                .builder("tracking_operator", OperatorType.MODIFIER)
                .transmissionDelay(0.12f).cycleCooldown(1.6f).manaCost(150)
                .display("Tracking", "追踪")
                .describe("Bends projectiles to track distant targets", "扭转投射物以追向较远目标")
                .operator(TrackingOperator::new)
                .build();
        OperatorRegistration
                .builder("pierce_operator", OperatorType.MODIFIER)
                .transmissionDelay(0.05f).cycleCooldown(0.5f).manaCost(50)
                .display("Pierce", "穿刺")
                .describe("Increases the max entity pierce count by 1", "使投射物最大穿刺次数 +1")
                .describe("Also allows the projectile to damage itself", "同时令投射物能够伤害自己")
                .operator(PierceOperator::new)
                .build();
        OperatorRegistration
                .builder("bounce_operator", OperatorType.MODIFIER)
                .transmissionDelay(0.05f).cycleCooldown(0.5f).manaCost(50)
                .display("Bounce", "弹跳")
                .describe("Increases the max bounce count by 1", "使投射物最大弹跳次数 +1")
                .operator(BounceOperator::new)
                .build();
        // projectile
        OperatorRegistration
                .builder("magic_arrow_operator", OperatorType.PROJECTILE)
                .transmissionDelay(0.05f).cycleCooldown(0.4f).manaCost(30)
                .display("Magic Arrow", "魔法箭")
                .describe("Ordinary arrow", "普通的箭矢")
                .operator(MagicArrowOperator::new)
                .build();
        OperatorRegistration
                .builder("laser_operator", OperatorType.PROJECTILE)
                .transmissionDelay(-0.02f).cycleCooldown(0.6f).manaCost(80)
                .display("Laser", "激光")
                .describe("Fires a high-speed laser with a glowing beam trail", "发射一束高速前进并带有发光束线的激光")
                .operator(LaserOperator::new)
                .build();
        OperatorRegistration
                .builder("gravity_collapse_operator", OperatorType.PROJECTILE)
                .transmissionDelay(2.71828f).cycleCooldown(3.14159f).manaCost(172)
                .display("Gravitational Collapse", "引力坍缩")
                .describe("When you distort the world's magic field to its limit...", "当你将世界的魔力场扭曲至极限时...")
                .operator(GravityCollapseOperator::new)
                .build();

        OperatorRegistration.forEachDescriptor(d -> registerOperator(d.opId, () -> d.operatorFactory.apply(d.opId)));
    }

    @Nullable
    public static ParaOpId getIdByString(String idStr) {
        return ID_MAP.get(idStr);
    }

    @Nullable
    public static ParaOperator createOperator(ParaOpId id) {
        var factory = OPERATORS.get(id);
        return factory == null ? null : factory.get();
    }

    public static boolean contains(ParaOpId id) {
        return OPERATORS.containsKey(id);
    }
}