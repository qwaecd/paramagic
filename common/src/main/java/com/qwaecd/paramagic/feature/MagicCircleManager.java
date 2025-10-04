package com.qwaecd.paramagic.feature;

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
