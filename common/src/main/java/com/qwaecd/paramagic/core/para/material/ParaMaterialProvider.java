package com.qwaecd.paramagic.core.para.material;

import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import lombok.Getter;

@Getter
public class ParaMaterialProvider {
    private final RingMaterial ringMaterial;
    private final PolygonMaterial polygonMaterial;

    public ParaMaterialProvider() {
        Shader ringParaShader = ShaderManager.getInstance().getShaderThrowIfNotFound("ring_para");
        this.ringMaterial = new RingMaterial(ringParaShader);
        // TODO: 实际上每个 para 用的 shader 都是差不多的
        this.polygonMaterial = new PolygonMaterial(ringParaShader);
    }
}
