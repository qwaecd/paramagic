package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.inventory.S2CSpellTreeEditRejectedPacket;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.ui.menu.SpellEditMenu;
import com.qwaecd.paramagic.ui_project.wand.WandEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;

@PlatformScope(PlatformScopeType.CLIENT)
public final class ClientSpellTreeHandlers {
    private ClientSpellTreeHandlers() {}

    public static void spellTreeEditRejected(S2CSpellTreeEditRejectedPacket packet, NetworkContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player != null && player.containerMenu instanceof SpellEditMenu menu) {
            menu.acceptServerEditEpoch(packet.getEditEpoch());
        }
        Screen screen = minecraft.screen;
        if (screen instanceof WandEditScreen wandEditScreen) {
            wandEditScreen.handleSpellTreeEditRejected(packet);
        }
        Paramagic.LOG.debug(
                "Spell tree edit rejected: requestId={}, epoch={}, serverVersion={}, reason={}",
                packet.getRequestId(), packet.getEditEpoch(), packet.getServerVersion(), packet.getReason()
        );
    }
}
