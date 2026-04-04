package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.ProjectileRuntimeModifierHost;
import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.TrackingRuntimeModifier;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class TrackingOperator extends ProjectileOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("tracking_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.025f, 0.06f)
    );

    private static final float TRACKING_RANGE = 18.0f;
    private static final float TRACKING_STRENGTH = 0.1f;

    public TrackingOperator() {
        super(OP_ID, ModItems.TRACKING_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryApplyTracking(projectile));
        return true;
    }

    private void tryApplyTracking(ProjectileEntity projectile) {
        if (!(projectile instanceof ProjectileRuntimeModifierHost modifierHost)) {
            return;
        }
        modifierHost.addRuntimeModifier(new TrackingRuntimeModifier(TRACKING_RANGE, TRACKING_STRENGTH));
    }
}
