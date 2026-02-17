package com.qwaecd.paramagic.tools.nbt;

import com.qwaecd.paramagic.network.codec.NBTCodec;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CrystalComponentUtils {

    @Nullable
    public static ParaCrystalComponent getComponentFromTag(@Nullable CompoundTag tag) {
        if (tag == null) {
            return null;
        }

        Tag paraTag = tag.get("para");
        if (!(paraTag instanceof CompoundTag crystalComponent)) {
            return null;
        }
        return ParaCrystalComponent.fromCodec(new NBTCodec(crystalComponent));
    }

    public static void writeComponentToTag(@Nonnull CompoundTag tag, @Nonnull ParaCrystalComponent component) {
        CompoundTag crystalComponentTag = new CompoundTag();
        NBTCodec codec = new NBTCodec(crystalComponentTag);
        component.write(codec);
        tag.put("para", crystalComponentTag);
    }

    @Nullable
    public static ParaCrystalComponent getComponentFromItemStack(@Nonnull ItemStack item) {
        return getComponentFromTag(item.getTag());
    }

    public static void writeComponentToItemStack(@Nonnull ItemStack item, @Nonnull ParaCrystalComponent component) {
        CompoundTag tag = item.getOrCreateTag();
        writeComponentToTag(tag, component);
    }
}
