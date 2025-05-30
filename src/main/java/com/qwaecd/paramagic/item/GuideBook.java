package com.qwaecd.paramagic.item;

import com.qwaecd.paramagic.client.gui.screen.GuideBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GuideBook extends Item {
    public GuideBook(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand){
        if(level.isClientSide){
            Minecraft.getInstance().setScreen(new GuideBookScreen());
        }

        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }
}
