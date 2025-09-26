package com.qwaecd.paramagic.core.render.shader;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ShaderInfo {
    @Getter
    private final String path;
    @Getter
    private final String fileName;
    @Getter
    private final Set<ShaderType> shaderTypes;

    /**
     * @param path        着色器文件相对于 "shaders/" 目录的路径。
     * @param fileName    着色器文件名（不带扩展名）。
     * @param shaderTypes 一个包含所有着色器阶段的 Set。
     */
    public ShaderInfo(String path, String fileName, Set<ShaderType> shaderTypes) {
        this.path = path;
        this.fileName = fileName;
        this.shaderTypes = Collections.unmodifiableSet(shaderTypes);
    }

    /**
     * @param path        路径。
     * @param fileName    文件名。
     * @param shaderTypes 着色器阶段，例如 ShaderType.VERTEX, ShaderType.FRAGMENT。
     */
    public ShaderInfo(String path, String fileName, ShaderType... shaderTypes) {
        this(path, fileName, new HashSet<>(Arrays.asList(shaderTypes)));
    }

    /**
     * 仅有顶点和片段着色器的标准图形着色器。
     * @param path     路径。
     * @param fileName 文件名。
     */
    public ShaderInfo(String path, String fileName) {
        this(path, fileName, ShaderType.VERTEX, ShaderType.FRAGMENT);
    }

    /**
     * 检查是否为计算着色器。
     */
    public boolean isComputeShader() {
        return shaderTypes.size() == 1 && shaderTypes.contains(ShaderType.COMPUTE);
    }
}
