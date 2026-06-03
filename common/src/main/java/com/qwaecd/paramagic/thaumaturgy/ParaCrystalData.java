package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.NBTCodec;
import com.qwaecd.paramagic.thaumaturgy.node.ParaSpellTree;
import com.qwaecd.paramagic.thaumaturgy.spelltree.ParaSpellTreeData;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorMap;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ParaCrystalData implements IDataSerializable {
    private static final String SPELL_TREE_DATA_KEY = "spellTreeData";

    @Nonnull
    private ParaSpellTreeData spellTreeData;

    public ParaCrystalData() {
        this(ParaSpellTreeData.empty());
    }

    public ParaCrystalData(@Nonnull ParaSpellTreeData spellTreeData) {
        this.spellTreeData = spellTreeData;
    }

    public ParaCrystalData(@Nonnull ParaData paraData) {
        this(ParaSpellTreeData.empty());
    }

    @Nonnull
    public ParaData getParaData() {
        return this.createParaData();
    }

    public void setParaData(@Nonnull ParaData paraData) {
        this.spellTreeData = ParaSpellTreeData.empty();
    }

    @Nonnull
    public ParaSpellTreeData getSpellTreeData() {
        return this.spellTreeData;
    }

    public void setSpellTreeData(@Nonnull ParaSpellTreeData spellTreeData) {
        this.spellTreeData = spellTreeData;
    }

    @Nonnull
    public ParaSpellTree createRuntimeTree() {
        return new ParaSpellTree(this.spellTreeData);
    }

    @Nonnull
    public ParaData createParaData() {
        return ParaData.EMPTY;
    }

    @Nonnull
    public OperatorMap createOperatorMap() {
        return new OperatorMap();
    }

    @Nullable
    public ParaOpId getOperatorId(String path) {
        return this.createOperatorMap().get(path);
    }

    public void putOperator(String path, @Nonnull ParaOperator op) {
    }

    public void removeOperator(String path) {
    }

    @Nonnull
    public List<OperatorMap.Entry> getOperatorEntriesSnapshot() {
        return this.createOperatorMap().getEntriesSnapshot();
    }

    public void clearOperators() {
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeObject(SPELL_TREE_DATA_KEY, this.spellTreeData);
    }

    public static ParaCrystalData fromCodec(DataCodec codec) {
        if (codec instanceof NBTCodec nbtCodec && !nbtCodec.getTag().contains(SPELL_TREE_DATA_KEY)) {
            return new ParaCrystalData();
        }
        ParaSpellTreeData spellTreeData = codec.readObject(SPELL_TREE_DATA_KEY, ParaSpellTreeData::fromCodec);
        return new ParaCrystalData(spellTreeData);
    }
}
