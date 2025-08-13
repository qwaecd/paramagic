package com.qwaecd.paramagic.core.render.state;

public class RenderState {
    public final boolean depthTest;
    public final boolean depthWrite;
    public final boolean cullFace;
    public final BlendMode blendMode;

    public RenderState(boolean depthTest, boolean depthWrite, BlendMode blendMode, boolean cullFace) {
        this.depthTest = depthTest;
        this.depthWrite = depthWrite;
        this.cullFace = cullFace;
        this.blendMode = blendMode;
    }

    public enum BlendMode {
        NONE,
        ALPHA,
        ADDITIVE;
    }

    public static final RenderState OPAQUE   = new RenderState(true,  true,  BlendMode.NONE,    true);
    public static final RenderState ALPHA    = new RenderState(true,  false, BlendMode.ALPHA,   false);
    public static final RenderState ADDITIVE = new RenderState(true,  false, BlendMode.ADDITIVE,false);
}
