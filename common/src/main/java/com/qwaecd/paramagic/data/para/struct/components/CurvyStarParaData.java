package com.qwaecd.paramagic.data.para.struct.components;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;

public class CurvyStarParaData  extends ParaComponentData {
    public final float radius;
    public final int sides;
    public final float curvature;
    public final float startAngle;
    public final float lineWidth;

    public CurvyStarParaData(float radius, int sides, float curvature, float startAngle, float lineWidth) {
        super();
        this.radius = radius;
        this.sides = Math.max(sides, 3);
        this.curvature = curvature == 0.0f ? 0.1f : curvature;
        this.startAngle = startAngle;
        this.lineWidth = lineWidth;
    }

    public CurvyStarParaData(float radius, int sides) {
        super();
        this.radius = radius;
        this.sides = Math.max(sides, 3);
        this.curvature = 1.0f;
        this.startAngle = 0.0f;
        this.lineWidth = 4.0f;
    }
}
