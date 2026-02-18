package com.qwaecd.paramagic.world.block;

import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModBlocks {
    public static SpellEditTableBlock SPELL_EDIT_TABLE;

    public static void init(BlockProvider provider) {
        SPELL_EDIT_TABLE = create(provider, "spell_edit_table", new SpellEditTableBlock());
    }

    public interface BlockProvider {
        <T extends Block> T register(String name, T block);
    }

    public static <T extends Block> T create(BlockProvider provider, String name, T block) {
        return create(provider, name, block, true);
    }

    public static <T extends Block> T create(BlockProvider provider, String name, T block, boolean registerBlockItem) {
        if (registerBlockItem) {
            ModItems.create(ModItems.PROVIDER, name, () -> new BlockItem(block, new Item.Properties()));
        }
        return provider.register(name, block);
    }
}
