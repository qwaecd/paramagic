package com.qwaecd.paramagic.spell.logic;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;


@SuppressWarnings("ClassCanBeRecord")
public class ExecutionContext {
    @Getter
    @Nonnull
    public final ServerSession serverSession;
    @Getter
    @Nonnull
    public final ServerLevel level;
    @Getter
    public final SpellCaster caster;

    public ExecutionContext(@Nonnull ServerSession session, @Nonnull ServerLevel level, SpellCaster caster) {
        this.serverSession = session;
        this.level = level;
        this.caster = caster;
    }
}
