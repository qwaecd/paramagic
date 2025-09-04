package com.qwaecd.paramagic.data.para.converter.impl;

import com.qwaecd.paramagic.data.para.VoidParaData;
import com.qwaecd.paramagic.data.para.converter.ParaComponentConverter;
import com.qwaecd.paramagic.data.para.converter.parafactory.ParaAssetFactory;
import com.qwaecd.paramagic.feature.MagicNode;

public class VoidParaConverter implements ParaComponentConverter<VoidParaData> {
    private final ParaAssetFactory assetFactory;

    public VoidParaConverter(ParaAssetFactory factory) {
        this.assetFactory = factory;
    }
    @Override
    public MagicNode convert(VoidParaData data) {
        return this.assetFactory.createNode(data);
    }

    @Override
    public Class<VoidParaData> getDataType() {
        return VoidParaData.class;
    }
}
