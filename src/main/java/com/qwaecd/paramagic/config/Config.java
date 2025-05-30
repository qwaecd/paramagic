package com.qwaecd.paramagic.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static ForgeConfigSpec SPEC;

    private static ForgeConfigSpec.IntValue MAX_DEPTH;

    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        setupConfig(BUILDER);
        SPEC = BUILDER.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        builder.push("General");
        MAX_DEPTH = builder
                .comment("法术嵌套的深度")
                .comment("Maximum depth of spell nesting")
                .defineInRange("maxDepth", 10,1, Integer.MAX_VALUE);


        builder.pop();
    }

    public static int getMaxDepth() {
        return MAX_DEPTH.get();
    }
}
