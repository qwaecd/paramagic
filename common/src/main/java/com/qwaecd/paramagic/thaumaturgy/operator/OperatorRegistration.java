package com.qwaecd.paramagic.thaumaturgy.operator;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.datagen.Lang;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.operator.GenericOperatorItem;
import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 集中管理法术（{@link ParaOperator}）注册的单一事实源：
 * <ul>
 *     <li>{@link ParaOpId}（id/类型/各项数值）</li>
 *     <li>{@link ParaOperator} 工厂（供 {@link AllParaOperators} 注册）</li>
 *     <li>{@link Item} 工厂（供 {@code ModItems} 注册 operator 物品并自动生成 tooltip）</li>
 *     <li>展示名 / describe 行（供 {@link Lang} 注册翻译）</li>
 * </ul>
 */
public final class OperatorRegistration {
    private static final List<Descriptor> REGISTRY = new ArrayList<>();
    private static final Map<String, Supplier<?>> ITEM_BINDINGS = new LinkedHashMap<>();
    private static volatile boolean langsRegistered = false;

    private OperatorRegistration() {}

    @Nonnull
    public static Builder builder(@Nonnull String name, @Nonnull OperatorType type) {
        return new Builder(name, type);
    }

    public static void forEachDescriptor(@Nonnull Consumer<Descriptor> consumer) {
        REGISTRY.forEach(consumer);
    }

    /** ModItems 在注册完成 item 后回填绑定，供 operator 构造时通过 {@link #itemOf(String)} 取用。 */
    public static void bindItem(@Nonnull String name, @Nonnull Supplier<?> itemSupplier) {
        ITEM_BINDINGS.put(name, itemSupplier);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T extends Item> Supplier<? extends T> itemOf(@Nonnull String name) {
        Supplier<?> s = ITEM_BINDINGS.get(name);
        if (s == null) {
            throw new IllegalStateException("Item for operator '" + name + "' has not been bound yet (ensure ModItems.init runs before constructing operators).");
        }
        return (Supplier<? extends T>) s;
    }

    /**
     * 将 REGISTRY 中所有 descriptor 的展示名和 describe 行写入 {@link Lang}。
     * <p>
     * describe 行 lang key 自动编号：
     * 单行用 {@code describe}，多行依次 {@code describe} / {@code describe1} / {@code describe2}...
     */
    public static synchronized void registerLangs() {
        if (langsRegistered) return;
        for (Descriptor d : REGISTRY) {
            Lang.of("item.paramagic." + d.name, d.displayNameEn, d.displayNameZh);
            for (int i = 0; i < d.describeLines.size(); i++) {
                String suffix = (i == 0) ? "describe" : "describe" + i;
                DescribeLine line = d.describeLines.get(i);
                Lang.of("tooltip.paramagic." + d.name + "." + suffix, line.en, line.zh);
            }
        }
        langsRegistered = true;
    }

    public static final class Builder {
        private final String name;
        private final OperatorType type;
        private float transmissionDelay = 0.01f;
        private float cycleCooldown = 0.05f;
        private int manaCost = 10;
        private String displayNameEn;
        private String displayNameZh;
        private final List<DescribeLine> describeLines = new ArrayList<>();
        private Function<ParaOpId, ? extends ParaOperator> operatorFactory;
        private Supplier<? extends Item> itemFactory;

        Builder(@Nonnull String name, @Nonnull OperatorType type) {
            this.name = name;
            this.type = type;
        }

        public Builder transmissionDelay(float v) { this.transmissionDelay = v; return this; }
        public Builder cycleCooldown(float v) { this.cycleCooldown = v; return this; }
        public Builder manaCost(int v) { this.manaCost = v; return this; }

        public Builder display(@Nonnull String en, @Nonnull String zh) {
            this.displayNameEn = en;
            this.displayNameZh = zh;
            return this;
        }

        /**
         * 附加一行 describe；多次调用即多行，按顺序生成 describe / describe1 / describe2 ... lang key。
         */
        public Builder describe(@Nonnull String en, @Nonnull String zh) {
            this.describeLines.add(new DescribeLine(en, zh));
            return this;
        }

        public Builder operator(@Nonnull Function<ParaOpId, ? extends ParaOperator> factory) {
            this.operatorFactory = factory;
            return this;
        }

        /**
         * 利用提供的物品工厂注册 operator item。若不调用本方法，则默认使用 {@link GenericOperatorItem}。
         */
        public Builder item(@Nonnull Supplier<? extends Item> factory) {
            this.itemFactory = factory;
            return this;
        }

        @Nonnull
        public ParaOpId build() {
            if (displayNameEn == null || displayNameZh == null) {
                Paramagic.LOG.warn("display() not set for operator '{}'", name);
            }
            if (operatorFactory == null) {
                throw new IllegalStateException("operator() not set for operator '" + name + "'");
            }
            ParaOpId opId = ParaOpId.of(ModRL.inModSpace(name),
                    new ParaOpId.Properties(type, transmissionDelay, cycleCooldown, manaCost));
            Supplier<? extends Item> itemF = (itemFactory != null)
                    ? itemFactory
                    : () -> new GenericOperatorItem(opId, List.copyOf(describeLines));
            REGISTRY.add(new Descriptor(name, opId, itemF,
                    List.copyOf(describeLines), displayNameEn, displayNameZh, operatorFactory));
            return opId;
        }
    }

    public static final class Descriptor {
        public final String name;
        public final ParaOpId opId;
        public final Supplier<? extends Item> itemFactory;
        public final List<DescribeLine> describeLines;
        public final String displayNameEn;
        public final String displayNameZh;
        /** 接收 {@link #opId} 作为参数，确保 operator 实例与注册的 id 一一对应。 */
        public final Function<ParaOpId, ? extends ParaOperator> operatorFactory;

        Descriptor(
                String name,
                ParaOpId opId,
                Supplier<? extends Item> itemFactory,
                List<DescribeLine> describeLines,
                String displayNameEn,
                String displayNameZh,
                Function<ParaOpId, ? extends ParaOperator> operatorFactory
        ) {
            this.name = name;
            this.opId = opId;
            this.itemFactory = itemFactory;
            this.describeLines = describeLines;
            this.displayNameEn = displayNameEn;
            this.displayNameZh = displayNameZh;
            this.operatorFactory = operatorFactory;
        }
    }

    public static final class DescribeLine {
        public final String en;
        public final String zh;

        DescribeLine(String en, String zh) {
            this.en = en;
            this.zh = zh;
        }
    }
}