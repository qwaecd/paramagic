package com.qwaecd.paramagic.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class LangGenFabric {

    public static class EnUsGen extends FabricLanguageProvider {
        public EnUsGen(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            Lang.forEach((key, lang) -> translationBuilder.add(key, lang.getEn_us()));
        }
    }

    public static class ZhCnGen extends FabricLanguageProvider {
        public ZhCnGen(FabricDataOutput dataOutput) {
            super(dataOutput, "zh_cn");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            Lang.forEach((key, lang) -> translationBuilder.add(key, lang.getZh_cn()));
        }
    }
}
