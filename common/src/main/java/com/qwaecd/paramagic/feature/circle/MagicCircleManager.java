package com.qwaecd.paramagic.feature.circle;

import com.qwaecd.paramagic.client.renderer.MagicCircleRenderer;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MagicCircleManager {
    private static final MagicCircleManager INSTANCE = new MagicCircleManager();
    private static final Matrix4f WORLD_IDENTITY = new Matrix4f();
    private final Set<MagicCircle> activeCircles = new HashSet<>();

    private final ConcurrentLinkedQueue<MagicCircle> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<MagicCircle> pendingRemove = new ConcurrentLinkedQueue<>();
    private MagicCircleManager() {}

    public static MagicCircleManager getInstance() {
        return INSTANCE;
    }

    public void addCircle(MagicCircle magicCircle) {
        pendingAdd.add(magicCircle);
    }
    public void removeCircle(MagicCircle circle) {
        pendingRemove.add(circle);
    }

    public void removeAllCircles() {
        pendingRemove.addAll(activeCircles);
    }

    public void drawAll(MagicCircleRenderer renderer) {
        for (MagicCircle circle : activeCircles) {
            circle.draw(WORLD_IDENTITY, renderer);
        }
    }

    /**
     * Call this function only within the rendering loop, not in the game logic loop.<br>
     * 仅在渲染循环内调用该函数，不要在游戏逻辑循环内调用。<br>
     * 更新粒子效果的状态，包括其所有发射器的状态。<br>
     * @param deltaTime Seconds of time increment (time since last frame).<br>
     * 时间增量，单位秒（为距离上一帧的时间）<br>
     */
    public void update(float deltaTime) {
        MagicCircle magicCircle;
        while ((magicCircle = pendingAdd.poll()) != null) {
            activeCircles.add(magicCircle);
        }

        while ((magicCircle = pendingRemove.poll()) != null) {
            if (activeCircles.remove(magicCircle)) {
                unregisterCircleFromRenderSystem(magicCircle);
            }
        }

        for (MagicCircle circle : activeCircles) {
            circle.update(deltaTime);
        }
    }


    private void unregisterCircleFromRenderSystem(MagicCircle circle) {
        ModRenderSystem rs = ModRenderSystem.getInstance();
        List<IRenderable> toRemove = new ArrayList<>();
        class Collector {
            void collect(MagicNode node) {
                if (node.getMesh() != null) {
                    toRemove.add(node);
                }
                for (MagicNode child : node.getChildren()) {
                    collect(child);
                }
            }
        }
        new Collector().collect(circle);
        rs.removeRenderables(toRemove);
    }
}
