package com.qwaecd.paramagic.feature;

import com.qwaecd.paramagic.client.animation.Animator;
import com.qwaecd.paramagic.client.renderer.MagicCircleRenderer;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MagicNode implements IRenderable {
    public  final Transform transform;
    private final AbstractMaterial material;
    private final Mesh mesh;
    @Getter
    private final List<MagicNode> children;
    @Getter
    private Matrix4f worldTransform;
    @Getter
    private MagicNode parent = null;
    @Getter
    @Setter
    private Animator animator = null;

    public MagicNode(Mesh mesh, AbstractMaterial material) {
        this.mesh = mesh;
        this.material = material;
        this.children = new ArrayList<>();
        this.transform = new Transform();
        this.worldTransform = new Matrix4f();
    }

    public void addChild(MagicNode child) {
        this.children.add(child);
        child.parent = this;
    }

    /**
     * Call this function only within the rendering loop, not in the game logic loop.<br>
     * 仅在渲染循环内调用该函数，不要在游戏逻辑循环内调用。<br>
     * 更新粒子效果的状态，包括其所有发射器的状态。<br>
     * @param deltaTime Seconds of time increment (time since last frame).<br>
     * 时间增量，单位秒（为距离上一帧的时间）<br>
     */
    public void update(float deltaTime) {
        if (this.animator != null) {
            this.animator.update(deltaTime);
        }
        for (MagicNode child : this.children) {
            child.update(deltaTime);
        }
    }

    public void draw(Matrix4f parentWorldTransform, MagicCircleRenderer renderer) {
        parentWorldTransform.mul(this.transform.getModelMatrix(), this.worldTransform);
        if (this.mesh != null && this.material != null) {
            renderer.submit(this);
        }
        for (MagicNode child : this.children) {
            child.draw(this.worldTransform, renderer);
        }
    }


    @Override
    public Mesh getMesh() {
        return this.mesh;
    }

    @Override
    public AbstractMaterial getMaterial() {
        return this.material;
    }

    @Override
    public Transform getTransform() {
        return this.transform;
    }

    @Override
    public Optional<Matrix4f> getPrecomputedWorldTransform() {
        return Optional.of(this.worldTransform);
    }
}
