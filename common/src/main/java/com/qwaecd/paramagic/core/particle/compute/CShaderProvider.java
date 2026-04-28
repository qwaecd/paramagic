package com.qwaecd.paramagic.core.particle.compute;

import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;

public class CShaderProvider implements IComputeShaderProvider {
    private final ComputeShader initializeRequestShader;
    private final ComputeShader reserveRequestShader;
    private final ComputeShader particleUpdateShader;
    private final ComputeShader particleClassifyShader;
    private final ComputeShader particleBuildDrawCommandsShader;

    private final boolean computeShadersSupported;

    public CShaderProvider(boolean computeShadersSupported) {
        this.computeShadersSupported = computeShadersSupported;
        this.initializeRequestShader = get("initialize_request");
        this.reserveRequestShader = get("reserve_request");
        this.particleUpdateShader = get("particle_update");
        this.particleClassifyShader = get("particle_classify");
        this.particleBuildDrawCommandsShader = get("particle_build_draw_commands");
    }

    private ComputeShader get(String name) {
        ShaderManager sm = ShaderManager.getInstance();
        Shader shader = sm.getShaderNullable(name);
        if (shader != null) {
            return new ComputeShader(shader);
        }
        return null;
    }

    @Override
    public boolean isSupported() {
        return this.computeShadersSupported;
    }

    @Override
    public ComputeShader initializeRequestShader() {
        return this.initializeRequestShader;
    }

    @Override
    public ComputeShader reserveRequestShader() {
        return this.reserveRequestShader;
    }

    @Override
    public ComputeShader particleUpdateShader() {
        return this.particleUpdateShader;
    }

    @Override
    public ComputeShader particleClassifyShader() {
        return this.particleClassifyShader;
    }

    @Override
    public ComputeShader particleBuildDrawCommandsShader() {
        return this.particleBuildDrawCommandsShader;
    }
}
