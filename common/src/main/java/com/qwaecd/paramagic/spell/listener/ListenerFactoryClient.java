package com.qwaecd.paramagic.spell.listener;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.spell.state.phase.struct.SpellPhaseType;
import com.qwaecd.paramagic.spell.struct.SpellConfig;
import com.qwaecd.paramagic.spell.struct.phase.PhaseAssetConfig;

import java.util.ArrayList;
import java.util.List;

@PlatformScope(PlatformScopeType.CLIENT)
public class ListenerFactoryClient {
    public static List<ISpellPhaseListener> createListenersFromConfig(SpellConfig config) {
        List<ISpellPhaseListener> listeners = new ArrayList<>();

        boolean hasAnyPhaseAssets = false;
        for (SpellPhaseType type : SpellPhaseType.values()) {
            SpellPhase phase = config.getPhase(type);
            if (phase != null) {
                PhaseAssetConfig assetConfig = phase.getConfig().getAssetConfig();
                if (assetConfig != null && assetConfig.getSpellAssets() != null) {
                    hasAnyPhaseAssets = true;
                    break;
                }
            }
        }

        if (hasAnyPhaseAssets) {
            listeners.add(new MultiPhaseRenderListener(config));
        }

        return listeners;
    }
}
