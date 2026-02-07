package com.qwaecd.paramagic.world.item;

import com.qwaecd.paramagic.world.item.debug.DebugWand;
import com.qwaecd.paramagic.world.item.feat.ExplosionWand;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public final class ModItems {
    public static ItemProvider PROVIDER;
    public static DebugWand DEBUG_WAND;
    public static ExplosionWand EXPLOSION_WAND;

    public static void init(ItemProvider provider) {
        PROVIDER = provider;
        DEBUG_WAND = create(provider, "debug_wand", DebugWand::new);
        EXPLOSION_WAND = create(provider, "explosion_wand", ExplosionWand::new);
    }

    public interface ItemProvider {
        <T extends Item> T register(String name, Supplier<T> factory);
    }

    public static <T extends Item> T create(ItemProvider provider, String name, Supplier<T> factory) {
        return provider.register(name, factory);
    }
}
