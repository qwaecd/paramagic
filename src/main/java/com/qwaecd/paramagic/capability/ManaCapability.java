package com.qwaecd.paramagic.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.qwaecd.paramagic.Paramagic.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ManaCapability {
    public static final Capability<IManaStorage> MANA_STORAGE = CapabilityManager.get(new CapabilityToken<>() {
    });

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IManaStorage.class);
    }

    public interface IManaStorage extends INBTSerializable<CompoundTag> {
        int getMana();

        int getMaxMana();

        boolean consumeMana(int amount);

        void addMana(int amount);

        void setMana(int mana);
    }

    public static class ManaStorage implements IManaStorage {
        private int mana;
        private int maxMana;

        public ManaStorage(int maxMana) {
            this.maxMana = maxMana;
            this.mana = maxMana;
        }

        @Override
        public int getMana() {
            return mana;
        }

        @Override
        public int getMaxMana() {
            return maxMana;
        }

        @Override
        public boolean consumeMana(int amount) {
            if (mana >= amount) {
                mana -= amount;
                return true;
            }
            return false;
        }

        @Override
        public void addMana(int amount) {
            mana = Math.min(mana + amount, maxMana);
        }

        @Override
        public void setMana(int mana) {
            this.mana = Math.max(0, Math.min(mana, maxMana));
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("mana", mana);
            tag.putInt("maxMana", maxMana);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            mana = tag.getInt("mana");
            maxMana = tag.getInt("maxMana");
        }
    }

    public static class ManaCapabilityProvider implements ICapabilityProvider {
        private final LazyOptional<IManaStorage> manaStorage;

        public ManaCapabilityProvider(int maxMana) {
            this.manaStorage = LazyOptional.of(() -> new ManaStorage(maxMana));
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == MANA_STORAGE ? manaStorage.cast() : LazyOptional.empty();
        }
    }
}
