package com.qwaecd.paramagic.ui.util;

import org.joml.Vector4f;

/**
 * 原点是屏幕左上角，x向右，y向下
 */
public final class Rect {
    public float x;
    public float y;
    public float w;
    public float h;

    public Rect() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.w = 0.0f;
        this.h = 0.0f;
    }

    public Rect(Rect other) {
        this.x = other.x;
        this.y = other.y;
        this.w = other.w;
        this.h = other.h;
    }

    public Rect(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public static Rect fromVec4f(Vector4f v) {
        return new Rect(v.x, v.y, v.z, v.w);
    }

    public boolean contains(float px, float py) {
        return px >= this.x && px < this.x + this.w && py >= this.y && py < this.y + this.h;
    }

    public void set(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void set(Rect other) {
        this.x = other.x;
        this.y = other.y;
        this.w = other.w;
        this.h = other.h;
    }

    public void set(Vector4f v) {
        this.x = v.x;
        this.y = v.y;
        this.w = v.z;
        this.h = v.w;
    }

    public void setXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setWH(float w, float h) {
        this.w = w;
        this.h = h;
    }

    public Rect inset(float inset) {
        return this.inset(inset, new Rect());
    }

    public Rect inset(float inset, Rect dest) {
        dest.set(
                this.x + inset,
                this.y + inset,
                Math.max(0.0f, this.w - inset * 2.0f),
                Math.max(0.0f, this.h - inset * 2.0f)
        );
        return dest;
    }

    public Rect inset(float insetX, float insetY, Rect dest) {
        dest.set(
                this.x + insetX,
                this.y + insetY,
                Math.max(0.0f, this.w - insetX * 2.0f),
                Math.max(0.0f, this.h - insetY * 2.0f)
        );
        return dest;
    }

    public Rect inset(float insetX, float insetY) {
        return this.inset(insetX, insetY, new Rect());
    }

    @Override
    public String toString() {
        return "Rect{" +
                "x=" + x +
                ", y=" + y +
                ", w=" + w +
                ", h=" + h +
                '}';
    }
}
