package com.qwaecd.paramagic.data.para.struct.components;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaComponentType;
import com.qwaecd.paramagic.network.DataCodec;

public class RingParaData extends ParaComponentData {
    public final float innerRadius;
    public final float outerRadius;
    public final int segments;
    static {
        register(ParaComponentType.RING.ID(), RingParaData::fromCodec);
    }

    public RingParaData(float innerRadius, float outerRadius, int segments) {
        super();
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.segments = segments;
    }

    @Override
    public int getComponentType() {
        return ParaComponentType.RING.ID();
    }

    public static ParaComponentData fromCodec(DataCodec codec) {
        float innerR = codec.readFloat("inR");
        float outerR = codec.readFloat("outR");
        int segs = codec.readInt("segs");
        RingParaData ringParaData = new RingParaData(innerR, outerR, segs);
        ringParaData.readBase(codec);
        return ringParaData;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("type", this.getComponentType());
        codec.writeFloat("inR", this.innerRadius);
        codec.writeFloat("outR", this.outerRadius);
        codec.writeInt("segs", this.segments);
        super.writeBase(codec);
    }
}
