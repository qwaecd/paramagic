package com.qwaecd.paramagic.init;

import com.mojang.datafixers.types.Type;
import com.qwaecd.paramagic.world.block.ModBlockEntityTypes;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntitiesFabric {
    public static void registerAll() {
        var provider = new ModBlockEntityTypes.BlockEntityProvider() {
            @Override
            public <T extends BlockEntity> BlockEntityType<T>
            register(String name, ModBlockEntityTypes.BlockEntityFactory<T> factory, Block vaildBlock) {
                Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, name);
                BlockEntityType.Builder<T> builder = BlockEntityType.Builder.of(factory::create, vaildBlock);
                return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, name, builder.build(type));
            }
        };
        ModBlockEntityTypes.init(provider);
    }
}
