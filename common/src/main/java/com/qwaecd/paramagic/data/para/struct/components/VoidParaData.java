package com.qwaecd.paramagic.data.para.struct.components;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaComponentType;
import com.qwaecd.paramagic.network.DataCodec;

/**
 * Does not have geometry shape component data, only used for organizational structure.
 * <p>
 * 没有几何形状的组件数据，仅用于组织结构。
 */
public class VoidParaData extends ParaComponentData {
    static {
        register(ParaComponentType.VOID.ID(), VoidParaData::fromCodec);
    }
    public VoidParaData() {
        super();
    }
    @Override
    public int getComponentType() {
        return ParaComponentType.VOID.ID();
    }

    public static VoidParaData fromCodec(DataCodec codec) {
        VoidParaData voidParaData = new VoidParaData();
        voidParaData.readBase(codec);
        return voidParaData;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("type", this.getComponentType());
        super.writeBase(codec);
    }
}
