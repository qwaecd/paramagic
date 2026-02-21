package com.qwaecd.paramagic.datagen;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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
        of("item.paramagic.void_operator", "Void Operator", "空参量");
        of("item.paramagic.magic_arrow_operator", "Magic Arrow", "魔法箭");
        of("item.paramagic.para_crystal", "Para Crystal", "魔力水晶");
        of("tooltip.paramagic.para_operator_item.operator_type", "§lOperator Type: §r", "§l法术类型: §r");
        of("tooltip.paramagic.para_operator_item.transmission_delay", "§lTransmission Delay: §r", "§l传导延迟: §r");
        of("tooltip.paramagic.para_operator_item.cycle_cooldown", "§lCycle Cooldown: §r", "§l回转冷却: §r");
        of("tooltip.paramagic.magic_arrow_operator.describe", "Shoots a magic arrow forward", "向前射出一支魔法箭");
        of("tooltip.paramagic.void_operator.describe", "Do noting", "什么也不做");
    }
}
