package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.api.IMagicMap;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MagicMapRegistry {
    private static final Map<ResourceLocation, IMagicMap> MAGIC_MAPS = new HashMap<>();
    private static final Map<String, IMagicMap> ID_MAP = new HashMap<>();

    public static void init() {
        // Register built-in magic maps here
        registerBuiltinMaps();
    }

    public static void register(ResourceLocation location, IMagicMap magicMap) {
        MAGIC_MAPS.put(location, magicMap);
        ID_MAP.put(magicMap.getId(), magicMap);
    }

    public static Optional<IMagicMap> get(ResourceLocation location) {
        return Optional.ofNullable(MAGIC_MAPS.get(location));
    }

    public static Optional<IMagicMap> getById(String id) {
        return Optional.ofNullable(ID_MAP.get(id));
    }

    private static void registerBuiltinMaps() {
        // Placeholder for built-in magic maps
        // register(new ResourceLocation(NeoParamagicMod.MODID, "basic_light"), new BasicLightMap());
    }
}
