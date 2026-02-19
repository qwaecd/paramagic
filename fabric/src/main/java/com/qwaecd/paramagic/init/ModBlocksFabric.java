package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class ModBlocksFabric {
    public static void registerAll() {
        ModBlocks.BlockProvider provider = new ModBlocks.BlockProvider() {
            @Override
            public <T extends Block> T register(String name, T block) {
                return Registry.register(BuiltInRegistries.BLOCK, ModRL.inModSpace(name), block);
            }
        };
        ModBlocks.init(provider);
    }
}
