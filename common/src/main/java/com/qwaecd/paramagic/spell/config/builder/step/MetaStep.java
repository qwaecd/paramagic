package com.qwaecd.paramagic.spell.config.builder.step;

import com.qwaecd.paramagic.spell.config.SpellMetaConfig;

public interface MetaStep {
    PhaseStep withMeta(SpellMetaConfig meta);
}
