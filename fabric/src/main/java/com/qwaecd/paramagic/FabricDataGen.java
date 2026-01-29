package com.qwaecd.paramagic;

import com.qwaecd.paramagic.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FabricDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockLootTableGenFabric::new);
        pack.addProvider(BlockTagsGenFabric::new);
        pack.addProvider(LangGenFabric.EnUsGen::new);
        pack.addProvider(LangGenFabric.ZhCnGen::new);
        pack.addProvider(ModelsGenFabric::new);
        pack.addProvider(RecipesGenFabric::new);
    }
}
