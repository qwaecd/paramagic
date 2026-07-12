package com.qwaecd.paramagic.platform;

import com.qwaecd.paramagic.spell.caster.ManaAccess;
import com.qwaecd.paramagic.spell.caster.ManaStorage;
import com.qwaecd.paramagic.spell.caster.ManaSync;
import com.qwaecd.paramagic.tools.ModRL;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.Nullable;

/** Forge Capability backend for persistent player mana. */
public final class ForgeManaAccess implements ManaStorage {
    private static final ResourceLocation MANA_ID = ModRL.inModSpace("mana");
    public static final Capability<ManaData> MANA = CapabilityManager.get(new CapabilityToken<>() {});

    @Override
    public int getMana(Player player) {
        return player.getCapability(MANA).map(ManaData::getMana).orElse(ManaAccess.DEFAULT_MAX_MANA);
    }

    @Override
    public void setMana(Player player, int mana) {
        player.getCapability(MANA).ifPresent(data -> data.setMana(mana));
    }

    @Override
    public int getMaxMana(Player player) {
        return player.getCapability(MANA).map(ManaData::getMaxMana).orElse(ManaAccess.DEFAULT_MAX_MANA);
    }

    @Override
    public void setMaxMana(Player player, int maxMana) {
        player.getCapability(MANA).ifPresent(data -> data.setMaxMana(maxMana));
    }

    /** Registers the Capability lifecycle hooks from the Forge mod entry point. */
    public static void register() {
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.addGenericListener(Entity.class, ForgeManaAccess::attachCapabilities);
        eventBus.addListener(ForgeManaAccess::copyOnPlayerClone);
        eventBus.addListener(ForgeManaAccess::syncOnPlayerLogin);
    }

    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            ManaProvider provider = new ManaProvider();
            event.addCapability(MANA_ID, provider);
            event.addListener(provider::invalidate);
        }
    }

    public static void copyOnPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(MANA).ifPresent(oldData ->
                event.getEntity().getCapability(MANA).ifPresent(newData -> newData.setMana(oldData.getMana()))
        );
        event.getOriginal().invalidateCaps();
        if (event.getEntity() instanceof ServerPlayer player) {
            ManaSync.sync(player);
        }
    }

    public static void syncOnPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ManaSync.sync(player);
        }
    }

    public static final class ManaData {
        private int mana = ManaAccess.DEFAULT_MAX_MANA;
        private int maxMana = ManaAccess.DEFAULT_MAX_MANA;

        public int getMana() {
            return mana;
        }

        public void setMana(int mana) {
            this.mana = Math.max(0, Math.min(mana, this.maxMana));
        }

        public int getMaxMana() {
            return maxMana;
        }

        public void setMaxMana(int maxMana) {
            this.maxMana = Math.max(0, maxMana);
            this.mana = Math.min(this.mana, this.maxMana);
        }
    }

    private static final class ManaProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final ManaData data = new ManaData();
        private final LazyOptional<ManaData> optional = LazyOptional.of(() -> data);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
            return capability == MANA ? optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("mana", data.getMana());
            tag.putInt("maxMana", data.getMaxMana());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            data.setMaxMana(tag.contains("maxMana") ? tag.getInt("maxMana") : ManaAccess.DEFAULT_MAX_MANA);
            data.setMana(tag.getInt("mana"));
        }

        private void invalidate() {
            optional.invalidate();
        }
    }
}
