package com.qwaecd.paramagic.core.render.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.qwaecd.paramagic.Constants;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.opengl.GL33.*;

@UtilityClass
public class ShaderManager {
    private static final Map<String, Shader> SHADER_REGISTRY = new HashMap<>();
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

    public void init() {
        loadShaders();
    }

    private void loadShaders() {
        positionColorShader = new Shader("", "position_color");
        magicRingShader = new Shader("", "magic_ring");
        baseBallInShader = new Shader("debug/","base_ball_in");
        baseBallOutShader = new Shader("debug/","base_ball_out");
        MagicCircleShader = new Shader("debug/","magic_circle");
        register("position_color", positionColorShader);
        register("magic_ring", magicRingShader);
        register("base_ball_in", baseBallInShader);
        register("base_ball_out", baseBallOutShader);
        register("magic_circle", MagicCircleShader);
    }

    private void register(String name, Shader shader) {
        SHADER_REGISTRY.put(name, shader);
    }

    public static Shader getShader(String name) {
        if (SHADER_REGISTRY.containsKey(name)) {
            return SHADER_REGISTRY.get(name);
        }
        Constants.LOG.warn("Shader {} not found, returning default position color shader", name);
        return positionColorShader;
    }

    public int loadShaderProgram(String path, String name, ShaderType type) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        ResourceLocation location =
                new ResourceLocation(
                        Constants.MOD_ID,
                        "shaders/" + path + name + type.extension
                );
        Optional<Resource> resource = resourceManager.getResource(location);
        int shaderId = glCreateShader(type.glType);
        boolean fileIsPresent = true;
        if (resource.isPresent()) {
            try (var inputStream = resource.get().open()) {
                String shaderData = new String(inputStream.readAllBytes());
                GlStateManager.glShaderSource(shaderId, List.of(shaderData));
            } catch (Exception e) {
                throw new RuntimeException("Failed to load shader: " + location, e);
            }
        } else {
            fileIsPresent = false;
        }
        try {
            glCompileShader(shaderId);
            if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0 || !fileIsPresent) {
                String msg = StringUtils.trim(glGetShaderInfoLog(shaderId, 32768));
                throw new IOException("Couldn't compile " + type.name + " program {" + name + "} : " + msg);
            }
            Constants.LOG.debug("Fuck shader {} compiled successfully", name);
            return shaderId;
        } catch (IOException e){
            Constants.LOG.error("Shader compilation error", e);
        }
        throw new RuntimeException("Failed to load shader: " + location);
    }

    public enum ShaderType {
        VERTEX("vertex", ".vsh", GL_VERTEX_SHADER),
        FRAGMENT("fragment", ".fsh", GL_FRAGMENT_SHADER),
        ;
        private final String name;
        private final String extension;
        private final int glType;

        ShaderType(String name, String extension, int glType) {
            this.name = name;
            this.extension = extension;
            this.glType = glType;
        }
    }
}
