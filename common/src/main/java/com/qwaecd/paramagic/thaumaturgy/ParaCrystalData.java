package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorMap;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;

import javax.annotation.Nonnull;

public class ParaCrystalData implements IDataSerializable {
    @Nonnull
    private ParaData paraData;
    private final OperatorMap operatorMap;

    public ParaCrystalData(@Nonnull ParaData paraData) {
        this.paraData = paraData;
        this.operatorMap = new OperatorMap();
    }

    @Nonnull
    public ParaData getParaData() {
        return this.paraData;
    }

    public void setParaData(@Nonnull ParaData paraData) {
        this.paraData = paraData;
    }

    public ParaOpId getOperatorId(String path) {
        return this.operatorMap.get(path);
    }

    public void putOperator(String path, @Nonnull ParaOperator op) {
        this.operatorMap.put(path, op.id);
    }

    public void removeOperator(String path) {
        this.operatorMap.remove(path);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeObject("paraData", this.paraData);
        codec.writeObject("operatorMap", this.operatorMap);
    }

    public static ParaCrystalData fromCodec(DataCodec codec) {
        ParaData paraData = codec.readObject("paraData", ParaData::fromCodec);
        OperatorMap map = codec.readObject("operatorMap", OperatorMap::fromCodec);
        ParaCrystalData component = new ParaCrystalData(paraData);
        component.operatorMap.set(map);
        return component;
    }
}
