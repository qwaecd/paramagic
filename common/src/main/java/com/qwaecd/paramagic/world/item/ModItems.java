package com.qwaecd.paramagic.world.item;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.world.item.content.ParaCrystalItem;
import com.qwaecd.paramagic.world.item.debug.DebugWand;
import com.qwaecd.paramagic.world.item.feat.ExplosionWand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ModItems {
    public static final Map<String, Entry<? extends Item>> ITEMS = new LinkedHashMap<>();
    private static final List<Entry<? extends Item>> OPERATORS = new ArrayList<>();

    public static ItemProvider PROVIDER;
    public static Entry<DebugWand> DEBUG_WAND;
    public static Entry<ExplosionWand> EXPLOSION_WAND;
    public static Entry<ParaCrystalItem> PARA_CRYSTAL;

    public static void init(ItemProvider provider, ItemFactories factories) {
        PROVIDER = provider;
        worldItems(provider, factories);
        operators(provider);
    }

    public static void forEachOperator(Consumer<Entry<? extends Item>> consumer) {
        OPERATORS.forEach(consumer);
    }

    private static void worldItems(ItemProvider provider, ItemFactories factories) {
        DEBUG_WAND = create(provider, "debug_wand", DebugWand::new);
        EXPLOSION_WAND = create(provider, "explosion_wand", factories.explosionWand());
        PARA_CRYSTAL = create(provider, "para_crystal", ParaCrystalItem::new);
    }

    /**
     * 所有法术 item 由 {@link OperatorRegistration} 的 REGISTRY 驱动统一注册：不再在 ModItems 内逐项列举。
     * 注册成功后回填 item 绑定，供 operator 构造时通过 {@link OperatorRegistration#itemOf(String)} 取到。
     */
    private static void operators(ItemProvider provider) {
        OperatorRegistration.forEachDescriptor(d -> {
            Entry<? extends Item> entry = createOperator(provider, d.name, d.itemFactory);
            OperatorRegistration.bindItem(d.name, entry);
        });
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

    public static <T extends Item> Entry<T> createOperator(ItemProvider provider, String name, Supplier<? extends T> factory) {
        Entry<T> entry = create(provider, name, factory);
        OPERATORS.add(entry);
        return entry;
    }
}