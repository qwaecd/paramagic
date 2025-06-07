package com.qwaecd.paramagic.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Main magic circle class that manages all elements
 */
public class MagicCircle {
    private List<Element> elements;
    private Vec3 position;
    private boolean active;
    private float totalTime;

    public MagicCircle(Vec3 position) {
        this.elements = new ArrayList<>();
        this.position = position;
        this.active = true;
        this.totalTime = 0;
    }

    public MagicCircle(Vec3 position, List<Element> elements){
        this.elements = elements;
        this.position = position;
        this.active = true;
        this.totalTime = 0;
    }

    public void update(float deltaTime) {
        if (!active) return;

        totalTime += deltaTime;

        // Sort elements by z-order before updating
        elements.sort(Comparator.comparingInt(e -> e.zOrder));

        for (Element element : elements) {
            element.update(deltaTime);
        }
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, float partialTicks) {
        if (!active) return;

        poseStack.pushPose();
        poseStack.translate(position.x, position.y, position.z);

        for (Element element : elements) {
            if (element.visible) {
                element.render(poseStack, buffer, position, partialTicks);
            }
        }

        poseStack.popPose();
    }

    public void addElement(Element element) {
        elements.add(element);
    }

    public void removeElement(Element element) {
        elements.remove(element);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public Vec3 getPosition() {
        return position;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public List<Element> getElements(){
        return  elements;
    }
}