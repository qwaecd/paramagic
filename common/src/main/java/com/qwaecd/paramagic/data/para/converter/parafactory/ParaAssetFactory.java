package com.qwaecd.paramagic.data.para.converter.parafactory;

import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.data.para.ParaComponentData;
import com.qwaecd.paramagic.data.para.RingParaData;
import com.qwaecd.paramagic.data.para.VoidParaData;
import com.qwaecd.paramagic.data.para.converter.parafactory.creator.RingParaCreator;
import com.qwaecd.paramagic.data.para.converter.parafactory.creator.VoidParaCreator;
import com.qwaecd.paramagic.feature.MagicNode;

import java.util.HashMap;
import java.util.Map;

public class ParaAssetFactory {
    private final Map<String, Mesh> meshCache;
    private final Map<Class<? extends ParaComponentData>, NodeCreator<?>> nodeFactories;

    public ParaAssetFactory() {
        this.meshCache = new HashMap<>();
        this.nodeFactories = new HashMap<>();
        registerDefaultFactories();
    }

    private void registerDefaultFactories() {
        registerFactory(VoidParaData.class, new VoidParaCreator());
        registerFactory(RingParaData.class, new RingParaCreator());
    }

    public <T extends ParaComponentData> void registerFactory(Class<T> type, NodeCreator<T> factory) {
        nodeFactories.put(type, factory);
    }

    public MagicNode createNode(ParaComponentData data) {
        @SuppressWarnings("unchecked")
        final NodeCreator<ParaComponentData> factory = (NodeCreator<ParaComponentData>) nodeFactories.get(data.getClass());
        if (factory != null) {
            return factory.create(data);
        }
        throw new IllegalStateException("No factory registered for type: " + data.getClass());
    }
}
