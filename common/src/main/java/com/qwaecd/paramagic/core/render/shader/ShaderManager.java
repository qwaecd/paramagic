package com.qwaecd.paramagic.core.render.shader;

import com.qwaecd.paramagic.Paramagic;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL33.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL33.GL_VERTEX_SHADER;

public class ShaderManager {
    private static ShaderManager INSTANCE;

    private final Map<String, Shader> SHADER_REGISTRY;
    @Getter
    private Shader positionColorShader;
    @Getter
    private Shader magicRingShader;
    @Getter
    private Shader baseBallInShader;
    @Getter
    private Shader baseBallOutShader;
    @Getter
    private Shader MagicCircleShader;
    @Getter
    private Shader compositeShader;
    @Getter
    private Shader emissiveMagicShader;

    private ShaderManager() {
        this.SHADER_REGISTRY = new HashMap<>();
    }
    public static void init() {
        if (INSTANCE != null) {
            Paramagic.LOG.warn("ShaderManager is already initialized.");
            return;
        }
        INSTANCE = new ShaderManager();
        INSTANCE.loadShaders();
    }

    public static ShaderManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ShaderManager has not been initialized. Please call init() first.");
        }
        return INSTANCE;
    }

    private void loadShaders() {
        positionColorShader = new Shader("", "position_color");
        magicRingShader = new Shader("", "magic_ring");
        baseBallInShader = new Shader("debug/","base_ball_in");
        baseBallOutShader = new Shader("debug/","base_ball_out");
        MagicCircleShader = new Shader("debug/","magic_circle");
        compositeShader = new Shader("post/", "full_screen");
        emissiveMagicShader = new Shader("magic/emissive/", "emissive_magic_circle");
        register("position_color", positionColorShader);
        register("magic_ring", magicRingShader);
        register("base_ball_in", baseBallInShader);
        register("base_ball_out", baseBallOutShader);
        register("debug_magic_circle", MagicCircleShader);
        register("magic_circle", new Shader("magic/", "magic_circle"));
        register("composite", compositeShader);
        register("full_screen", compositeShader);
        register("blur", new Shader("post/", "blur"));
        register("final_blit", new Shader("post/", "final_blit"));
        register("sun", new Shader("", "sun"));
        register("bloom_composite", new Shader("post/", "bloom_composite"));
        register("emissive_magic", emissiveMagicShader);
    }

    private void register(String registerName, Shader shader) {
        SHADER_REGISTRY.put(registerName, shader);
    }

    public Shader getShader(String name) {
        if (SHADER_REGISTRY.containsKey(name)) {
            return SHADER_REGISTRY.get(name);
        }
        Paramagic.LOG.warn("Shader {} not found, returning default position color shader", name);
        return positionColorShader;
    }

    public Shader getShaderThrowIfNotFound(String name) {
        if (!SHADER_REGISTRY.containsKey(name)) {
            throw new RuntimeException("Shader " + name + " not found in registry.");
        }
        return SHADER_REGISTRY.get(name);
    }

    public enum ShaderType {
        VERTEX("vertex", ".vsh", GL_VERTEX_SHADER),
        FRAGMENT("fragment", ".fsh", GL_FRAGMENT_SHADER),
        ;
        @Getter
        private final String name;
        @Getter
        private final String extension;
        @Getter
        private final int glType;

        ShaderType(String name, String extension, int glType) {
            this.name = name;
            this.extension = extension;
            this.glType = glType;
        }
    }
}
