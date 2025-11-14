package com.qwaecd.paramagic.data.para.struct.components;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaComponentType;
import com.qwaecd.paramagic.network.DataCodec;

public class CurvyStarParaData extends ParaComponentData {
    public final float radius;
    public final int sides;
    public final float curvature;
    public final float startAngle;
    public final float lineWidth;

    static {
        register(ParaComponentType.CURVY_STAR.ID(), CurvyStarParaData::fromCodec);
    }

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

    @Override
    public int getComponentType() {
        return ParaComponentType.CURVY_STAR.ID();
    }

    public static ParaComponentData fromCodec(DataCodec codec) {
        float radius = codec.readFloat("r");
        int sides = codec.readInt("sides");
        float curvature = codec.readFloat("curv");
        float startAngle = codec.readFloat("startAng");
        float lineWidth = codec.readFloat("lineW");
        CurvyStarParaData curvyStarParaData = new CurvyStarParaData(radius, sides, curvature, startAngle, lineWidth);
        curvyStarParaData.readBase(codec);
        return curvyStarParaData;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("type", this.getComponentType());
        codec.writeFloat("r", this.radius);
        codec.writeInt("sides", this.sides);
        codec.writeFloat("curv", this.curvature);
        codec.writeFloat("startAng", this.startAngle);
        codec.writeFloat("lineW", this.lineWidth);
        super.writeBase(codec);
    }
}
