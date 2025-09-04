package com.qwaecd.paramagic.data.para;

public class PolygonParaData extends ParaComponentData {
    public final float radius;
    public final int sides;
    public PolygonParaData(String componentId, float radius, int sides) {
        super(componentId);
        this.radius = radius;
        this.sides = sides;
    }
}
