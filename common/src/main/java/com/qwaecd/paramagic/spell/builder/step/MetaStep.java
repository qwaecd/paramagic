package com.qwaecd.paramagic.spell.builder.step;

import com.qwaecd.paramagic.spell.config.SpellMetaConfig;

public interface MetaStep {
    PhaseStep withMeta(SpellMetaConfig meta);
}
