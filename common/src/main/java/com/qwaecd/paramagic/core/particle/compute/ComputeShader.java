package com.qwaecd.paramagic.core.particle.compute;

import com.qwaecd.paramagic.core.render.shader.Shader;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL40.glGetSubroutineIndex;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

public class ComputeShader extends Shader {
    private final Map<String, Integer> subroutineMap;
    /**
     * @param path      着色器文件路径，相对于 resources 下的 shaders 目录，shaders下的传空字符串
     * @param name      着色器名称，文件名（不带扩展名）
     * @param programId 着色器程序ID
     */
    public ComputeShader(String path, String name, int programId) {
        super(path, name, programId);
        this.subroutineMap = new HashMap<>();
        loadSubroutines();
    }

    private void loadSubroutines() {
        // TODO: 加载所有的子函数缓存
        // glGetSubroutineIndex(int program, int shadertype(GL_COMPUTE_SHADER), String name)
    }

    public int getSubroutineIndex(String subroutineName) {
        return subroutineMap.getOrDefault(subroutineName, -1);
    }
}
