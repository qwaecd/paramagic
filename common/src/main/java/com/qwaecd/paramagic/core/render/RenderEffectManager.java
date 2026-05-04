package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.core.render.api.RenderEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class RenderEffectManager {
    private final ModRenderSystem system;

    private final List<RenderEffect> effects = new ArrayList<>();

    RenderEffectManager(ModRenderSystem system) {
        this.system = system;
    }

    public void update(float deltaTime) {
        Iterator<RenderEffect> iterator = this.effects.iterator();
        while (iterator.hasNext()) {
            RenderEffect effect = iterator.next();
            effect.update(deltaTime);
            if (!effect.isAlive()) {
                effect.close();
                iterator.remove();
            }
        }
    }

    public void add(RenderEffect effect) {
        if (effect == null) {
            return;
        }
        effect.onAdded(this.system);
        this.effects.add(effect);
    }

    public void remove(RenderEffect effect) {
        if (effect == null) {
            return;
        }
        effect.close();
        this.effects.remove(effect);
    }
}
