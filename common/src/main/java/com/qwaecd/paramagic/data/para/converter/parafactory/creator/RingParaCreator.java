package com.qwaecd.paramagic.data.para.converter.parafactory.creator;

import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.data.para.RingParaData;
import com.qwaecd.paramagic.data.para.converter.parafactory.NodeCreator;
import com.qwaecd.paramagic.data.para.mesh.ParaMeshProvider;
import com.qwaecd.paramagic.feature.MagicNode;

public class RingParaCreator implements NodeCreator<RingParaData> {
    private final ParaMeshProvider meshProvider;

    public RingParaCreator(ParaMeshProvider meshProvider) {
        this.meshProvider = meshProvider;
    }
    @Override
    public MagicNode create(RingParaData data) {
        // TODO: 实现环Para的创建逻辑
        Mesh mesh = this.meshProvider.rings().get(data.color, data.innerRadius, data.outerRadius, data.segments);
        AbstractMaterial material = new Material();
        MagicNode magicNode = new MagicNode(mesh, material);

        magicNode.transform.set(data.position, data.rotation, data.scale);

        return magicNode;
    }
}
