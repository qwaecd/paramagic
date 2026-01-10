package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.world.item.debug.DebugWand;
import com.qwaecd.paramagic.world.item.feat.ExplosionWand;
import com.qwaecd.paramagic.tools.ModRL;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModItemsFabric {
    private static final Map<String, Item> ITEMS = new LinkedHashMap<>();

    public static final Item DEBUG_WAND = register("debug_wand", new DebugWand(new Item.Properties()));
    public static final Item EXPLOSION_WAND = register("explosion_wand", new ExplosionWand(new Item.Properties()));

    public static <T extends Item> Item register(String path, T item) {
        Item it = Items.registerItem(ModRL.InModSpace(path), item);
        ITEMS.put(path, it);
        return it;
    }

    public static final ResourceKey<CreativeModeTab> CUSTOM_ITEM_GROUP_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ModRL.InModSpace("item_group"));
    public static final CreativeModeTab CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(EXPLOSION_WAND))
            .title(Component.translatable("item_group.paramagic.main"))
            .displayItems((itemParameter, output) -> ITEMS.forEach((path, item) -> output.accept(item)))
            .build();

    public static void registerAll() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);
    }
}
