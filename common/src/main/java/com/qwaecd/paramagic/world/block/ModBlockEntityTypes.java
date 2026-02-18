package com.qwaecd.paramagic.world.block;

import com.qwaecd.paramagic.world.block.entity.SpellEditTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public final class ModBlockEntityTypes {
    public static BlockEntityType<SpellEditTableBlockEntity> SPELL_EDIT_TABLE;
    public static void init(BlockEntityProvider provider) {
        SPELL_EDIT_TABLE = create(provider, "spell_edit_table", SpellEditTableBlockEntity::new, ModBlocks.SPELL_EDIT_TABLE);
    }

    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState blockState);
    }

    public interface BlockEntityProvider {
        <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityFactory<T> factory, Block vaildBlock);
    }

    public static <T extends BlockEntity> BlockEntityType<T> create(BlockEntityProvider provider, String name, BlockEntityFactory<T> factory, Block vaildBlock) {
        return provider.register(name, factory, vaildBlock);
    }
}
