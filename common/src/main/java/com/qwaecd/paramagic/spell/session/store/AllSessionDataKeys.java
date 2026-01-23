package com.qwaecd.paramagic.spell.session.store;

import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AllSessionDataKeys {
    private static final Map<SessionDataKey<?>, Integer> dataToId = new ConcurrentHashMap<>();
    private static final Map<Integer, SessionDataKey<?>> idToData = new ConcurrentHashMap<>();


    public static final SessionDataKey<Vector3f> firstPosition = register(Vector3f.class, 0);


    public static <T> SessionDataKey<T> register(Class<T> typeClass, int id) {
        if (idToData.containsKey(id)) {
            throw new IllegalStateException("SessionData id " + id + " is already registered for type " + idToData.get(id).typeClass.getName());
        }
        SessionDataKey<T> sessionDataKey = SessionDataKey.of(typeClass, id);
        if (dataToId.containsKey(sessionDataKey)) {
            throw new IllegalStateException("SessionData for type " + typeClass.getName() + " is already registered with id " + dataToId.get(sessionDataKey));
        }
        dataToId.put(sessionDataKey, id);
        idToData.put(id, sessionDataKey);
        return sessionDataKey;
    }

    public static int getId(SessionDataKey<?> sessionDataKey) {
        Integer id = dataToId.get(sessionDataKey);
        if (id == null) {
            throw new IllegalArgumentException("SessionData not registered: " + sessionDataKey.typeClass.getName());
        }
        return id;
    }

    @Nullable
    public static SessionDataKey<?> getById(int id) {
        return idToData.get(id);
    }

    public static boolean contains(int id) {
        return idToData.containsKey(id);
    }

    public static boolean contains(SessionDataKey<?> sessionDataKey) {
        return idToData.containsValue(sessionDataKey);
    }
}
