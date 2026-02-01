package com.qwaecd.paramagic.datagen;

import com.qwaecd.paramagic.init.ModBlocksFabric;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;

public class ModelsGenFabric extends FabricModelProvider {
    public ModelsGenFabric(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        gen.createNonTemplateHorizontalBlock(ModBlocksFabric.SPELL_EDIT_TABLE);
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {

    }
}
