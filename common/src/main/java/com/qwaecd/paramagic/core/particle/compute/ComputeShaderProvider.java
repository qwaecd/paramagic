package com.qwaecd.paramagic.core.particle.compute;

import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import lombok.Getter;

public class ComputeShaderProvider {
    @Getter
    private final ComputeShader initializeRequestShader;
    @Getter
    private final ComputeShader reserveRequestShader;
    @Getter
    private final ComputeShader particleUpdateShader;

    public ComputeShaderProvider(
    ) {
        this.initializeRequestShader = get("initialize_request");
        this.reserveRequestShader = get("reserve_request");
        this.particleUpdateShader = get("particle_update");
    }

    private ComputeShader get(String name) {
        ShaderManager sm = ShaderManager.getInstance();
        Shader shader = sm.getShaderNullable(name);
        if (shader != null) {
            return new ComputeShader(shader);
        }
        return null;
    }
}
