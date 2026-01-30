package com.qwaecd.paramagic.datagen;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Lang {
    private static final Map<String, Lang> languages = new HashMap<>();
    @Getter
    public final String en_us;
    @Getter
    public final String zh_cn;

    protected Lang(String en_us, String zh_cn) {
        this.en_us = en_us;
        this.zh_cn = zh_cn;
    }

    public static Lang of(String key, String en_us, String zh_cn) {
        Lang lang = new Lang(en_us, zh_cn);
        languages.put(key, lang);
        return lang;
    }

    @Nullable
    public static Lang get(String key) {
        return languages.get(key);
    }

    public static void getFor(String key, Consumer<Lang> consumer) {
        Lang lang = languages.get(key);
        if (lang != null) {
            consumer.accept(lang);
        }
    }

    @FunctionalInterface
    public interface LangConsumer {
        void accept(String key, Lang lang);
    }

    public static void forEach(LangConsumer consumer) {
        languages.forEach(consumer::accept);
    }

    public static void init() {
        of("item_group.paramagic.main", "Paramagic", "参数魔法");
        of("item.paramagic.debug_wand", "Debug wand", "调试魔杖");
        of("item.paramagic.explosion_wand", "EXPLOSION WAND", "慧慧的法杖");
        of("block.paramagic.spell_edit_table", "Spell Edit Table", "法术编辑台");
    }
}
