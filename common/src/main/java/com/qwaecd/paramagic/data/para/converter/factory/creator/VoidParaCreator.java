package com.qwaecd.paramagic.data.para.converter.factory.creator;

import com.qwaecd.paramagic.data.para.struct.components.VoidParaData;
import com.qwaecd.paramagic.data.para.converter.factory.NodeCreator;
import com.qwaecd.paramagic.feature.MagicNode;

public class VoidParaCreator implements NodeCreator<VoidParaData> {
    @Override
    public MagicNode create(VoidParaData data) {
        // 为 null 的节点不会被上传到渲染但是会参与变换
        MagicNode magicNode = new MagicNode(null, null);
        magicNode.transform.set(data.position, data.rotation, data.scale);
        return magicNode;
    }
}
