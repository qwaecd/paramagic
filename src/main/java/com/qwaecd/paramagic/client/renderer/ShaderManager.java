package com.qwaecd.paramagic.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.qwaecd.paramagic.Paramagic.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = MODID)
public class ShaderManager {
    private static final ConcurrentMap<ResourceLocation, ShaderInstance> shaders = new ConcurrentHashMap<>();

    /**
     * Get a shader instance by resource location
     */
    public static ShaderInstance getShader(ResourceLocation name) {
        return shaders.get(name);
    }

    /**
     * Register a shader instance
     */
    public static void register(ResourceLocation name, ShaderInstance instance) {
        shaders.put(name, instance);
    }

    /**
     * Check if a shader is registered
     */
    public static boolean hasShader(ResourceLocation name) {
        return shaders.containsKey(name);
    }

    /**
     * Get all registered shader names
     */
    public static java.util.Set<ResourceLocation> getShaderNames() {
        return shaders.keySet();
    }

    /**
     * Clear all shaders (called during resource reload)
     */
    public static void clear() {
        shaders.clear();
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        // Clear existing shaders on reload
        clear();

        // Register magic circle shader
        ResourceLocation magicCircleShader = ResourceLocation.fromNamespaceAndPath(MODID, "magic_circle");
        ShaderInstance magicCircleInstance = new ShaderInstance(
                event.getResourceProvider(),
                magicCircleShader,
                DefaultVertexFormat.NEW_ENTITY
        );
        event.registerShader(magicCircleInstance, instance -> register(magicCircleShader, instance));

        // Register additional shaders here as needed
        // Example for particle effects:
        /*
        ResourceLocation particleShader = new ResourceLocation("paramagic", "magic_particle");
        ShaderInstance particleInstance = new ShaderInstance(
            event.getResourceProvider(),
            particleShader,
            DefaultVertexFormat.PARTICLE
        );
        event.registerShader(particleInstance, instance -> register(particleShader, instance));
        */

        System.out.println("Paramagic shaders registered successfully!");
    }
}