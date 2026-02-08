package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.block.SpellEditTableBlock;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModBlocksFabric {
    public static final Map<String, Block> BLOCKS = new LinkedHashMap<>();

    public static final Block SPELL_EDIT_TABLE = register("spell_edit_table", new SpellEditTableBlock());

    public static <T extends Block> Block register(String path, T block) {
        Block it = Registry.register(BuiltInRegistries.BLOCK, ModRL.InModSpace(path), block);
        ModItems.PROVIDER.register(path, () -> new BlockItem(it, new Item.Properties()));
        BLOCKS.put(path, it);
        return it;
    }

    public static void registerAll() {
    }
}
