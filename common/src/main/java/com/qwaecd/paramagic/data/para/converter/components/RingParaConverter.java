package com.qwaecd.paramagic.data.para.converter.components;

import com.qwaecd.paramagic.data.para.converter.api.ParaComponentConverter;
import com.qwaecd.paramagic.data.para.converter.factory.ParaAssetFactory;
import com.qwaecd.paramagic.data.para.struct.components.RingParaData;
import com.qwaecd.paramagic.feature.circle.MagicNode;

public class RingParaConverter implements ParaComponentConverter<RingParaData> {
    private final ParaAssetFactory assetFactory;

    public RingParaConverter(ParaAssetFactory factory) {
        this.assetFactory = factory;
    }
    @Override
    public MagicNode convert(RingParaData data) {
        return this.assetFactory.createNode(data);
    }

    @Override
    public Class<RingParaData> getDataType() {
        return RingParaData.class;
    }
}
