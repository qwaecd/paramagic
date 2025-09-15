package com.qwaecd.paramagic.core.para.material;

import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;

public class ParaMaterialProvider {
    private final Shader ringShader;

    public ParaMaterialProvider() {
        this.ringShader = ShaderManager.getInstance().getShaderThrowIfNotFound("ring_para");
    }

    public ParaMaterial createRingMaterial() {
        return new RingMaterial(this.ringShader);
    }

    public ParaMaterial createPolygonMaterial() {
        return new PolygonMaterial(this.ringShader);
    }
}
