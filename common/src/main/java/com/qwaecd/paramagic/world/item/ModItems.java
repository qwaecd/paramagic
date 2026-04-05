package com.qwaecd.paramagic.world.item;

import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
import com.qwaecd.paramagic.world.item.debug.DebugWand;
import com.qwaecd.paramagic.world.item.feat.ExplosionWand;
import com.qwaecd.paramagic.world.item.operator.*;
import net.minecraft.world.item.Item;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModItems {
    public static final Map<String, Item> ITEMS = new LinkedHashMap<>();

    public static ItemProvider PROVIDER;
    public static DebugWand DEBUG_WAND;
    public static ExplosionWand EXPLOSION_WAND;

    public static VoidOperatorItem VOID_OPERATOR;
    public static AccelerateOperatorItem ACCELERATE_OPERATOR;
    public static GradualAccelerationOperatorItem GRADUAL_ACCELERATION_OPERATOR;
    public static HeavyOperatorItem HEAVY_OPERATOR;
    public static LaserOperatorItem LASER_OPERATOR;
    public static MagicArrowOperatorItem MAGIC_ARROW_OPERATOR;
    public static ShortTrackingOperatorItem SHORT_TRACKING_OPERATOR;
    public static TrackingOperatorItem TRACKING_OPERATOR;
    public static WeightlessOperatorItem WEIGHTLESS_OPERATOR;

    public static ParaCrystalItem PARA_CRYSTAL;

    public static void init(ItemProvider provider) {
        PROVIDER = provider;
        worldItems(provider);
        operators(provider);
    }

    private static void worldItems(ItemProvider provider) {
        DEBUG_WAND = create(provider, "debug_wand", DebugWand::new);
        EXPLOSION_WAND = create(provider, "explosion_wand", ExplosionWand::new);
        PARA_CRYSTAL = create(provider, "para_crystal", ParaCrystalItem::new);
    }

    private static void operators(ItemProvider provider) {
        VOID_OPERATOR = create(provider, "void_operator", VoidOperatorItem::new);
        ACCELERATE_OPERATOR = create(provider, "accelerate_operator", AccelerateOperatorItem::new);
        GRADUAL_ACCELERATION_OPERATOR = create(provider, "gradual_acceleration_operator", GradualAccelerationOperatorItem::new);
        LASER_OPERATOR = create(provider, "laser_operator", LaserOperatorItem::new);
        MAGIC_ARROW_OPERATOR = create(provider, "magic_arrow_operator", MagicArrowOperatorItem::new);
        TRACKING_OPERATOR = create(provider, "tracking_operator", TrackingOperatorItem::new);
        SHORT_TRACKING_OPERATOR = create(provider, "short_tracking_operator", ShortTrackingOperatorItem::new);
        HEAVY_OPERATOR = create(provider, "heavy_operator", HeavyOperatorItem::new);
        WEIGHTLESS_OPERATOR = create(provider, "weightless_operator", WeightlessOperatorItem::new);
    }

    public interface ItemProvider {
        <T extends Item> T register(String name, Supplier<T> factory);
    }

    public static <T extends Item> T create(ItemProvider provider, String name, Supplier<T> factory) {
        T item = provider.register(name, factory);
        ITEMS.put(name, item);
        return item;
    }
}
