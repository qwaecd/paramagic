package com.qwaecd.paramagic.core.render.shader;

import com.qwaecd.paramagic.core.exception.ShaderException;
import com.qwaecd.paramagic.tools.ShaderTools;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgramBuilder {
    private final String name;
    private final String path;
    private final List<Integer> shaderIds;
    private final int programId;

    private ShaderProgramBuilder(String path, String name) {
        this.path = path;
        this.name = name;
        this.shaderIds = new ArrayList<>();
        this.programId = glCreateProgram();
    }

    public static Shader buildFromInfo(ShaderInfo info) {
        ShaderProgramBuilder builder = new ShaderProgramBuilder(info.getPath(), info.getFileName());

        for (ShaderType type : info.getShaderTypes()) {
            builder.addShader(type);
        }

        return builder.build();
    }

    private void addShader(ShaderType type) {
        int shaderId = ShaderTools.loadSingleShaderObject(path, name, type); // 若失败会抛异常 -> 直接外层崩溃
        glAttachShader(programId, shaderId);
        shaderIds.add(shaderId);
    }

    private Shader build() {
        if (shaderIds.isEmpty()) {
            glDeleteProgram(programId);
            throw new ShaderException("No shader stages attached for program {" + name + "}. Did the source files fail to load?");
        }

        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            String log = glGetProgramInfoLog(programId, 32768);
            // Clean up attached shaders before throwing
            shaderIds.forEach(id -> {
                glDetachShader(programId, id);
                glDeleteShader(id);
            });
            glDeleteProgram(programId);
            throw new ShaderException("Failed to link shader program {" + name + "}: " + log);
        }

        shaderIds.forEach(shaderId -> {
            glDetachShader(programId, shaderId);
            glDeleteShader(shaderId);
        });

        return new Shader(path, name, programId);
    }
}
