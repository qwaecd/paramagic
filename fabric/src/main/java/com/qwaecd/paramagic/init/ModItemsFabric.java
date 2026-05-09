package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.geo.item.ExplosionWandGeoFabric;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;
import com.qwaecd.paramagic.world.item.feat.ExplosionWand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class ModItemsFabric {
    public static void registerAll() {
        ModItems.ItemProvider provider = new ModItems.ItemProvider() {
            @Override
            @SuppressWarnings("unchecked")
            public <T extends Item> ModItems.Entry<T> register(String name, Supplier<? extends T> factory) {
                ResourceLocation id = ModRL.inModSpace(name);
                T item = (T) Items.registerItem(id, factory.get());
                return new FabricEntry<>(id, item);
            }
        };
        ModItems.ItemFactories factories = new ModItems.ItemFactories() {
            @Override
            public Supplier<? extends ExplosionWand> explosionWand() {
                return ExplosionWandGeoFabric::new;
            }
        };
        ModItems.init(provider, factories);
    }

    private record FabricEntry<T extends Item>(ResourceLocation id, T item) implements ModItems.Entry<T> {
        @Override
        public T get() {
            return item;
        }
    }
}
