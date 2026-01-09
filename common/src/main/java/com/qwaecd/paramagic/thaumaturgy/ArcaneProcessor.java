package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.spell.logic.ExecutionContext;
import com.qwaecd.paramagic.spell.logic.SpellLogic;

import javax.annotation.Nonnull;

public class ArcaneProcessor {
    @Nonnull
    private final SpellLogic logic;

    public ArcaneProcessor(@Nonnull SpellLogic logic) {
        this.logic = logic;
    }

    public void process(ExecutionContext context) {

    }
}
