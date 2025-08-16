package com.qwaecd.paramagic.core.render.post.effect;

public interface IPostProcessingEffect extends AutoCloseable {
    void initialize(int width, int height);
    void onResize(int newWidth, int newHeight);
    int apply(int inputTextureId);
    boolean isEnabled();
}
