package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.menu.ModMenuTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenusFabric {
    public static void registerAll() {
        ModMenuTypes.RegistryProvider provider = new ModMenuTypes.RegistryProvider() {
            @Override
            public <T extends AbstractContainerMenu> MenuType<T> register(String id, ModMenuTypes.MenuFactory<T> entry) {
                return Registry.register(
                        BuiltInRegistries.MENU,
                        ModRL.InModSpace(id),
                        new MenuType<>(entry::create, FeatureFlags.VANILLA_SET)
                );
            }
        };
        ModMenuTypes.init(provider);
    }
}
