package com.qwaecd.paramagic.datagen;

import com.qwaecd.paramagic.init.ModBlocksFabric;
import com.qwaecd.paramagic.world.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

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
        gen.generateFlatItem(ModItems.VOID_OPERATOR_ITEM, ModelTemplates.FLAT_ITEM);
    }
}
