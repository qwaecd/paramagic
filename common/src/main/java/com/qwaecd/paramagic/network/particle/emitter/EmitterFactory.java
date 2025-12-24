package com.qwaecd.paramagic.network.particle.emitter;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;

import javax.annotation.Nullable;

public interface EmitterFactory {
    @Nullable
    Emitter createEmitter(EmitterConfig config);
}
