package com.qwaecd.paramagic.datagen;

import com.qwaecd.paramagic.world.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class BlockLootTableGenFabric extends FabricBlockLootTableProvider {
    public BlockLootTableGenFabric(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        this.dropSelf(ModBlocks.SPELL_EDIT_TABLE);
    }
}
