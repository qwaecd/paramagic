package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;

public final class ParaContext {
    @Getter
    @Nonnull
    public final ServerSession session;
    @Getter
    @Nonnull
    public final ServerLevel level;
    @Getter
    public final SpellCaster caster;

    public ParaContext(@Nonnull ServerSession session, @Nonnull ServerLevel level, SpellCaster caster) {
        this.session = session;
        this.level = level;
        this.caster = caster;
    }

    public void addOperator(@Nonnull ParaOperator operator) {
    }
}
