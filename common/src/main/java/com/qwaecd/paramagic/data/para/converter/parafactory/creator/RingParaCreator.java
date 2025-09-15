package com.qwaecd.paramagic.data.para.converter.parafactory.creator;

import com.qwaecd.paramagic.core.para.material.ParaMaterial;
import com.qwaecd.paramagic.core.para.material.ParaMaterialProvider;
import com.qwaecd.paramagic.core.para.mesh.ParaMeshProvider;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.data.para.RingParaData;
import com.qwaecd.paramagic.data.para.converter.parafactory.NodeCreator;
import com.qwaecd.paramagic.feature.MagicNode;

public class RingParaCreator implements NodeCreator<RingParaData> {
    private final ParaMeshProvider meshProvider;
    private final ParaMaterialProvider materialProvider;

    public RingParaCreator(ParaMeshProvider meshProvider, ParaMaterialProvider materialProvider) {
        this.meshProvider = meshProvider;
        this.materialProvider = materialProvider;
    }
    @Override
    public MagicNode create(RingParaData data) {
        Mesh mesh = this.meshProvider.rings().get(data.color, data.innerRadius, data.outerRadius, data.segments);
        ParaMaterial material = this.materialProvider.createRingMaterial();
        MagicNode magicNode = new MagicNode(mesh, material);

        magicNode.transform.set(data.position, data.rotation, data.scale);

        return magicNode;
    }
}
