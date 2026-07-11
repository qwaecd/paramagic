package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.NBTCodec;
import com.qwaecd.paramagic.thaumaturgy.node.ParaSpellTree;
import com.qwaecd.paramagic.thaumaturgy.spelltree.ParaSpellTreeData;

import javax.annotation.Nonnull;

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

    /**
     * Reserved for a future presentation-data extension. Crystal spell logic is stored only in {@link #spellTreeData}.
     */
    @Nonnull
    public ParaData getParaData() {
        return ParaData.EMPTY;
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
