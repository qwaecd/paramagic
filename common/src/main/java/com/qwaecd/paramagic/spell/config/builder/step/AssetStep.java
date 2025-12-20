package com.qwaecd.paramagic.spell.config.builder.step;

import com.qwaecd.paramagic.spell.config.CircleTransformConfig;
import com.qwaecd.paramagic.spell.view.position.CirclePositionRule;
import com.qwaecd.paramagic.spell.view.position.PositionRuleType;
import org.joml.Vector3f;

public interface AssetStep {
    AssetStep positionRule(CirclePositionRule positionRule);
    default AssetStep positionRule(PositionRuleType type, Vector3f offset, boolean lockRotation, Vector3f rotationOffset) {
        return this.positionRule(new CirclePositionRule(type, offset, lockRotation, rotationOffset));
    }

    AssetStep transformConfig(CircleTransformConfig transformConfig);
    default AssetStep transformConfig(Vector3f initialScale, Vector3f initialRotation) {
        return this.transformConfig(new CircleTransformConfig(initialScale, initialRotation));
    }

    PhaseStep endAsset();
}
