package com.qwaecd.paramagic.core.render.buffer;

import lombok.Getter;

import java.awt.*;

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
    @Getter
    private float u;
    @Getter
    private float v;
    @Getter
    private float normal;

    public Vertex(float x, float y, float z, float r, float g, float b, float a) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Vertex(float x, float y, float z, float r, float g, float b, float a, float u, float v) {
        this(x, y, z, r, g, b, a);
        this.u = u;
        this.v = v;
    }

    public static class Builder {
        private float x;
        private float y;
        private float z;
        private float r;
        private float g;
        private float b;
        private float a;
        private float u;
        private float v;

        public Builder pos(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public Builder color(float r, float g, float b, float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            return this;
        }

        public Builder color(Color color) {
            this.r = color.getRed() / 255f;
            this.g = color.getGreen() / 255f;
            this.b = color.getBlue() / 255f;
            this.a = color.getAlpha() / 255f;
            return this;
        }

        public Builder uv(float u, float v) {
            this.u = u;
            this.v = v;
            return this;
        }

        public Vertex build() {
            return new Vertex(x, y, z, r, g, b, a, u, v);
        }
    }
}
