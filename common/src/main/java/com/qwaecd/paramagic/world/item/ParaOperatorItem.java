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
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                        .append(operatorType)
        );

        float delay = id.getTransmissionDelay();
        ChatFormatting delayFormat;
        if (delay < 0.5f) {
            delayFormat = ChatFormatting.GREEN;
        } else if (delay < 1.0f) {
            delayFormat = ChatFormatting.YELLOW;
        } else if (delay < 3.0f) {
            delayFormat = ChatFormatting.RED;
        } else {
            delayFormat = ChatFormatting.DARK_RED;
        }
        Component transmissionDelayStyle = Component.literal(String.valueOf(delay)).withStyle(delayFormat);
        components.add(
                Component.translatable("tooltip.paramagic.para_operator_item.transmission_delay")
                        .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD).append(transmissionDelayStyle)
        );

        float cycleCooldown = this.operatorId.getCycleCooldown();
        ChatFormatting cycleCooldownFormat;
        if (cycleCooldown < 1.0f) {
            cycleCooldownFormat = ChatFormatting.GREEN;
        } else if (delay < 2.0f) {
            cycleCooldownFormat = ChatFormatting.YELLOW;
        } else if (delay < 5.0f) {
            cycleCooldownFormat = ChatFormatting.RED;
        } else {
            cycleCooldownFormat = ChatFormatting.DARK_RED;
        }
        Component cycleCooldownStyle = Component.literal(String.valueOf(cycleCooldown)).withStyle(cycleCooldownFormat);
        components.add(
                Component.translatable("tooltip.paramagic.para_operator_item.cycle_cooldown")
                        .withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD).append(cycleCooldownStyle)
        );
    }
}
