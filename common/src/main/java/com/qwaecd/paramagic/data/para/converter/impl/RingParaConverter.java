package com.qwaecd.paramagic.data.para.converter.impl;

import com.qwaecd.paramagic.data.para.RingParaData;
import com.qwaecd.paramagic.data.para.converter.ParaComponentConverter;
import com.qwaecd.paramagic.feature.MagicNode;

public class RingParaConverter implements ParaComponentConverter<RingParaData> {
    @Override
    public MagicNode convert(RingParaData data) {
        return null;
    }

    @Override
    public Class<RingParaData> getDataType() {
        return RingParaData.class;
    }
}
