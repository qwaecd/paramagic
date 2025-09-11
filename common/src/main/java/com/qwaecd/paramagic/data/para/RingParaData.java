package com.qwaecd.paramagic.data.para;

public class RingParaData extends ParaComponentData {
    public final float innerRadius;
    public final float outerRadius;
    public final int segments;
    public RingParaData(float innerRadius, float outerRadius, int segments) {
        super();
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.segments = segments;
    }
}
