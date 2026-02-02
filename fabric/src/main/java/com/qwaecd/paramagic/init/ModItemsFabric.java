package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.debug.DebugWand;
import com.qwaecd.paramagic.world.item.feat.ExplosionWand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModItemsFabric {
    public static final Map<String, Item> ITEMS = new LinkedHashMap<>();

    public static final Item DEBUG_WAND = register("debug_wand", new DebugWand(new Item.Properties()));
    public static final Item EXPLOSION_WAND = register("explosion_wand", new ExplosionWand(new Item.Properties()));

    public static <T extends Item> Item register(String path, T item) {
        Item it = Items.registerItem(ModRL.InModSpace(path), item);
        ITEMS.put(path, it);
        return it;
    }

    public static void registerAll() {
    }
}
