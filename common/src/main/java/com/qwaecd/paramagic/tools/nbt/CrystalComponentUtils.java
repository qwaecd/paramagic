package com.qwaecd.paramagic.tools.nbt;

import com.qwaecd.paramagic.network.codec.NBTCodec;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalComponent;
import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.tools.ModRL;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CrystalComponentUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrystalComponentUtils.class);
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

    public static boolean insertParaOpIdToPath(
            @Nonnull ParaOpId opId, @Nonnull String path, @Nonnull ParaCrystalComponent component
    ) {
        ParaOperator operator = AllParaOperators.createOperator(opId);
        if (operator == null) {
            return false;
        }
        component.putOperator(path, operator);
        return true;
    }

    public static boolean insertParaOperatorFromPathInItemStack(
            @Nonnull ParaOpId opId, @Nonnull String path, @Nonnull ItemStack item
    ) {
        if (!AllParaOperators.contains(opId)) {
            return false;
        }

        CompoundTag tag = item.getTag();
        if (tag == null) {
            return false;
        }

        try {
            CompoundTag paraTag = tag.getCompound("para");
            CompoundTag operatorMap = paraTag.getCompound("operatorMap");
            CompoundTag entry = operatorMap.getCompound(path);
            if (!(entry.contains("id") && entry.contains("type"))) {
                entry.putInt("type", opId.type.id);
                entry.putString("id", opId.id.toString());
                operatorMap.put(path, entry);
            }
            return true;
        } catch (NullPointerException | ClassCastException ignored) {
        } catch (Exception e) {
            LOGGER.error("Failed to insert operator {} to path {} in item stack {}: {}", opId, path, item, e);
        }
        return false;
    }

    public static ItemStack removeParaOperatorFromPathInItemStack(@Nonnull String path, @Nonnull ItemStack item) {
        CompoundTag tag = item.getTag();
        if (tag == null) {
            return ItemStack.EMPTY;
        }

        try {
            CompoundTag paraTag = tag.getCompound("para");
            CompoundTag operatorMap = paraTag.getCompound("operatorMap");
            if (!operatorMap.contains(path)) {
                return ItemStack.EMPTY;
            }
            CompoundTag entry = operatorMap.getCompound(path);
            if (!(entry.contains("id") && entry.contains("id"))) {
                return ItemStack.EMPTY;
            }
            String opId = entry.getString("id");
            int type = entry.getInt("type");
            ParaOpId paraOpId = ParaOpId.of(ModRL.ofString(opId), OperatorType.fromId(type));
            ParaOperator operator = AllParaOperators.createOperator(paraOpId);
            if (operator == null) {
                return ItemStack.EMPTY;
            }
            operatorMap.remove(path);
            return operator.createOperatorItem();
        } catch (NullPointerException | ClassCastException ignored) {
        } catch (Exception e) {
            LOGGER.error("Failed to remove operator from path {} in item stack {}: {}", path, item, e);
        }
        return ItemStack.EMPTY;
    }

    public static boolean containsParaOperatorInPath(@Nonnull String path, @Nonnull ItemStack item) {
        CompoundTag tag = item.getTag();
        if (tag == null) {
            return false;
        }

        try {
            CompoundTag paraTag = tag.getCompound("para");
            CompoundTag operatorMap = paraTag.getCompound("operatorMap");
            return operatorMap.contains(path);
        } catch (NullPointerException | ClassCastException ignored) {
        } catch (Exception e) {
            LOGGER.error("Failed to check operator in path {} in item stack {}: {}", path, item, e);
        }
        return false;
    }
}
