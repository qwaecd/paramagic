package com.qwaecd.paramagic.spell.builder.step;

import com.qwaecd.paramagic.spell.config.CircleAssets;
import com.qwaecd.paramagic.spell.view.position.CirclePositionRule;
import com.qwaecd.paramagic.spell.view.position.PositionRuleType;
import org.joml.Vector3f;

public interface AssetStart {
    // 可选
    AssetStart circleAssets(CircleAssets assets);
    PositionRequired positionRule(CirclePositionRule rule);
    default PositionRequired positionRule(PositionRuleType t, Vector3f offset, boolean lockRot, Vector3f rotOffset) {
        return positionRule(new CirclePositionRule(t, offset, lockRot, rotOffset));
    }
}
