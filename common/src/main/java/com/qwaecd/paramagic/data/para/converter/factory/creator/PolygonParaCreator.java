package com.qwaecd.paramagic.data.para.converter.factory.creator;

import com.qwaecd.paramagic.core.para.material.ParaMaterial;
import com.qwaecd.paramagic.core.para.material.ParaMaterialProvider;
import com.qwaecd.paramagic.core.para.mesh.ParaMeshProvider;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.data.para.converter.factory.NodeCreator;
import com.qwaecd.paramagic.data.para.struct.components.PolygonParaData;
import com.qwaecd.paramagic.feature.MagicNode;

public class PolygonParaCreator implements NodeCreator<PolygonParaData> {
    private final ParaMeshProvider meshProvider;
    private final ParaMaterialProvider materialProvider;

    public PolygonParaCreator(ParaMeshProvider meshProvider, ParaMaterialProvider materialProvider) {
        this.meshProvider = meshProvider;
        this.materialProvider = materialProvider;
    }
    @Override
    public MagicNode create(PolygonParaData data) {
        Mesh mesh = this.meshProvider.polygons().get(data.color, data.radius, data.sides, data.startAngle, data.lineWidth);
        ParaMaterial material = this.materialProvider.createPolygonMaterial();
        MagicNode magicNode = new MagicNode(mesh, material);

        magicNode.transform.set(data.position, data.rotation, data.scale);

        return magicNode;
    }
}
