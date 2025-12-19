package com.qwaecd.paramagic.spell.listener;

import com.qwaecd.paramagic.spell.state.phase.struct.SpellPhaseType;

@FunctionalInterface
public interface RenderCondition {
    boolean shouldRender(SpellPhaseType old, SpellPhaseType current);
}
