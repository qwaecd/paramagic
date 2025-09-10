package com.qwaecd.paramagic.data.para.converter.impl;

import com.qwaecd.paramagic.data.para.PolygonParaData;
import com.qwaecd.paramagic.data.para.converter.ParaComponentConverter;
import com.qwaecd.paramagic.data.para.converter.parafactory.ParaAssetFactory;
import com.qwaecd.paramagic.feature.MagicNode;

public class PolygonConverter implements ParaComponentConverter<PolygonParaData> {
    private final ParaAssetFactory assetFactory;

    public PolygonConverter(ParaAssetFactory assetFactory) {
        this.assetFactory = assetFactory;
    }

    @Override
    public MagicNode convert(PolygonParaData data) {
        return this.assetFactory.createNode(data);
    }

    @Override
    public Class<PolygonParaData> getDataType() {
        return PolygonParaData.class;
    }
}
