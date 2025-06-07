package com.qwaecd.paramagic.util;

import com.qwaecd.paramagic.elements.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;

import java.awt.*;

public class MagicCircleBuilder {
    private MagicCircle circle;

    public MagicCircleBuilder(Vec3 position) {
        this.circle = new MagicCircle(position);
    }

    public MagicCircleBuilder addCircle(float radius, float thickness, Color color) {
        CircleElement element = new CircleElement(radius, thickness);
        element.setColor(color);
        circle.addElement(element);
        return this;
    }

    public MagicCircleBuilder addRotatingCircle(float radius, float thickness, Color color, float rotationSpeed) {
        CircleElement element = new CircleElement(radius, thickness);
        element.setColor(color);
        element.setRotationSpeed(rotationSpeed);
        circle.addElement(element);
        return this;
    }

    public MagicCircleBuilder addText(String text, float x, float y, Color color, int fontSize) {
        TextElement element = new TextElement(text, "default", fontSize);
        element.setOffset(x, y);
        element.setColor(color);
        circle.addElement(element);
        return this;
    }

    public MagicCircleBuilder addRune(String glyph, float x, float y, float size, Color color) {
        RuneElement element = new RuneElement(glyph, size);
        element.setOffset(x, y);
        element.setColor(color);
        circle.addElement(element);
        return this;
    }

    public MagicCircleBuilder addLine(float x1, float y1, float x2, float y2, float thickness, Color color) {
        LineElement element = new LineElement(new Vector2f(x1, y1), new Vector2f(x2, y2), thickness);
        element.setColor(color);
        circle.addElement(element);
        return this;
    }

    public MagicCircleBuilder addParticles(ParticleOptions particleType, float x, float y, int count, float speed) {
        ParticleElement element = new ParticleElement(particleType, count, speed, 60, true);
        element.setOffset(x, y);
        circle.addElement(element);
        return this;
    }

    public MagicCircle build() {
        return circle;
    }
}
