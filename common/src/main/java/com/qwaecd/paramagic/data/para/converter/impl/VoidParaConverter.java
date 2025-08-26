package com.qwaecd.paramagic.data.para.converter.impl;

import com.qwaecd.paramagic.data.para.VoidParaData;
import com.qwaecd.paramagic.data.para.converter.ParaConverter;
import com.qwaecd.paramagic.feature.MagicNode;

public class VoidParaConverter implements ParaConverter<VoidParaData> {
    @Override
    public MagicNode convert(VoidParaData data) {
        // 为 null 的节点不会被上传到渲染但是会参与变换
        MagicNode magicNode = new MagicNode(null, null);
        magicNode.transform.set(data.position, data.rotation, data.scale);
        return magicNode;
    }

    @Override
    public Class<VoidParaData> getDataType() {
        return VoidParaData.class;
    }
}
