package com.qwaecd.paramagic.spell;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class SpellUnion implements IDataSerializable {
    private final boolean isBuiltin;
    @Nonnull
    private final SpellSessionRef sessionRef;

    @Nullable
    private final BuiltinSpellId builtinId;

    @Nullable
    private final ParaData paraData;

    private SpellUnion(
            boolean isBuiltin,
            @Nonnull SpellSessionRef ref,
            @Nullable BuiltinSpellId builtinId,
            @Nullable ParaData paraData
            ) {
        this.isBuiltin = isBuiltin;
        this.sessionRef = ref;
        this.builtinId = builtinId;
        this.paraData = paraData;
    }

    public static SpellUnion ofBuiltin(
            @Nonnull SpellSessionRef ref,
            @Nonnull BuiltinSpellId builtinId
    ) {
        return new SpellUnion(true, ref, builtinId, null);
    }

    public boolean isBuiltinSpell() {
        return this.isBuiltin;
    }

    @Nonnull
    public SpellSessionRef getSessionRef() {
        return this.sessionRef;
    }

    @Nullable
    public BuiltinSpellId getBuiltinId() {
        return this.builtinId;
    }

    @Nullable
    public ParaData getParaData() {
        return this.paraData;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeBoolean("isBuiltin", this.isBuiltin);
        codec.writeObject("sessionRef", this.sessionRef);
        if (this.isBuiltin) {
            codec.writeObject("builtinId",
                    Objects.requireNonNull(this.builtinId, "builtinId is null for builtin spell")
            );
        } else {
            codec.writeObject("paraData",
                    Objects.requireNonNull(this.paraData, "paraData is null for para spell")
            );
        }
    }

    public static SpellUnion fromCodec(DataCodec codec) {
        boolean isBuiltin = codec.readBoolean("isBuiltin");
        SpellSessionRef ref = codec.readObject("sessionRef", SpellSessionRef::fromCodec);
        if (isBuiltin) {
            BuiltinSpellId builtinId = codec.readObject("builtinId", BuiltinSpellId::fromCodec);
            return new SpellUnion(true, ref, builtinId, null);
        } else {
            ParaData paraData = codec.readObject("paraData", ParaData::fromCodec);
            return new SpellUnion(false, ref, null, paraData);
        }
    }

    public SpellUnion copy() {
        return new SpellUnion(
                this.isBuiltin,
                this.sessionRef.copy(),
                this.builtinId,
                this.paraData
        );
    }
}
