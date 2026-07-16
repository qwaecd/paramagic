package com.qwaecd.paramagic.datagen;

import com.qwaecd.paramagic.world.block.ModBlocks;
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
        gen.createNonTemplateHorizontalBlock(ModBlocks.SPELL_EDIT_TABLE);
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {
        ModItems.forEachOperator(entry -> gen.generateFlatItem(entry.get(), ModelTemplates.FLAT_ITEM));
//        gen.generateFlatItem(ModItems.VOID_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.ACCELERATE_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.GRADUAL_ACCELERATION_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.HEAVY_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.LASER_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.MAGIC_ARROW_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
        gen.generateFlatItem(ModItems.PARA_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.SHORT_TRACKING_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.TRACKING_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.WEIGHTLESS_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.GRAVITY_COLLAPSE_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.EXTEND_LIFETIME_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
//        gen.generateFlatItem(ModItems.SHORTEN_LIFETIME_OPERATOR.get(), ModelTemplates.FLAT_ITEM);
    }
}
