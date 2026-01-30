package com.qwaecd.paramagic.ui;

import net.minecraft.util.FastColor;

public class UIColor {
    public final int color;

    public UIColor(int color) {
        this.color = color;
    }

    public UIColor(int r, int g, int b, int a) {
        this(fromRGBA(r, g, b, a));
    }

    public int r() {
        return red(this.color);
    }

    public int g() {
        return green(this.color);
    }

    public int b() {
        return blue(this.color);
    }

    public int a() {
        return alpha(this.color);
    }

    public static int fromRGBA(int r, int g, int b, int a) {
        return FastColor.ARGB32.color(a, r, g, b);
    }

    public static int fromRGBA4f(float r, float g, float b, float a) {
        return fromRGBA(
                Math.round(r * 255.0f),
                Math.round(g * 255.0f),
                Math.round(b * 255.0f),
                Math.round(a * 255.0f)
        );
    }

    public static int red(int color) {
        return FastColor.ARGB32.red(color);
    }

    public static int green(int color) {
        return FastColor.ARGB32.green(color);
    }

    public static int blue(int color) {
        return FastColor.ARGB32.blue(color);
    }

    public static int alpha(int color) {
        return FastColor.ARGB32.alpha(color);
    }
}
