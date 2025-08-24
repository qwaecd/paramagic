package com.qwaecd.paramagic.data.para;

public class RingParaData extends ParaComponentData {
    public final float innerRadius;
    public final float outerRadius;
    public final int segments;
    public RingParaData(String componentId, float innerRadius, float outerRadius, int segments) {
        super(componentId);
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.segments = segments;
    }
}
