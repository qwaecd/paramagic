package com.qwaecd.paramagic.world.item;

import com.qwaecd.paramagic.world.item.debug.DebugWand;
import com.qwaecd.paramagic.world.item.feat.ExplosionWand;
import com.qwaecd.paramagic.world.item.operator.VoidOperatorItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public final class ModItems {
    public static ItemProvider PROVIDER;
    public static DebugWand DEBUG_WAND;
    public static ExplosionWand EXPLOSION_WAND;
    public static VoidOperatorItem VOID_OPERATOR_ITEM;

    public static void init(ItemProvider provider) {
        PROVIDER = provider;
        DEBUG_WAND = create(provider, "debug_wand", DebugWand::new);
        EXPLOSION_WAND = create(provider, "explosion_wand", ExplosionWand::new);
        VOID_OPERATOR_ITEM = create(provider, "void_operator", VoidOperatorItem::new);
    }

    public interface ItemProvider {
        <T extends Item> T register(String name, Supplier<T> factory);
    }

    public static <T extends Item> T create(ItemProvider provider, String name, Supplier<T> factory) {
        return provider.register(name, factory);
    }
}
