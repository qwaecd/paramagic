package com.qwaecd.paramagic.tools;

import com.mojang.blaze3d.platform.GlStateManager;
import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.exception.ShaderException;
import com.qwaecd.paramagic.core.render.shader.ShaderType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.GL20.*;

public class ShaderTools {
    public static int loadSingleShaderObject(String path, String name, ShaderType type) {
        ResourceLocation location = createResourceLocation(path, name, type);
        int shaderId = glCreateShader(type.getGlType());

        String shaderSource = loadShaderSource(location);
        attachSourceData(shaderId, shaderSource);

        return compile(shaderId, location, type);
    }

    public static int loadComputeShaderWithExtraSources(String path, String name, ShaderType type, String additionalSource) {
        ResourceLocation location = createResourceLocation(path, name, type);
        int shaderId = glCreateShader(type.getGlType());

        String shaderSource = loadShaderSource(location) + "\n" + additionalSource;
        attachSourceData(shaderId, shaderSource);

        return compile(shaderId, location, type);
    }

    public static String loadShaderSource(String path, String name, ShaderType type) {
        ResourceLocation location = createResourceLocation(path, name, type);
        return loadShaderSource(location);
    }

    public static String loadShaderSource(ResourceLocation location) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Optional<Resource> resource = resourceManager.getResource(location);

        if (resource.isEmpty()) {
            throw new ShaderException("Shader file not found: " + location);
        }

        try (var inputStream = resource.get().open()) {
            return new String(inputStream.readAllBytes());
        } catch (Exception e) {
            throw new ShaderException("Failed to load shader: " + location, e);
        }
    }

    private static ResourceLocation createResourceLocation(String path, String name, ShaderType type) {
        return new ResourceLocation(
                Paramagic.MOD_ID,
                "shaders/" + path + name + type.getExtension()
        );
    }

    private static void attachSourceData(int shaderId, String shaderData) {
        GlStateManager.glShaderSource(shaderId, List.of(shaderData));
    }

    private static int compile(int shaderId, ResourceLocation location, ShaderType type) {
        glCompileShader(shaderId);
        int status = glGetShaderi(shaderId, GL_COMPILE_STATUS);
        if (status == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderId, 8192);
            glDeleteShader(shaderId);
            throw new ShaderException("Failed to compile " + type.getTypeName() + " shader: " + location + "\n" + log);
        }
        if (Paramagic.LOG.isDebugEnabled()) {
            Paramagic.LOG.debug("Compiled {} shader: {} (id={})", type.getTypeName(), location, shaderId);
        }
        return shaderId;
    }
}
