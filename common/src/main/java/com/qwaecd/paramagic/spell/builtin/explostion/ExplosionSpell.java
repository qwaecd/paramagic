package com.qwaecd.paramagic.spell.builtin.explostion;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.builder.MachineBuilder;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpell;
import com.qwaecd.paramagic.spell.config.PhaseConfig;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;

import javax.annotation.Nonnull;

@PlatformScope(PlatformScopeType.COMMON)
public class ExplosionSpell implements BuiltinSpell {
    public static final BuiltinSpellId SPELL_ID = new BuiltinSpellId(Paramagic.MOD_ID, "explosion");

    public ExplosionSpell() {
    }

    @Override
    @Nonnull
    public SpellStateMachine createMachine() {
        return new MachineBuilder(PhaseConfig.create(SpellPhaseType.IDLE, 0.1f))
                .phase(SpellPhaseType.CASTING, 3.0f)
                .phase(SpellPhaseType.CHANNELING, 8.0f)
                .phase(SpellPhaseType.COOLDOWN, 0.0f)
                .build();
    }
}
