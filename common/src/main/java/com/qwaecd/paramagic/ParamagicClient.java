package com.qwaecd.paramagic;

import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.shader.AllShaders;
import com.qwaecd.paramagic.particle.api.EmitterFactoryRegistry;
import com.qwaecd.paramagic.particle.client.ClientEffectRepository;
import com.qwaecd.paramagic.spell.builtin.client.BuiltinSpellVisualRegistry;
import com.qwaecd.paramagic.spell.session.client.ClientSessionManager;

public class ParamagicClient {
    public static void initOnClient() {
        EmitterFactoryRegistry.registerAll();
        AllShaders.registerAllShaders();
        ModRenderSystem.init();
        ClientSessionManager.init();
        ClientEffectRepository.init();
        BuiltinSpellVisualRegistry.init();
    }
}
