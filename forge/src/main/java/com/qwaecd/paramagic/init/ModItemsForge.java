package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.world.item.ModItems;
import com.qwaecd.paramagic.world.item.feat.ExplosionWand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModItemsForge {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Paramagic.MOD_ID);

    private ModItemsForge() {}

    public static void registerAll(IEventBus bus) {
        ModItems.ItemProvider provider = new ModItems.ItemProvider() {
            @Override
            public <T extends Item> ModItems.Entry<T> register(String name, Supplier<? extends T> factory) {
                RegistryObject<T> object = ITEMS.register(name, factory);
                return new ForgeEntry<>(object);
            }
        };
        ModItems.ItemFactories factories = new ModItems.ItemFactories() {
            @Override
            public Supplier<? extends ExplosionWand> explosionWand() {
                return ExplosionWand::new;
            }
        };
        ModItems.init(provider, factories);
        ITEMS.register(bus);
    }

    private record ForgeEntry<T extends Item>(RegistryObject<T> object) implements ModItems.Entry<T> {
        @Override
        public ResourceLocation id() {
            return object.getId();
        }

        @Override
        public T get() {
            return object.get();
        }
    }
}
