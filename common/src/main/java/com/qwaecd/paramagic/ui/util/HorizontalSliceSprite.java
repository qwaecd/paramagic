package com.qwaecd.paramagic.ui.util;

import net.minecraft.resources.ResourceLocation;

public class HorizontalSliceSprite {
    /**
     * <pre>
     * ┌───┬─────┬───┐
     * │ L │  C  │ R │
     * └───┴─────┴───┘
     * </pre>
     */
    private final Sprite[] slices = new Sprite[3];

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    public final int texWidth, texHeight;

    private HorizontalSliceSprite(int texWidth, int texHeight, Sprite[] slices) {
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        System.arraycopy(slices, 0, this.slices, 0, this.slices.length);
    }

    public static Builder builder(ResourceLocation texture, int texWidth, int texHeight) {
        return new Builder(texture, texWidth, texHeight);
    }

    public Sprite[] getSlices() {
        return this.slices;
    }

    public int getHeight() {
        return this.slices[CENTER].height;
    }

    public static class Builder {
        private static final int TOTAL_SLICES = 3;

        private final ResourceLocation texture;
        private final int texWidth;
        private final int texHeight;
        private final int[] us = new int[TOTAL_SLICES];
        private final int[] vs = new int[TOTAL_SLICES];
        private final int[] widths = new int[TOTAL_SLICES];
        private final int[] heights = new int[TOTAL_SLICES];
        private final boolean[] configured = new boolean[TOTAL_SLICES];

        private Builder(ResourceLocation texture, int texWidth, int texHeight) {
            if (texture == null) {
                throw new IllegalArgumentException("texture cannot be null");
            }
            if (texWidth <= 0 || texHeight <= 0) {
                throw new IllegalArgumentException("texture size must be positive");
            }

            this.texture = texture;
            this.texWidth = texWidth;
            this.texHeight = texHeight;
        }

        public Builder slice(int index, int u, int v, int width, int height) {
            checkSliceIndex(index);
            if (u < 0 || v < 0) {
                throw new IllegalArgumentException("slice uv cannot be negative");
            }
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("slice size must be positive");
            }

            this.us[index] = u;
            this.vs[index] = v;
            this.widths[index] = width;
            this.heights[index] = height;
            this.configured[index] = true;
            return this;
        }

        public HorizontalSliceSprite build() {
            Sprite[] slices = new Sprite[TOTAL_SLICES];
            for (int i = 0; i < TOTAL_SLICES; i++) {
                if (!this.configured[i]) {
                    throw new IllegalStateException("slice " + i + " is not configured");
                }
                slices[i] = new Sprite(
                        this.texture,
                        this.us[i],
                        this.vs[i],
                        this.widths[i],
                        this.heights[i],
                        this.texWidth,
                        this.texHeight
                );
            }
            return new HorizontalSliceSprite(this.texWidth, this.texHeight, slices);
        }

        private static void checkSliceIndex(int index) {
            if (index < 0 || index >= TOTAL_SLICES) {
                throw new IllegalArgumentException("slice index must be in [0, 2]");
            }
        }
    }
}
