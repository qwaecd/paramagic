package com.qwaecd.paramagic.world.item.operator;

import com.qwaecd.paramagic.thaumaturgy.operator.projectile.GravityCollapseOperator;
import com.qwaecd.paramagic.world.item.ParaOperatorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class GravityCollapseOperatorItem extends ParaOperatorItem {
    public GravityCollapseOperatorItem() {
        super(GravityCollapseOperator.OP_ID);
    }

    @Override
    protected void appendDescribeHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag isAdvanced) {
        Component describe1 = Component.translatable("tooltip.paramagic.gravity_collapse_operator.describe1").withStyle(ChatFormatting.GRAY);
        components.add(describe1);
    }
}
