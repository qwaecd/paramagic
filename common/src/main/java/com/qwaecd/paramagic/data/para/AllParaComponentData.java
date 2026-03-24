package com.qwaecd.paramagic.data.para;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaComponentType;
import com.qwaecd.paramagic.data.para.struct.components.CurvyStarParaData;
import com.qwaecd.paramagic.data.para.struct.components.PolygonParaData;
import com.qwaecd.paramagic.data.para.struct.components.RingParaData;
import com.qwaecd.paramagic.data.para.struct.components.VoidParaData;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.platform.Services;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class AllParaComponentData {
    private static final Map<Integer, Entry> REGISTRY = new LinkedHashMap<>();

    public static final class Entry {
        private final int componentType;
        private final Component displayName;
        private final Function<DataCodec, ? extends ParaComponentData> factory;

        private Entry(int componentType, Component displayName, Function<DataCodec, ? extends ParaComponentData> factory) {
            this.componentType = componentType;
            this.displayName = displayName;
            this.factory = factory;
        }

        public int getComponentType() {
            return this.componentType;
        }

        public Component getDisplayName() {
            return this.displayName;
        }

        public Function<DataCodec, ? extends ParaComponentData> getFactory() {
            return this.factory;
        }
    }

    private AllParaComponentData() {
    }

    public static synchronized void register(
            int componentType,
            String displayName,
            Function<DataCodec, ? extends ParaComponentData> constructor
    ) {
        register(componentType, Component.literal(displayName), constructor);
    }

    public static synchronized void register(
            int componentType,
            Component displayName,
            Function<DataCodec, ? extends ParaComponentData> constructor
    ) {
        if (REGISTRY.containsKey(componentType)) {
            String message = "ParaComponentData type " + componentType + " is already registered.";
            if (Services.PLATFORM.isDevelopmentEnvironment()) {
                throw new IllegalStateException(message);
            }
            Paramagic.LOG.error(message);
            return;
        }
        REGISTRY.put(componentType, new Entry(componentType, displayName, constructor));
    }

    public static boolean contains(int componentType) {
        return REGISTRY.containsKey(componentType);
    }

    @Nullable
    public static Entry get(int componentType) {
        return REGISTRY.get(componentType);
    }

    public static Entry require(int componentType) {
        Entry entry = get(componentType);
        if (entry == null) {
            throw new UnsupportedOperationException("Unsupported ParaComponentData type: " + componentType);
        }
        return entry;
    }

    public static Collection<Entry> getEntriesView() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static void registerAll() {
        register(ParaComponentType.VOID.ID(), "VOID", VoidParaData::fromCodec);
        register(ParaComponentType.RING.ID(), "RING", RingParaData::fromCodec);
        register(ParaComponentType.POLYGON.ID(), "POLYGON", PolygonParaData::fromCodec);
        register(ParaComponentType.CURVY_STAR.ID(), "CURVY_STAR", CurvyStarParaData::fromCodec);
    }
}
