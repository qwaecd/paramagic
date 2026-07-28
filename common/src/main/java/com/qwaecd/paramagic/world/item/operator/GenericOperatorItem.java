package com.qwaecd.paramagic.world.item.operator;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.world.item.ParaOperatorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 通用 operator item。根据 descriptor 中携带的 describe 行自动生成 tooltip。
 * <p>
 * describe 行 lang key 按 {@link OperatorRegistration#registerLangs()} 的规则生成：
 * 单行 {@code describe}，多行 {@code describe} / {@code describe1} / {@code describe2} ...
 * 注册一个新法术时若无需自定义 item 行为，可直接使用 builder 默认提供的本类，省去手写 XxxOperatorItem。
 */
public class GenericOperatorItem extends ParaOperatorItem {
    private final int describeCount;
    private final String langKeyPrefix;

    public GenericOperatorItem(ParaOpId opId, List<OperatorRegistration.DescribeLine> describeLines) {
        super(opId);
        this.describeCount = describeLines.size();
        this.langKeyPrefix = "tooltip.paramagic." + opId.getId().getPath();
    }

    @Override
    protected void appendDescribeHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag isAdvanced) {
        for (int i = 0; i < this.describeCount; i++) {
            String suffix = (i == 0) ? "describe" : "describe" + i;
            components.add(Component.translatable(this.langKeyPrefix + "." + suffix).withStyle(ChatFormatting.GRAY));
        }
    }
}