package com.qwaecd.paramagic.data.para.converter.components;

import com.qwaecd.paramagic.data.para.struct.components.PolygonParaData;
import com.qwaecd.paramagic.data.para.converter.api.ParaComponentConverter;
import com.qwaecd.paramagic.data.para.converter.factory.ParaAssetFactory;
import com.qwaecd.paramagic.feature.MagicNode;

public class PolygonParaConverter implements ParaComponentConverter<PolygonParaData> {
    private final ParaAssetFactory assetFactory;

    public PolygonParaConverter(ParaAssetFactory assetFactory) {
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
