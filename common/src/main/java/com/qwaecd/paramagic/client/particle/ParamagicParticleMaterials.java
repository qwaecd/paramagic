package com.qwaecd.paramagic.client.particle;

import com.qwaecd.paramagic.core.particle.material.ParticleMaterialRegistry;
import com.qwaecd.paramagic.tools.ModRL;
import net.minecraft.resources.ResourceLocation;

/** Built-in cutout materials backed by {@code assets/paramagic/textures/particle}. */
public final class ParamagicParticleMaterials {
    public static final ResourceLocation PARTICLE_0 = ModRL.inModSpace("particle/particle_0");
    public static final ResourceLocation PARTICLE_1 = ModRL.inModSpace("particle/particle_1");

    private ParamagicParticleMaterials() {}

    public static void registerAll() {
        ParticleMaterialRegistry.register(
                PARTICLE_0,
                ModRL.inModSpace("textures/particle/particle_0.png"),
                0.5f
        );
        ParticleMaterialRegistry.register(
                PARTICLE_1,
                ModRL.inModSpace("textures/particle/particle_1.png"),
                0.5f
        );
    }
}
