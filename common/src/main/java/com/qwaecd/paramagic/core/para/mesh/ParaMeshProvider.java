package com.qwaecd.paramagic.core.para.mesh;


import com.qwaecd.paramagic.core.render.vertex.VertexAttributeEnum;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;

public class ParaMeshProvider {
    private final RingMeshGenerator ringMeshGenerator;
    private final PolygonMeshGenerator polygonMeshGenerator;
    private final CurvyStarMeshGenerator curvyStarMeshGenerator;

    static final VertexLayout DEFAULT_LAYOUT_POS_COLOR = new VertexLayout()
            .addNextAttribute(VertexAttributeEnum.POSITION.get())
            .addNextAttribute(VertexAttributeEnum.COLOR.get());

    public ParaMeshProvider() {
        this.ringMeshGenerator = new RingMeshGenerator();
        this.polygonMeshGenerator = new PolygonMeshGenerator();
        this.curvyStarMeshGenerator = new CurvyStarMeshGenerator();
    }

    public RingMeshGenerator rings() {
        return this.ringMeshGenerator;
    }
    public PolygonMeshGenerator polygons() {
        return this.polygonMeshGenerator;
    }

    public CurvyStarMeshGenerator curvyStars() {
        return this.curvyStarMeshGenerator;
    }
}
