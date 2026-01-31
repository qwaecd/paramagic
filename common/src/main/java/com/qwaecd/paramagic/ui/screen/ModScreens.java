package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.ui.menu.ModMenuTypes;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModScreens {
    public static void init(RegistryProvider provider) {
        provider.register(ModMenuTypes.SPELL_EDIT_TABLE_MENU_TYPE, SpellEditTableScreen::new);
    }


    public interface RegistryProvider {
        <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>>
        void register(MenuType<M> menuType, ScreenFactory<M, S> factory);
    }

    public interface ScreenFactory<M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> {
        S create(M menu, Inventory inventory, Component title);
    }
}
