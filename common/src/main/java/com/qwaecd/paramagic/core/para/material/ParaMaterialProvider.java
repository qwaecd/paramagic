package com.qwaecd.paramagic.core.para.material;

import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import lombok.Getter;

@Getter
public class ParaMaterialProvider {
    private final RingMaterial ringMaterial;

    public ParaMaterialProvider() {
        // TODO: 实现具体的Shader
        this.ringMaterial = new RingMaterial(ShaderManager.getInstance().getShaderThrowIfNotFound("position_color"));
    }
}
