package com.qwaecd.paramagic.core.particle.material;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.render.texture.Texture2D;
import com.qwaecd.paramagic.core.render.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ParticleMaterialRegistry {
    public static final int MAX_TEXTURED_MATERIALS = 16;
    public static final ResourceLocation ADDITIVE_UNTEXTURED = new ResourceLocation(Paramagic.MOD_ID, "additive_untextured");

    private static final Map<ResourceLocation, RegisteredMaterial> MATERIALS = new LinkedHashMap<>();
    private static boolean frozen;

    private ParticleMaterialRegistry() {}

    public static synchronized void register(ResourceLocation materialId, ResourceLocation texture, float alphaCutoff) {
        if (frozen) {
            throw new IllegalStateException("GPU particle material registry is frozen.");
        }
        if (materialId.equals(ADDITIVE_UNTEXTURED)) {
            throw new IllegalArgumentException("The additive material id is reserved.");
        }
        if (MATERIALS.containsKey(materialId)) {
            throw new IllegalArgumentException("GPU particle material is already registered: " + materialId);
        }
        if (MATERIALS.size() >= MAX_TEXTURED_MATERIALS) {
            throw new IllegalStateException("GPU particle material capacity exceeded: " + MAX_TEXTURED_MATERIALS);
        }
        if (alphaCutoff < 0.0f || alphaCutoff > 1.0f) {
            throw new IllegalArgumentException("alphaCutoff must be in [0, 1].");
        }
        int slot = MATERIALS.size() + 1;
        MATERIALS.put(materialId, new RegisteredMaterial(slot, materialId, new Texture2D(texture, true), alphaCutoff));
    }

    public static synchronized int resolveSlot(ResourceLocation materialId) {
        if (materialId == null || materialId.equals(ADDITIVE_UNTEXTURED)) {
            return 0;
        }
        RegisteredMaterial material = MATERIALS.get(materialId);
        if (material == null) {
            Paramagic.LOG.warn("Unknown GPU particle material {}; falling back to additive rendering.", materialId);
            return 0;
        }
        return material.slot();
    }

    public static synchronized List<RegisteredMaterial> materials() {
        return List.copyOf(new ArrayList<>(MATERIALS.values()));
    }

    public static synchronized void freeze() {
        frozen = true;
    }

    /** Resolves all registered resources while a client OpenGL context is available. */
    public static synchronized void preload(TextureManager textureManager) {
        for (RegisteredMaterial material : MATERIALS.values()) {
            textureManager.preload(material.texture());
        }
    }

    public record RegisteredMaterial(int slot, ResourceLocation id, Texture2D texture, float alphaCutoff) {}
}
