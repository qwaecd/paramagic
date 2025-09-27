package com.qwaecd.paramagic.core.render.shader;

import com.qwaecd.paramagic.core.exception.ShaderException;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.*;

public class ShaderInfo {
    @Getter
    private final String path;
    @Getter
    private final String fileName;
    @Getter
    private final Set<ShaderType> shaderTypes;
    /**
     * List of subroutine information, can only be used for compute shaders.<br>
     * 着色器子程序信息列表，只能给 compute shader 使用。
     */
    @Getter
    @Nullable
    private List<SubroutineInfo> subroutineInfoList = null;

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

    public ShaderInfo addSubroutineInfo(SubroutineInfo... subroutineInfos) {
        if (!this.isComputeShader()) {
            throw new ShaderException("SubroutineInfo can only be added to compute shaders.");
        }
        this.subroutineInfoList = List.of(subroutineInfos);
        return this;
    }

    /**
     * 检查是否为计算着色器。
     */
    public boolean isComputeShader() {
        return shaderTypes.size() == 1 && shaderTypes.contains(ShaderType.COMPUTE);
    }

    /**
     * 检查是否有几何着色器。
     */
    public boolean hasGeometryShader() {
        return shaderTypes.contains(ShaderType.GEOMETRY);
    }
}
