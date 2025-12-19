package com.qwaecd.paramagic.spell.listener;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.config.phase.PhaseAssetConfig;
import com.qwaecd.paramagic.spell.config.phase.PhaseConfig;
import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;

import java.util.ArrayList;
import java.util.List;

@PlatformScope(PlatformScopeType.CLIENT)
public class ListenerFactoryClient {
    public static List<ISpellPhaseListener> createListenersFromConfig(SpellDefinition definition) {
        List<ISpellPhaseListener> listeners = new ArrayList<>();

        boolean hasAnyPhaseAssets = false;
        for (SpellPhaseType type : SpellPhaseType.values()) {
            PhaseConfig phaseConfig = definition.phases.getPhaseConfig(type);
            PhaseAssetConfig assetConfig = phaseConfig.getAssetConfig();
            if (assetConfig != null && assetConfig.getSpellAssets() != null) {
                hasAnyPhaseAssets = true;
                break;
            }
        }

        if (hasAnyPhaseAssets) {
            listeners.add(new MultiPhaseRenderListener(definition));
        }

        return listeners;
    }
}
