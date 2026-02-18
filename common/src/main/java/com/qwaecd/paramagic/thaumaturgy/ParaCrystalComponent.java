package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorMapComponent;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;

import javax.annotation.Nonnull;

public class ParaCrystalComponent implements IDataSerializable {
    @Nonnull
    private ParaData paraData;
    private final OperatorMapComponent operatorMap;

    public ParaCrystalComponent(@Nonnull ParaData paraData) {
        this.paraData = paraData;
        this.operatorMap = new OperatorMapComponent();
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

    public static ParaCrystalComponent fromCodec(DataCodec codec) {
        ParaData paraData = codec.readObject("paraData", ParaData::fromCodec);
        ParaCrystalComponent component = new ParaCrystalComponent(paraData);
        component.operatorMap.updateFromCodec(codec);
        return component;
    }
}
