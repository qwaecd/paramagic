package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class ModItemsFabric {
    public static void registerAll() {
        ModItems.ItemProvider provider = new ModItems.ItemProvider() {
            @Override
            @SuppressWarnings("unchecked")
            public <T extends Item> T register(String name, Supplier<T> factory) {
                return (T) Items.registerItem(ModRL.InModSpace(name), factory.get());
            }
        };
        ModItems.init(provider);
    }
}
