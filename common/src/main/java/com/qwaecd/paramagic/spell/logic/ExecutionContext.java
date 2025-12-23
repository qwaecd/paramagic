package com.qwaecd.paramagic.spell.logic;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;


@SuppressWarnings("ClassCanBeRecord")
public class ExecutionContext {
    @Getter
    public final ServerLevel level;
    @Getter
    public final SpellCaster caster;

    public ExecutionContext(ServerLevel level, SpellCaster caster) {
        this.level = level;
        this.caster = caster;
    }
}
