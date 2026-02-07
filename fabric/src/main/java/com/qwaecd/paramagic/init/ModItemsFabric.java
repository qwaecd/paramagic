package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModItemsFabric {
    public static final Map<String, Item> ITEMS = new LinkedHashMap<>();

    public static void registerAll() {
        ModItems.ItemProvider provider = new ModItems.ItemProvider() {
            @Override
            @SuppressWarnings("unchecked")
            public <T extends Item> T register(String name, Supplier<T> factory) {
                T it = (T) Items.registerItem(ModRL.InModSpace(name), factory.get());
                ITEMS.put(name, it);
                return it;
            }
        };
        ModItems.init(provider);
    }
}
