package com.qwaecd.paramagic.ui.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static MenuType<TestMenu> TEST_MENU_TYPE;

    public static void init(RegistryProvider registry) {
        TEST_MENU_TYPE = registry.register("test_menu", TestMenu::new);
    }

    public interface RegistryProvider {
        <T extends AbstractContainerMenu> MenuType<T> register(String id, MenuFactory<T> entry);
    }

    public interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int i, Inventory inventory);
    }
}
