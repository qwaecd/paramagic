package com.qwaecd.paramagic.client;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.world.item.WandItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.Input;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge exposes this event after keyboard input is sampled but before vanilla
 * applies its use-item 0.2x movement multiplier.
 */
@Mod.EventBusSubscriber(modid = Paramagic.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ForgeUseSlowdownHandler {
    private static final float USE_ITEM_SLOWDOWN_COMPENSATION = 5.0F;

    private ForgeUseSlowdownHandler() {}

    @SubscribeEvent
    public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)
                || !player.isUsingItem()
                || player.isPassenger()
                || !WandItem.shouldSkipSlowdown(player.getUseItem())) {
            return;
        }

        Input input = event.getInput();
        input.leftImpulse *= USE_ITEM_SLOWDOWN_COMPENSATION;
        input.forwardImpulse *= USE_ITEM_SLOWDOWN_COMPENSATION;
    }
}
