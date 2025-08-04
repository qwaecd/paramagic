package com.qwaecd.paramagic.core.render.buffer;

import lombok.Getter;

public class Vertex {
    @Getter
    private float x;
    @Getter
    private float y;
    @Getter
    private float z;
    @Getter
    private float r;
    @Getter
    private float g;
    @Getter
    private float b;
    @Getter
    private float a;

    public Vertex(float x, float y, float z, float r, float g, float b, float a) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
