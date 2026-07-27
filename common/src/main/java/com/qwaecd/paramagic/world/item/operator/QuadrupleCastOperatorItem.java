package com.qwaecd.paramagic.world.item.operator;

import com.qwaecd.paramagic.thaumaturgy.operator.alpha.QuadrupleCastOperator;
import com.qwaecd.paramagic.world.item.ParaOperatorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public final class QuadrupleCastOperatorItem extends ParaOperatorItem {
    public QuadrupleCastOperatorItem() { super(QuadrupleCastOperator.OP_ID); }
    @Override protected void appendDescribeHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.paramagic.quadruple_cast_operator.describe").withStyle(ChatFormatting.GRAY));
    }
}
