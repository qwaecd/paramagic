package com.qwaecd.paramagic.core.para.material;

import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import lombok.Getter;

@Getter
public class ParaMaterialProvider {
    private final RingMaterial ringMaterial;

    public ParaMaterialProvider() {
        this.ringMaterial = new RingMaterial(ShaderManager.getInstance().getShaderThrowIfNotFound("ring_para"));
    }
}
