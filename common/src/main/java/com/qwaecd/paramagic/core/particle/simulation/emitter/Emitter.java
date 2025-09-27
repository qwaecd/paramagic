package com.qwaecd.paramagic.core.particle.simulation.emitter;

import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.render.shader.Shader;

import javax.annotation.Nullable;

public interface Emitter {
    void update(float deltaTime, Shader updateShader);
    /**
     * @return If particles need to be emitted this frame, returns an EmissionRequest object; otherwise returns null.<br>
     * 如果在这一帧需要发射粒子，则返回一个EmissionRequest对象；否则返回null。
     */
    @Nullable EmissionRequest getEmissionRequest();
}
