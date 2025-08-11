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

    public VertexLayout addNextAttribute(VertexAttribute attribute) {
        attributes.add(attribute);
        stride += attribute.size() * attribute.getTypeSize();
        return this;
    }


    public void apply() {
        long offset = 0L;
        for (VertexAttribute attribute : attributes) {
            glEnableVertexAttribArray(attribute.location());
            glVertexAttribPointer(
                    attribute.location(),
                    attribute.size(),
                    attribute.type(),
                    attribute.normalized(),
                    this.stride,
                    offset
            );
            offset += (long) attribute.size() * attribute.getTypeSize();
        }
    }
}
