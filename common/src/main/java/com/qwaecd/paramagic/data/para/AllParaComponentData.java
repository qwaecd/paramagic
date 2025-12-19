package com.qwaecd.paramagic.data.para;


import com.qwaecd.paramagic.data.para.struct.components.*;

public final class AllParaComponentData {
    public static void registerAll() {
        forceClassInit(VoidParaData.class);
        forceClassInit(RingParaData.class);
        forceClassInit(PolygonParaData.class);
        forceClassInit(CurvyStarParaData.class);
    }

    private static void forceClassInit(Class<?> clazz) {
        try {
            Class.forName(clazz.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to initialize " + clazz.getName(), e);
        }
    }
}
