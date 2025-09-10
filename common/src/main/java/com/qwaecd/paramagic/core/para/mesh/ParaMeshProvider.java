package com.qwaecd.paramagic.core.para.mesh;


public class ParaMeshProvider {
    private final RingMeshGenerator ringMeshGenerator;
    private final PolygonMeshGenerator polygonMeshGenerator;

    public ParaMeshProvider() {
        this.ringMeshGenerator = new RingMeshGenerator();
        this.polygonMeshGenerator = new PolygonMeshGenerator();
    }

    public RingMeshGenerator rings() {
        return this.ringMeshGenerator;
    }
    public PolygonMeshGenerator polygons() {
        return this.polygonMeshGenerator;
    }
}
