package com.qwaecd.paramagic.world.item.operator;

import com.qwaecd.paramagic.thaumaturgy.operator.content.LaserOperator;
import com.qwaecd.paramagic.world.item.ParaOperatorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class LaserOperatorItem extends ParaOperatorItem {
    public LaserOperatorItem() {
        super(LaserOperator.OP_ID);
    }

    @Override
    protected void appendDescribeHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag isAdvanced) {
        Component describe = Component.translatable("tooltip.paramagic.laser_operator.describe").withStyle(ChatFormatting.GRAY);
        components.add(describe);
    }
}
