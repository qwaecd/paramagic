package com.qwaecd.paramagic.core.render.vertex;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class VertexLayout {
    @Getter
    private final List<VertexAttribute> attributes = new ArrayList<>();
    @Getter
    private int stride = 0;

    public void addAttribute(VertexAttribute attribute) {
        attributes.add(attribute);
        stride += attribute.size() * attribute.getTypeSize();
    }

    public void addAttribute(int location, int size, int type) {
        boolean normalized = (type != GL_FLOAT);
        addAttribute(new VertexAttribute(location, size, type, normalized));
    }

    public void apply() {
        long offset = 0L;
        for (VertexAttribute attribute : attributes) {
            glVertexAttribPointer(
                    attribute.location(),
                    attribute.size(),
                    attribute.type(),
                    attribute.normalized(),
                    this.stride,
                    offset
            );
            glEnableVertexAttribArray(attribute.location());
            offset += (long) attribute.size() * attribute.getTypeSize();
        }
    }
}
