package com.qwaecd.paramagic.core.render.blackhole;

import com.qwaecd.paramagic.core.particle.compute.ComputeShader;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;

import javax.annotation.Nullable;

public class BlackHolePost {

    @Nullable
    private final ComputeShader computeShader;

    public BlackHolePost() {
        Shader shaderNullable = ShaderManager.getInstance().getShaderNullable("black_hole_post");
        this.computeShader = shaderNullable == null ? null : new ComputeShader(shaderNullable);
    }

    void init() {

    }
}
