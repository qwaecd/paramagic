package com.qwaecd.paramagic.core.para.mesh;


public class ParaMeshProvider {
    private final RingMeshGenerator ringMeshGenerator;

    public ParaMeshProvider() {
        this.ringMeshGenerator = new RingMeshGenerator();
    }

    public RingMeshGenerator rings() {
        return this.ringMeshGenerator;
    }
}
