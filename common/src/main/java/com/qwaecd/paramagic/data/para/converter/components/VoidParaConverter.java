package com.qwaecd.paramagic.data.para.converter.components;

import com.qwaecd.paramagic.data.para.struct.components.VoidParaData;
import com.qwaecd.paramagic.data.para.converter.api.ParaComponentConverter;
import com.qwaecd.paramagic.data.para.converter.factory.ParaAssetFactory;
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
