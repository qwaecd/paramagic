package com.qwaecd.paramagic.core.render.vertex;

import lombok.Getter;
import org.joml.Vector3f;

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
    private Vector3f normal = new Vector3f(1.0f);

    boolean hasNormal = false;
    boolean hasUV = false;

    public boolean hasNormal() {
        return this.hasNormal;
    }

    public boolean hasUV() {
        return this.hasUV;
    }

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
        this.hasUV = true;
    }

    public Vertex(float x, float y, float z, float r, float g, float b, float a, Vector3f normal) {
        this(x, y, z, r, g, b, a);
        this.normal = normal;
        this.hasNormal = true;
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
        private boolean hasNormal = false;
        private boolean hasUV = false;


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
            this.hasUV = true;
            return this;
        }

        public Builder normal(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.hasNormal = true;
            return this;
        }

        public Vertex build() {
            Vertex vertex = new Vertex(x, y, z, r, g, b, a, u, v);
            vertex.hasNormal = this.hasNormal;
            vertex.hasUV = this.hasUV;
            return vertex;
        }
    }
}
