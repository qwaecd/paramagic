package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.api.IMagicMap;
import com.qwaecd.paramagic.api.MagicMapType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MagicMapRegistry {
    private static final Map<String, IMagicMap> MAGIC_MAPS = new ConcurrentHashMap<>();
    private static final Map<MagicMapType, List<IMagicMap>> BY_TYPE = new ConcurrentHashMap<>();

    public static void init() {
        // Initialize type lists
        for (MagicMapType type : MagicMapType.values()) {
            BY_TYPE.put(type, new ArrayList<>());
        }

        // Register built-in magic maps
//        register(new FireballMagicMap());
//        register(new HealMagicMap());
//        register(new GetNearbyEntitiesMap());
//        register(new GetPlayerPositionMap());
//        register(new TeleportMagicMap());
//        register(new PlaceBlockMagicMap());
    }

    public static void register(IMagicMap magicMap) {
        MAGIC_MAPS.put(magicMap.getId(), magicMap);
        BY_TYPE.get(magicMap.getType()).add(magicMap);
    }

    public static IMagicMap get(String id) {
        return MAGIC_MAPS.get(id);
    }

    public static List<IMagicMap> getByType(MagicMapType type) {
        return new ArrayList<>(BY_TYPE.getOrDefault(type, Collections.emptyList()));
    }

    public static Collection<IMagicMap> getAll() {
        return new ArrayList<>(MAGIC_MAPS.values());
    }

    public static Set<String> getAllIds() {
        return new HashSet<>(MAGIC_MAPS.keySet());
    }
}
