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

    public Rect(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public static Rect fromVec4f(Vector4f v) {
        return new Rect(v.x, v.y, v.z, v.w);
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
}
