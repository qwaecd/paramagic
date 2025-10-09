package com.qwaecd.paramagic.core.render.shader;

import lombok.Getter;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

@SuppressWarnings("unused")
public enum ShaderType {
    VERTEX("vertex", ".vsh", GL_VERTEX_SHADER),
    FRAGMENT("fragment", ".fsh", GL_FRAGMENT_SHADER),
    COMPUTE("compute", ".comp", GL_COMPUTE_SHADER),
    GEOMETRY("geometry", ".gsh", GL_GEOMETRY_SHADER),
    Subroutine("subroutine", ".sub.comp", -1); // Subroutine shaders don't have a specific GL type
    @Getter
    private final String typeName;
    @Getter
    private final String extension;
    @Getter
    private final int glType;

    ShaderType(String typeName, String extension, int glType) {
        this.typeName = typeName;
        this.extension = extension;
        this.glType = glType;
    }
}
