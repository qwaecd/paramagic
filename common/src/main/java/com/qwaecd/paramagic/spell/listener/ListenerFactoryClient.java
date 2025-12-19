package com.qwaecd.paramagic.spell.listener;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.phase.PhaseFactory;
import com.qwaecd.paramagic.spell.phase.SpellPhase;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.config.phase.PhaseAssetConfig;

import java.util.ArrayList;
import java.util.List;

@PlatformScope(PlatformScopeType.CLIENT)
public class ListenerFactoryClient {
    public static List<ISpellPhaseListener> createListenersFromConfig(SpellDefinition definition) {
        List<ISpellPhaseListener> listeners = new ArrayList<>();

        boolean hasAnyPhaseAssets = false;
        for (SpellPhaseType type : SpellPhaseType.values()) {
            SpellPhase phase = PhaseFactory.createPhaseFromConfig(definition.phases.getPhaseConfig(type));
            if (phase != null) {
                PhaseAssetConfig assetConfig = phase.getConfig().getAssetConfig();
                if (assetConfig != null && assetConfig.getSpellAssets() != null) {
                    hasAnyPhaseAssets = true;
                    break;
                }
            }
        }

        if (hasAnyPhaseAssets) {
            listeners.add(new MultiPhaseRenderListener(definition));
        }

        return listeners;
    }
}
