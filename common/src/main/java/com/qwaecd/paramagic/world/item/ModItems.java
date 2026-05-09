package com.qwaecd.paramagic.world.item;

import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
import com.qwaecd.paramagic.world.item.debug.DebugWand;
import com.qwaecd.paramagic.world.item.feat.ExplosionWand;
import com.qwaecd.paramagic.world.item.operator.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModItems {
    public static final Map<String, Entry<? extends Item>> ITEMS = new LinkedHashMap<>();

    public static ItemProvider PROVIDER;
    public static Entry<DebugWand> DEBUG_WAND;
    public static Entry<ExplosionWand> EXPLOSION_WAND;

    public static Entry<VoidOperatorItem> VOID_OPERATOR;
    public static Entry<AccelerateOperatorItem> ACCELERATE_OPERATOR;
    public static Entry<ExtendLifetimeOperatorItem> EXTEND_LIFETIME_OPERATOR;
    public static Entry<GradualAccelerationOperatorItem> GRADUAL_ACCELERATION_OPERATOR;
    public static Entry<HeavyOperatorItem> HEAVY_OPERATOR;
    public static Entry<LaserOperatorItem> LASER_OPERATOR;
    public static Entry<MagicArrowOperatorItem> MAGIC_ARROW_OPERATOR;
    public static Entry<ShortenLifetimeOperatorItem> SHORTEN_LIFETIME_OPERATOR;
    public static Entry<ShortTrackingOperatorItem> SHORT_TRACKING_OPERATOR;
    public static Entry<TrackingOperatorItem> TRACKING_OPERATOR;
    public static Entry<WeightlessOperatorItem> WEIGHTLESS_OPERATOR;
    public static Entry<GravityCollapseOperatorItem> GRAVITY_COLLAPSE_OPERATOR;

    public static Entry<ParaCrystalItem> PARA_CRYSTAL;

    public static void init(ItemProvider provider, ItemFactories factories) {
        PROVIDER = provider;
        worldItems(provider, factories);
        operators(provider);
    }

    private static void worldItems(ItemProvider provider, ItemFactories factories) {
        DEBUG_WAND = create(provider, "debug_wand", DebugWand::new);
        EXPLOSION_WAND = create(provider, "explosion_wand", factories.explosionWand());
        PARA_CRYSTAL = create(provider, "para_crystal", ParaCrystalItem::new);
    }

    private static void operators(ItemProvider provider) {
        VOID_OPERATOR = create(provider, "void_operator", VoidOperatorItem::new);
        ACCELERATE_OPERATOR = create(provider, "accelerate_operator", AccelerateOperatorItem::new);
        GRADUAL_ACCELERATION_OPERATOR = create(provider, "gradual_acceleration_operator", GradualAccelerationOperatorItem::new);
        SHORTEN_LIFETIME_OPERATOR = create(provider, "shorten_lifetime_operator", ShortenLifetimeOperatorItem::new);
        EXTEND_LIFETIME_OPERATOR = create(provider, "extend_lifetime_operator", ExtendLifetimeOperatorItem::new);
        LASER_OPERATOR = create(provider, "laser_operator", LaserOperatorItem::new);
        MAGIC_ARROW_OPERATOR = create(provider, "magic_arrow_operator", MagicArrowOperatorItem::new);
        TRACKING_OPERATOR = create(provider, "tracking_operator", TrackingOperatorItem::new);
        SHORT_TRACKING_OPERATOR = create(provider, "short_tracking_operator", ShortTrackingOperatorItem::new);
        HEAVY_OPERATOR = create(provider, "heavy_operator", HeavyOperatorItem::new);
        WEIGHTLESS_OPERATOR = create(provider, "weightless_operator", WeightlessOperatorItem::new);
        GRAVITY_COLLAPSE_OPERATOR = create(provider, "gravity_collapse_operator", GravityCollapseOperatorItem::new);
    }

    public interface Entry<T extends Item> extends Supplier<T> {
        ResourceLocation id();

        @Override
        T get();
    }

    public interface ItemProvider {
        <T extends Item> Entry<T> register(String name, Supplier<? extends T> factory);
    }

    public interface ItemFactories {
        Supplier<? extends ExplosionWand> explosionWand();
    }

    public static <T extends Item> Entry<T> create(ItemProvider provider, String name, Supplier<? extends T> factory) {
        Entry<T> entry = provider.register(name, factory);
        ITEMS.put(name, entry);
        return entry;
    }
}
