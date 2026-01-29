package com.qwaecd.paramagic.world.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class SpellEditTable extends Block {
    public SpellEditTable() {
        super(Properties.of().sound(SoundType.WOOD).strength(2.0F).noOcclusion());
    }
}
