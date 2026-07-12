package com.qwaecd.paramagic.spell.mana;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.tools.TimeProvider;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.world.item.WandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

@PlatformScope(PlatformScopeType.CLIENT)
public final class ClientManaState {
    private static final int HUD_MARGIN = 4;
    private static final int HUD_WIDTH = 112;
    private static final int HUD_HEIGHT = 10;
    private static final int HUD_BORDER = 1;
    private static final int HUD_BOTTOM_MARGIN = 12;
    private static final int OUTER_BACKGROUND_COLOR = UIColor.of(20, 24, 30, 230).color;
    private static final int EMPTY_BAR_COLOR = UIColor.of(44, 51, 61, 235).color;
    private static final int FILL_TOP_COLOR = UIColor.of(178, 226, 255, 255).color;
    private static final int FILL_BOTTOM_COLOR = UIColor.of(79, 164, 218, 255).color;
    private static final int TEXT_COLOR = UIColor.of(245, 250, 255, 255).color;
    private static final float DEPLETION_ANIMATION_SPEED = 12.0f;
    private static final float RECOVERY_ANIMATION_SPEED = 5.0f;

    private static int mana;
    private static int maxMana;
    private static float displayedMana;
    private static boolean displayInitialized;

    private ClientManaState() {
    }

    public static int getMana() {
        return mana;
    }

    public static int getMaxMana() {
        return maxMana;
    }

    public static void update(int mana, int maxMana) {
        ClientManaState.maxMana = Math.max(0, maxMana);
        ClientManaState.mana = Math.max(0, Math.min(mana, ClientManaState.maxMana));
    }

    public static void reset() {
        mana = 0;
        maxMana = 0;
        displayedMana = 0.0f;
        displayInitialized = false;
    }

    public static void displayHud(GuiGraphics guiGraphics, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !isHoldingWand(player)) {
            return;
        }

        int currentMana = getMana();
        int currentMaxMana = getMaxMana();
        if (currentMaxMana <= 0) {
            return;
        }

        updateDisplayedMana(currentMana, minecraft);

        int outerX = HUD_MARGIN;
        int outerY = guiGraphics.guiHeight() - HUD_BOTTOM_MARGIN - HUD_HEIGHT - HUD_BORDER * 2;
        int x = outerX + HUD_BORDER;
        int y = outerY + HUD_BORDER;
        int filledWidth = Math.round(HUD_WIDTH * Math.min(1.0f, displayedMana / currentMaxMana));

        guiGraphics.fill(
                outerX, outerY,
                outerX + HUD_WIDTH + HUD_BORDER * 2, outerY + HUD_HEIGHT + HUD_BORDER * 2,
                OUTER_BACKGROUND_COLOR
        );
        guiGraphics.fill(x, y, x + HUD_WIDTH, y + HUD_HEIGHT, EMPTY_BAR_COLOR);
        if (filledWidth > 0) {
            guiGraphics.fillGradient(
                    x, y,
                    x + filledWidth, y + HUD_HEIGHT,
                    0,
                    FILL_TOP_COLOR,
                    FILL_BOTTOM_COLOR
            );
        }

        Component manaText = Component.literal(currentMana + " / " + currentMaxMana);
        Font font = minecraft.font;
        int textX = x + (HUD_WIDTH - font.width(manaText)) / 2;
        int textY = y + (HUD_HEIGHT - font.lineHeight) / 2;
        guiGraphics.drawString(font, manaText, textX, textY, TEXT_COLOR, true);
    }

    private static boolean isHoldingWand(LocalPlayer player) {
        return player.getMainHandItem().getItem() instanceof WandItem
                || player.getOffhandItem().getItem() instanceof WandItem;
    }

    private static void updateDisplayedMana(int currentMana, Minecraft minecraft) {
        if (!displayInitialized) {
            displayedMana = currentMana;
            displayInitialized = true;
            return;
        }

        float speed = currentMana < displayedMana ? DEPLETION_ANIMATION_SPEED : RECOVERY_ANIMATION_SPEED;
        float deltaTime = TimeProvider.getDeltaTime(minecraft);
        float interpolation = 1.0f - (float) Math.exp(-speed * deltaTime);
        displayedMana += (currentMana - displayedMana) * interpolation;

        if (Math.abs(currentMana - displayedMana) < 0.01f) {
            displayedMana = currentMana;
        }
        displayedMana = Math.max(0.0f, Math.min(displayedMana, getMaxMana()));
    }
}
