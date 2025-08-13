package com.qwaecd.paramagic.core.render.queue;

import com.qwaecd.paramagic.core.render.IRenderable;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RenderQueue {
    public final List<RenderItem> opaque = new ArrayList<>();     // 包含 CUTOUT
    public final List<RenderItem> transparent = new ArrayList<>();
    public final List<RenderItem> additive = new ArrayList<>();

    public void clear() {
        opaque.clear();
        transparent.clear();
        additive.clear();
    }

    public void gather(List<IRenderable> scene, Vector3d cameraPos) {
        clear();
        for (IRenderable r : scene) {
            RenderType t = getType(r);
            RenderItem item = new RenderItem(r, t, cameraPos);
            switch (t) {
                case OPAQUE, CUTOUT -> opaque.add(item);
                case TRANSPARENT -> transparent.add(item);
                case ADDITIVE -> additive.add(item);
            }
        }
    }

    private RenderType getType(IRenderable r) {
        var material = r.getMaterial();
        return material.getRenderType();
    }

    public void sortForDraw() {
        // 不透明：距离排序，近到远
        opaque.sort(Comparator.comparingDouble(it -> it.distanceSq));
        // 半透明/加色：远到近
        transparent.sort(Comparator.comparingDouble((RenderItem it) -> it.distanceSq).reversed());
        additive.sort(Comparator.comparingDouble((RenderItem it) -> it.distanceSq).reversed());
    }
}
