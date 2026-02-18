package com.qwaecd.paramagic.datagen;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class RecipesGenFabric extends FabricRecipeProvider {
    public RecipesGenFabric(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SPELL_EDIT_TABLE)
                .define('#', Items.DEEPSLATE)
                .define('X', Items.ENCHANTING_TABLE)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .unlockedBy(getHasName(Items.ENCHANTING_TABLE), has(Items.ENCHANTING_TABLE))
                .save(exporter, ModRL.InModSpace("spell_edit_table"));
    }
}
