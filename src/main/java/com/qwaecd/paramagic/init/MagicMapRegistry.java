package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.api.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MagicMapRegistry {
    public static final List<IMagicMap> REGISTRY_MAGICS = new ArrayList<>();
    private static final Map<String, IMagicMap> MAGIC_MAPS = new ConcurrentHashMap<>();
    private static final Map<MagicType, List<IMagicMap>> BY_TYPE = new ConcurrentHashMap<>();

    public static void init() {
        // 初始化Magic类型
        for (MagicType type : MagicType.values()) {
            BY_TYPE.put(type, new ArrayList<>());
        }
        //注册在注册表内的Magic实例
        REGISTRY_MAGICS.forEach(MagicMapRegistry::register);
    }

    public static void register(IMagicMap magicMap) {
        MAGIC_MAPS.put(magicMap.getId(), magicMap);
        BY_TYPE.get(magicMap.getType()).add(magicMap);
    }

    public static IMagicMap get(String id) {
        return MAGIC_MAPS.get(id);
    }

    public static List<IMagicMap> getByType(MagicType type) {
        return new ArrayList<>(BY_TYPE.getOrDefault(type, Collections.emptyList()));
    }

    public static Collection<IMagicMap> getAll() {
        return new ArrayList<>(MAGIC_MAPS.values());
    }

    public static Set<String> getAllIds() {
        return new HashSet<>(MAGIC_MAPS.keySet());
    }

    static {
        //TODO 向待注册列表里添加Magic实例
    }
}
