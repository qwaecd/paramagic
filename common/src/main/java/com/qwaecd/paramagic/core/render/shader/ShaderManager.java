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
import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.GL33.*;

@UtilityClass
public class ShaderManager {
    @Getter
    private Shader positionColorShader;

    public void init() {
    }

    private void loadShader() {
        positionColorShader = new Shader("position_color");
    }

    public int loadShaderProgram(String name, ShaderType type) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        ResourceLocation location =
                new ResourceLocation(
                        Constants.MOD_ID,
                        "shader/" + name + type.extension
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
            return shaderId;
        } catch (IOException e){
            Constants.LOG.error("Shader compilation error", e);
        }
        return 0;
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
