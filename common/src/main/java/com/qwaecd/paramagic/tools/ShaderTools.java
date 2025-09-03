package com.qwaecd.paramagic.tools;

import com.mojang.blaze3d.platform.GlStateManager;
import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.GL20.*;

public class ShaderTools {
    public static int loadShaderProgram(String path, String name, ShaderManager.ShaderType type) {
        ResourceLocation location = createResourceLocation(path, name, type);
        int shaderId = glCreateShader(type.getGlType());
        boolean fileIsPresent = loadShaderSource(location, shaderId);
        return compileAndValidateShader(shaderId, location, name, type, fileIsPresent);
    }

    private static ResourceLocation createResourceLocation(String path, String name, ShaderManager.ShaderType type) {
        return new ResourceLocation(
                Paramagic.MOD_ID,
                "shaders/" + path + name + type.getExtension()
        );
    }

    private static boolean loadShaderSource(ResourceLocation location, int shaderId) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Optional<Resource> resource = resourceManager.getResource(location);

        if (resource.isPresent()) {
            try (var inputStream = resource.get().open()) {
                String shaderData = new String(inputStream.readAllBytes());
                GlStateManager.glShaderSource(shaderId, List.of(shaderData));
                return true;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load shader: " + location, e);
            }
        } else {
            return false;
        }
    }

    private static int compileAndValidateShader(int shaderId, ResourceLocation location, String name,
                                              ShaderManager.ShaderType type, boolean fileIsPresent) {
        try {
            glCompileShader(shaderId);
            if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0 || !fileIsPresent) {
                String msg = StringUtils.trim(glGetShaderInfoLog(shaderId, 32768));
                throw new IOException("Couldn't compile " + type.getName() + " program {" + name + "} : " + msg);
            }
            Paramagic.LOG.debug("Fuck shader {} compiled successfully", name);
            return shaderId;
        } catch (IOException e) {
            Paramagic.LOG.error("Shader compilation error", e);
        }
        throw new RuntimeException("Failed to load shader: " + location);
    }
}
