package com.qwaecd.paramagic.core.render.shader;

public class AllShaders {
    public static void registerAllShaders() {
        // default shader 在 ShaderManager 里注册
//        ShaderManager.registerShaderInfo("position_color", new ShaderInfo("", "position_color"));

        ShaderManager.registerShaderInfo("sun", new ShaderInfo("", "sun"));

        postShaders();
        debugShaders();
        paraShaders();
        effectShaders();
        computeShaders();
    }
    private static void postShaders() {
        ShaderInfo compositeShader = new ShaderInfo("post/", "full_screen");
        ShaderManager.registerShaderInfo("composite", compositeShader);
        ShaderManager.registerShaderInfo("full_screen", compositeShader);

        ShaderManager.registerShaderInfo("blur", new ShaderInfo("post/", "blur"));
        ShaderManager.registerShaderInfo("final_blit", new ShaderInfo("post/", "final_blit"));
        ShaderManager.registerShaderInfo("final_compose", new ShaderInfo("post/", "final_compose"));
        ShaderManager.registerShaderInfo("screen_warp", new ShaderInfo("post/", "screen_warp"));
        ShaderManager.registerShaderInfo("distortion_field", new ShaderInfo("post/", "distortion_field"));
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

    private static void effectShaders() {
        ShaderManager.registerShaderInfo("laser_cylinder", new ShaderInfo("effect/", "laser_cylinder"));
    }

    private static void computeShaders() {
        ShaderManager.registerShaderInfo("initialize_request", new ShaderInfo("compute/", "initialize_request", ShaderType.COMPUTE));
        ShaderManager.registerShaderInfo("reserve_request", new ShaderInfo("compute/", "reserve_request", ShaderType.COMPUTE));
        ShaderManager.registerShaderInfo("particle_update", new ShaderInfo("compute/", "particle_update", ShaderType.COMPUTE));
        ShaderManager.registerShaderInfo("particle_classify", new ShaderInfo("compute/", "particle_classify", ShaderType.COMPUTE));
        ShaderManager.registerShaderInfo("particle_build_draw_commands", new ShaderInfo("compute/", "particle_build_draw_commands", ShaderType.COMPUTE));

        // Point pass: vertex + fragment
        ShaderManager.registerShaderInfo("particle_render_point",
                new ShaderInfo("particle/", "particle_render", ShaderType.VERTEX, ShaderType.FRAGMENT));
        // Shape pass: vertex + geometry + fragment, triangle/quad selected by uniform.
        ShaderManager.registerShaderInfo("particle_render_shape",
                new ShaderInfo("particle/", "particle_render", ShaderType.VERTEX, ShaderType.GEOMETRY, ShaderType.FRAGMENT));

        // Keep legacy alias for compatibility.
        ShaderManager.registerShaderInfo("particle_render", new ShaderInfo("particle/", "particle_render"));
    }
}
