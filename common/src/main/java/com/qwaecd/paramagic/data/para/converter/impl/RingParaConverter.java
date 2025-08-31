package com.qwaecd.paramagic.data.para.converter.impl;

import com.qwaecd.paramagic.data.para.RingParaData;
import com.qwaecd.paramagic.data.para.converter.ParaComponentConverter;
import com.qwaecd.paramagic.data.para.converter.parafactory.ParaAssetFactory;
import com.qwaecd.paramagic.feature.MagicNode;

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
