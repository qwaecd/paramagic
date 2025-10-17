package com.qwaecd.paramagic.data.para.converter.factory;

import com.qwaecd.paramagic.core.para.material.ParaMaterialProvider;
import com.qwaecd.paramagic.core.para.mesh.ParaMeshProvider;
import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.components.PolygonParaData;
import com.qwaecd.paramagic.data.para.struct.components.RingParaData;
import com.qwaecd.paramagic.data.para.struct.components.VoidParaData;
import com.qwaecd.paramagic.data.para.converter.factory.creator.PolygonCreator;
import com.qwaecd.paramagic.data.para.converter.factory.creator.RingParaCreator;
import com.qwaecd.paramagic.data.para.converter.factory.creator.VoidParaCreator;
import com.qwaecd.paramagic.feature.MagicNode;

import java.util.HashMap;
import java.util.Map;

public class ParaAssetFactory {
    private final ParaMeshProvider paraMeshProvider;
    private final ParaMaterialProvider paraMaterialProvider;
    private final Map<Class<? extends ParaComponentData>, NodeCreator<?>> nodeFactories;

    public ParaAssetFactory() {
        this.paraMeshProvider = new ParaMeshProvider();
        this.paraMaterialProvider = new ParaMaterialProvider();
        this.nodeFactories = new HashMap<>();
        registerDefaultFactories();
    }

    private void registerDefaultFactories() {
        registerFactory(VoidParaData.class, new VoidParaCreator());
        registerFactory(RingParaData.class, new RingParaCreator(this.paraMeshProvider, this.paraMaterialProvider));
        registerFactory(PolygonParaData.class, new PolygonCreator(this.paraMeshProvider, this.paraMaterialProvider));
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
