package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.item.debug.DebugWand;
import com.qwaecd.paramagic.item.feat.ExplosionWand;
import com.qwaecd.paramagic.tools.ModRL;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ModItemsFabric {

    public static final Item DEBUG_WAND = register("debug_wand", new DebugWand(new Item.Properties()));
    public static final Item EXPLOSION_WAND = register("explosion_wand", new ExplosionWand(new Item.Properties()));

    public static <T extends Item> Item register(String path, T item) {
        return Items.registerItem(ModRL.InModSpace(path), item);
    }

    public static void registerAll() {
    }
}
