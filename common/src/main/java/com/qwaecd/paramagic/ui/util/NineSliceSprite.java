package com.qwaecd.paramagic.ui.util;


import net.minecraft.resources.ResourceLocation;

public class NineSliceSprite {
    /**
     * <pre>
     * ┌───┬─────┬───┐
     * │C0 │ C1  │C2 │
     * ├───┼─────┼───┤
     * │C3 │ C4  │C5 │
     * ├───┼─────┼───┤
     * │C6 │ C7  │C8 │
     * └───┴─────┴───┘
     * </pre>
     */
    private final Sprite[] slices = new Sprite[9];

    public static final int[] corners = {0, 2, 6, 8};
    public static final int[] edges = {1, 3, 5, 7};
    public static final int center = 4;

    // 纹理的总尺寸
    public final int texWidth, texHeight;

    private NineSliceSprite(int texWidth, int texHeight, Sprite[] slices) {
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

    public static class Builder {
        private static final int TOTAL_SLICES = 9;

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

        public NineSliceSprite build() {
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
            return new NineSliceSprite(this.texWidth, this.texHeight, slices);
        }

        private static void checkSliceIndex(int index) {
            if (index < 0 || index >= TOTAL_SLICES) {
                throw new IllegalArgumentException("slice index must be in [0, 8]");
            }
        }
    }
}
