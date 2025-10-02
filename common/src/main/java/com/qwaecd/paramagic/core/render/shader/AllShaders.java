package com.qwaecd.paramagic.core.render.shader;

public class AllShaders {
    public static void registerAllShaders() {
        // default shader 在 ShaderManager 里注册
//        ShaderManager.registerShaderInfo("position_color", new ShaderInfo("", "position_color"));

        ShaderManager.registerShaderInfo("sun", new ShaderInfo("", "sun"));

        postShaders();
        debugShaders();
        paraShaders();
        computeShaders();
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

    private static void computeShaders() {
        ShaderManager.registerShaderInfo("compute_demo", new ShaderInfo("compute/", "compute_demo", ShaderType.COMPUTE));
        ShaderManager.registerShaderInfo("compute_render", new ShaderInfo("compute/", "compute_render"));
        ShaderManager.registerShaderInfo("initialize_request", new ShaderInfo("compute/", "initialize_request", ShaderType.COMPUTE));
        ShaderManager.registerShaderInfo("reserve_request", new ShaderInfo("compute/", "reserve_request", ShaderType.COMPUTE));
        ShaderManager.registerShaderInfo("particle_update", new ShaderInfo("compute/", "particle_update", ShaderType.COMPUTE));
        ShaderManager.registerShaderInfo("particle_render", new ShaderInfo("particle/", "particle_render"));
    }
}
