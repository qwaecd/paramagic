package com.qwaecd.paramagic.thaumaturgy.operator;

import com.qwaecd.paramagic.tools.ModRL;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ParaOpId {
    @Getter
    @Nonnull
    public final ResourceLocation id;

    @Nonnull
    public final OperatorType type;

    private ParaOpId(@Nonnull ResourceLocation id, @Nonnull OperatorType type) {
        this.id = id;
        this.type = type;
    }


    public static ParaOpId of(@Nonnull ResourceLocation id, OperatorType type) {
        return new ParaOpId(id, type);
    }

    public static ParaOpId from(String namespace, String path, OperatorType type) {
        return ParaOpId.of(ModRL.of(namespace, path), type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParaOpId other = (ParaOpId) o;
        return Objects.equals(this.id, other.id);
    }
}
