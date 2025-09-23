package com.qwaecd.paramagic.core.render.shader;

import lombok.Getter;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

@SuppressWarnings("unused")
public enum ShaderType {
    VERTEX("vertex", ".vsh", GL_VERTEX_SHADER),
    FRAGMENT("fragment", ".fsh", GL_FRAGMENT_SHADER),
    COMPUTE("compute", ".glsl", GL_COMPUTE_SHADER);
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
