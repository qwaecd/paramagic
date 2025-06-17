package com.qwaecd.paramagic.feature;

import com.qwaecd.paramagic.api.animation.AnimationKeyframe;
import com.qwaecd.paramagic.api.animation.AnimationTimeline;
import com.qwaecd.paramagic.api.animation.EasingType;
import com.qwaecd.paramagic.elements.*;
import com.qwaecd.paramagic.util.MagicCircleBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;

import java.awt.*;

/**
 * Example magic circle configurations
 */
public class MagicCircleExamples {

    /**
     * Creates a basic magic circle with rotating rings
     */
    public static MagicCircle createBasicCircle(Vec3 position) {
        return new MagicCircleBuilder(position)
                .addCircle(5f, 0.2f, new Color(255, 128, 0, 200))
                .addRotatingCircle(3f, 0.1f, new Color(0, 200, 255, 150), 30f)
                .addText("⚛", 0, 0, new Color(255, 255, 255, 200), 24)
                .addRune("☉", 0, 5f, 16f, new Color(255, 200, 0, 255))
                .addRune("☽", 0, -5f, 16f, new Color(200, 200, 255, 255))
                .build();
    }

    /**
     * Creates an advanced magic circle with animations
     */
    public static MagicCircle createAdvancedCircle(Vec3 position) {
        MagicCircle circle = new MagicCircle(position);

        // Main outer ring with animation
        CircleElement outerRing = new CircleElement(6f, 0.3f);
        outerRing.setColor(new Color(255, 100, 0, 180));
        AnimationTimeline outerAnimation = new AnimationTimeline(4f, true, 0f, EasingType.EASE_IN_OUT);
        outerAnimation.addKeyframe("alpha", new AnimationKeyframe(0.2f, 1.0f, EasingType.EASE_IN_OUT));
        outerRing.setAnimation(outerAnimation);
        circle.addElement(outerRing);

        // Middle rotating ring
        CircleElement middleRing = new CircleElement(4f, 0.15f);
        middleRing.setColor(new Color(0, 150, 255, 150));
        middleRing.setRotationSpeed(45f);
        circle.addElement(middleRing);

        // Inner ring with pulsing animation
        CircleElement innerRing = new CircleElement(2f, 0.1f);
        innerRing.setColor(new Color(255, 255, 255, 200));
        AnimationTimeline innerAnimation = new AnimationTimeline(2f, true, 0f, EasingType.EASE_IN_OUT);
        innerAnimation.addKeyframe("scale", new AnimationKeyframe(
                new Vector2f(0.8f, 0.8f), new Vector2f(1.2f, 1.2f), EasingType.EASE_IN_OUT));
        innerRing.setAnimation(innerAnimation);
        circle.addElement(innerRing);

        // Runic symbols around the circle
        String[] runes = {"ᚠ", "ᚢ", "ᚦ", "ᚨ", "ᚱ", "ᚲ", "ᚷ", "ᚹ"};
        for (int i = 0; i < runes.length; i++) {
            float angle = (float)(2 * Math.PI * i / runes.length);
            float x = (float)Math.cos(angle) * 5.5f;
            float y = (float)Math.sin(angle) * 5.5f;

            RuneElement rune = new RuneElement(runes[i], 14f);
            rune.setOffset(x, y);
            rune.setColor(new Color(255, 200, 100, 220));
            rune.setRotationSpeed(15f);
            circle.addElement(rune);
        }

        // Central symbol
        TextElement centerSymbol = new TextElement("⧨", "default", 32);
        centerSymbol.setColor(new Color(255, 255, 255, 255));
        AnimationTimeline centerAnimation = new AnimationTimeline(3f, true, 0f, EasingType.EASE_IN_OUT);
        centerAnimation.addKeyframe("rotation", new AnimationKeyframe(0f, 360f, EasingType.LINEAR));
        centerSymbol.setAnimation(centerAnimation);
        circle.addElement(centerSymbol);

        // Connecting lines
        for (int i = 0; i < 6; i++) {
            float angle = (float)(Math.PI / 3 * i);
            float x2 = (float)Math.cos(angle) * 3.5f;
            float y2 = (float)Math.sin(angle) * 3.5f;

            LineElement line = new LineElement(new Vector2f(0, 0), new Vector2f(x2, y2), 0.05f);
            line.setColor(new Color(100, 200, 255, 100));
            circle.addElement(line);
        }

        // Particle effects
        ParticleElement particles = new ParticleElement(ParticleTypes.WITCH, 3, 0.3f, 40, true);
        particles.setOffset(0, 0);
        circle.addElement(particles);

        return circle;
    }

    /**
     * Creates a ritual summoning circle
     */
    public static MagicCircle createSummoningCircle(Vec3 position) {
        MagicCircle circle = new MagicCircle(position);

        // Triple ring structure
        circle.addElement(createRing(7f, 0.4f, new Color(139, 0, 0, 200), 0f));
        circle.addElement(createRing(5.5f, 0.2f, new Color(255, 140, 0, 150), -20f));
        circle.addElement(createRing(4f, 0.15f, new Color(255, 215, 0, 120), 40f));

        // Pentagram
        createPentagram(circle, 3f, new Color(200, 0, 0, 180));

        // Ancient symbols at cardinal points{"♄", "♃", "♂", "☉", "♀", "☿", "☽", "⚹"}
        String[] symbols = {"*", "*", "*", "*", "*", "*", "*", "*"};
        for (int i = 0; i < symbols.length; i++) {
            float angle = (float)(2 * Math.PI * i / symbols.length);
            float x = (float)Math.cos(angle) * 6.2f * 2;
            float y = (float)Math.sin(angle) * 6.2f * 2;

            RuneElement symbol = new RuneElement(symbols[i], 1f);
//            symbol.setOffset(x-3.2f, y+0.5f);
            symbol.setOffset(x, y);
            symbol.setColor(new Color(139, 0, 0, 255));
            symbol.setRotationSpeed(1f);

            // Pulsing animation
            AnimationTimeline pulse = new AnimationTimeline(2f + i * 0.2f, true, i * 0.1f, EasingType.EASE_IN_OUT);
            pulse.addKeyframe("alpha", new AnimationKeyframe(0.4f, 1.0f, EasingType.EASE_IN_OUT));
            symbol.setAnimation(pulse);

            circle.addElement(symbol);
        }

        // Central flame effect
        ParticleElement flames = new ParticleElement(ParticleTypes.FLAME, 5, 0.1f, 30, true);
        flames.setOffset(0, 0);
        circle.addElement(flames);

        return circle;
    }

    /**
     * Creates a healing circle with soft, flowing animations
     */
    public static MagicCircle createHealingCircle(Vec3 position) {
        MagicCircle circle = new MagicCircle(position);

        // Gentle outer ring
        CircleElement outerRing = new CircleElement(4.5f, 3f);
        outerRing.setColor(new Color(50, 255, 50, 120));
        AnimationTimeline breathe = new AnimationTimeline(4f, true, 0f, EasingType.EASE_IN_OUT);
        breathe.addKeyframe("scale", new AnimationKeyframe(
                new Vector2f(0.9f, 0.9f), new Vector2f(1.1f, 1.1f), EasingType.EASE_IN_OUT));
        breathe.addKeyframe("alpha", new AnimationKeyframe(0.3f, 0.8f, EasingType.EASE_IN_OUT));
        outerRing.setAnimation(breathe);
        circle.addElement(outerRing);

        // Flowing middle rings
        for (int i = 0; i < 4; i++) {
            CircleElement ring = new CircleElement(3f - i * 0.7f, 1f);
            ring.setColor(new Color(100, 255, 150, 100 - i * 20));
            ring.setRotationSpeed(10f + i * 5f);
            circle.addElement(ring);
        }

        // Healing symbols{"♥", "☤", "✚", "☩"}
        String[] healSymbols = {"✚", "✚", "✚", "✚"};
        for (int i = 0; i < healSymbols.length; i++) {
            double angle = (float)(2f * Math.PI / healSymbols.length * i);
            float x = (float)Math.cos(angle);
            float y = (float)Math.sin(angle);

            RuneElement symbol = new RuneElement(healSymbols[i], 0.8f);
            symbol.setOffset(x, y);
            symbol.setColor(new Color(255, 255, 255, 200));

            AnimationTimeline glow = new AnimationTimeline(3f, true, i * 0.5f, EasingType.EASE_IN_OUT);
            glow.addKeyframe("color", new AnimationKeyframe(
                    new Color(100, 255, 100, 150), new Color(255, 255, 255, 255), EasingType.EASE_IN_OUT));
            symbol.setAnimation(glow);

            circle.addElement(symbol);
        }

        // Gentle particles
        ParticleElement healing = new ParticleElement(ParticleTypes.HAPPY_VILLAGER, 2, 0.2f, 60, true);
        healing.setOffset(0, 0);
        circle.addElement(healing);

        return circle;
    }

    // Helper methods
    private static CircleElement createRing(float radius, float thickness, Color color, float rotationSpeed) {
        CircleElement ring = new CircleElement(radius, thickness);
        ring.setColor(color);
        ring.setRotationSpeed(rotationSpeed);
        return ring;
    }

    private static void createPentagram(MagicCircle circle, float radius, Color color) {
        // Create pentagram using lines
        for (int i = 0; i < 5; i++) {
            float angle1 = (float)(2 * Math.PI * i / 5) - (float)Math.PI / 2;
            float angle2 = (float)(2 * Math.PI * ((i + 2) % 5) / 5) - (float)Math.PI / 2;

            float x1 = (float)Math.cos(angle1) * radius;
            float y1 = (float)Math.sin(angle1) * radius;
            float x2 = (float)Math.cos(angle2) * radius;
            float y2 = (float)Math.sin(angle2) * radius;

            LineElement line = new LineElement(new Vector2f(x1, y1), new Vector2f(x2, y2), 0.1f);
            line.setColor(color);
            circle.addElement(line);
        }
    }
}
