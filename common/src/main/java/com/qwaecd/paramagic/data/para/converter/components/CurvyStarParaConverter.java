package com.qwaecd.paramagic.data.para.converter.components;

import com.qwaecd.paramagic.data.para.converter.api.ParaComponentConverter;
import com.qwaecd.paramagic.data.para.converter.factory.ParaAssetFactory;
import com.qwaecd.paramagic.data.para.struct.components.CurvyStarParaData;
import com.qwaecd.paramagic.feature.circle.MagicNode;

public class CurvyStarParaConverter implements ParaComponentConverter<CurvyStarParaData> {
    private final ParaAssetFactory assetFactory;

    public CurvyStarParaConverter(ParaAssetFactory assetFactory) {
        this.assetFactory = assetFactory;
    }
    @Override
    public MagicNode convert(CurvyStarParaData data) {
        return this.assetFactory.createNode(data);
    }

    @Override
    public Class<CurvyStarParaData> getDataType() {
        return CurvyStarParaData.class;
    }
}
