package com.qwaecd.paramagic.core.render.shader;

public class AllShaders {
    public static void registerAllShaders() {
        ShaderManager.registerShaderInfo("position_color", new ShaderInfo("", "position_color"));

        ShaderManager.registerShaderInfo("sun", new ShaderInfo("", "sun"));


        postShaders();
        debugShaders();
        paraShaders();
        particleShaders();
    }
    private static void postShaders() {
        ShaderInfo compositeShader = new ShaderInfo("post/", "full_screen");
        ShaderManager.registerShaderInfo("composite", compositeShader);
        ShaderManager.registerShaderInfo("full_screen", compositeShader);

        ShaderManager.registerShaderInfo("blur", new ShaderInfo("post/", "blur"));
        ShaderManager.registerShaderInfo("final_blit", new ShaderInfo("post/", "final_blit"));
        ShaderManager.registerShaderInfo("bloom_composite", new ShaderInfo("post/", "bloom_composite"));
    }
    private static void debugShaders() {
        ShaderManager.registerShaderInfo("base_ball_in", new ShaderInfo("debug/","base_ball_in"));
        ShaderManager.registerShaderInfo("base_ball_out", new ShaderInfo("debug/","base_ball_out"));
        ShaderManager.registerShaderInfo("debug_magic_circle", new ShaderInfo("debug/","magic_circle"));
    }

    private static void paraShaders() {
        ShaderManager.registerShaderInfo("ring_para", new ShaderInfo("para/", "ring_para"));
    }

    private static void particleShaders() {
        ShaderManager.registerShaderInfo("particle_update", new ShaderInfo("particle/", "particle_update", feedbackVaryings));
        ShaderManager.registerShaderInfo("particle_render", new ShaderInfo("particle/", "particle_render"));
    }

    public static final String[] feedbackVaryings = {
            "out_position",
            "out_velocity",
            "out_age",
            "out_lifetime",
            "out_color",
            "out_intensity",
            "out_size",
            "out_angle",
            "out_angularVelocity",
            "out_type"
    };
}
