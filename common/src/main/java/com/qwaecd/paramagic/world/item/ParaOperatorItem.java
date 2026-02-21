package com.qwaecd.paramagic.world.item;

import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ParaOperatorItem extends Item {
    @Nonnull
    protected final ParaOpId operatorId;
    protected ParaOperatorItem(@Nonnull ParaOpId operatorId) {
        super(new Properties());
        this.operatorId = operatorId;
    }

    protected ParaOperatorItem(@Nonnull ParaOpId operatorId, @Nonnull Properties properties) {
        super(properties);
        this.operatorId = operatorId;
    }

    @Nonnull
    public final ParaOpId getOperatorId() {
        return this.operatorId;
    }

    /**
     * <pre>
     *     法术类型: *
     *     传导延迟: *
     *     回转冷却: *
     * </pre>
     */
    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> components,
            TooltipFlag isAdvanced
    ) {
        ParaOpId id = this.operatorId;
        Component operatorType = switch (id.type) {
            case PROJECTILE -> Component.literal(id.type.toString()).withStyle(ChatFormatting.LIGHT_PURPLE);
            case MODIFIER -> Component.literal(id.type.toString()).withStyle(ChatFormatting.DARK_GREEN);
            case ALPHA -> Component.literal(id.type.toString()).withStyle(ChatFormatting.GOLD);
            case FLOW -> Component.literal(id.type.toString()).withStyle(ChatFormatting.DARK_AQUA);
        };
        components.add(
                Component.translatable("tooltip.paramagic.para_operator_item.operator_type")
                        .withStyle(ChatFormatting.GREEN).append(operatorType)
        );

        float delay = id.getTransmissionDelay();
        ChatFormatting delayFormat = getTimeFormatting(delay);
        Component transmissionDelayStyle = Component.literal(String.valueOf(delay)).withStyle(delayFormat);
        components.add(
                Component.translatable("tooltip.paramagic.para_operator_item.transmission_delay")
                        .withStyle(ChatFormatting.DARK_GREEN).append(transmissionDelayStyle)
        );

        float cycleCooldown = id.getCycleCooldown();
        ChatFormatting cycleCooldownFormat = getTimeFormatting(cycleCooldown);
        Component cycleCooldownStyle = Component.literal(String.valueOf(cycleCooldown)).withStyle(cycleCooldownFormat);
        components.add(
                Component.translatable("tooltip.paramagic.para_operator_item.cycle_cooldown")
                        .withStyle(ChatFormatting.DARK_PURPLE).append(cycleCooldownStyle)
        );

        this.appendDescribeHoverText(stack, level, components, isAdvanced);
    }

    protected void appendDescribeHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> components,
            TooltipFlag isAdvanced
    ) {
    }

    public static ChatFormatting getTimeFormatting(float time) {
        ChatFormatting formatting;
        if (time < 0.05f) {
            formatting = ChatFormatting.GREEN;
        } else if (time < 0.5f) {
            formatting = ChatFormatting.WHITE;
        } else if (time < 1.0f) {
            formatting = ChatFormatting.YELLOW;
        } else if (time < 3.0f) {
            formatting = ChatFormatting.RED;
        } else {
            formatting = ChatFormatting.DARK_RED;
        }
        return formatting;
    }
}
