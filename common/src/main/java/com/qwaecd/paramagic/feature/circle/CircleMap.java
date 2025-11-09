package com.qwaecd.paramagic.feature.circle;

import java.util.HashMap;
import java.util.Map;

public class CircleMap {
    private static final Map<String, MagicCircle> circleMap = new HashMap<>();

    public static void register(String id, MagicCircle circle) {
        if (id == null) {
            throw new NullPointerException("id must not be null");
        }
        if (circle == null) {
            throw new NullPointerException("circle must not be null");
        }
        circleMap.put(id, circle);
    }

    public static MagicCircle unregister(String id) {
        if (id == null) {
            throw new NullPointerException("id must not be null");
        }
        return circleMap.remove(id);
    }

    public static MagicCircle get(String id) {
        return circleMap.get(id);
    }
}
