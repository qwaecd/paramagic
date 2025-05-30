package com.qwaecd.paramagic.init;


import com.qwaecd.paramagic.item.GuideBook;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import static com.qwaecd.paramagic.Paramagic.MODID;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    private static final Map<String, RegistryObject<Item>> ITEM_REGISTRIES = new LinkedHashMap<>();

    public static final RegistryObject<Item> GUIDE = registerItem("guide_book",
            () -> new GuideBook(new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("main_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable(MODID + ".main_tab"))
                    .icon(() -> getItem("guide_book").getDefaultInstance())
                    .displayItems((params, output) -> ITEM_REGISTRIES.values().forEach(item -> output.accept(item.get())))
                    .build());

    public static RegistryObject<Item> registerItem(String name, Supplier<Item> supplier) {
        RegistryObject<Item> item = ITEMS.register(name, supplier);
        ITEM_REGISTRIES.put(name, item);
        return item;
    }

    public static Item getItem(String name) {
        return ITEM_REGISTRIES.get(name).get();
    }

}
