package com.qwaecd.paramagic.core.render.state;

import static org.lwjgl.opengl.GL33.*;


public final class GLStateSnapshot {
    // Depth
    public final boolean depthTest;
    public final boolean depthWrite;
    public final int depthFunc;

    // Blend
    public final boolean blend;
    public final int srcRGB, dstRGB, srcAlpha, dstAlpha;
    public final int eqRGB, eqAlpha;

    // Cull
    public final boolean cull;
    public final int cullFaceMode;
    public final int frontFace;

    private GLStateSnapshot(
            boolean depthTest, boolean depthWrite, int depthFunc,
            boolean blend, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha, int eqRGB, int eqAlpha,
            boolean cull, int cullFaceMode, int frontFace
    ) {
        this.depthTest = depthTest;
        this.depthWrite = depthWrite;
        this.depthFunc = depthFunc;
        this.blend = blend;
        this.srcRGB = srcRGB;
        this.dstRGB = dstRGB;
        this.srcAlpha = srcAlpha;
        this.dstAlpha = dstAlpha;
        this.eqRGB = eqRGB;
        this.eqAlpha = eqAlpha;
        this.cull = cull;
        this.cullFaceMode = cullFaceMode;
        this.frontFace = frontFace;
    }

    public static GLStateSnapshot capture() {
        return new GLStateSnapshot(
                glIsEnabled(GL_DEPTH_TEST),
                glGetBoolean(GL_DEPTH_WRITEMASK),
                glGetInteger(GL_DEPTH_FUNC),

                glIsEnabled(GL_BLEND),
                glGetInteger(GL_BLEND_SRC_RGB), glGetInteger(GL_BLEND_DST_RGB),
                glGetInteger(GL_BLEND_SRC_ALPHA), glGetInteger(GL_BLEND_DST_ALPHA),
                glGetInteger(GL_BLEND_EQUATION_RGB), glGetInteger(GL_BLEND_EQUATION_ALPHA),

                glIsEnabled(GL_CULL_FACE),
                glGetInteger(GL_CULL_FACE_MODE),
                glGetInteger(GL_FRONT_FACE)
        );
    }

    public void restore() {
        toggle(GL_DEPTH_TEST, depthTest);
        glDepthMask(depthWrite);
        glDepthFunc(depthFunc);

        toggle(GL_BLEND, blend);
        glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
        glBlendEquationSeparate(eqRGB, eqAlpha);

        toggle(GL_CULL_FACE, cull);
        glCullFace(cullFaceMode);
        glFrontFace(frontFace);
    }

    private static void toggle(int cap, boolean on) {
        if (on) glEnable(cap);
        else glDisable(cap);
    }

    private static int glGetInteger(int pname) {
        return org.lwjgl.opengl.GL11C.glGetInteger(pname);
    }

    private static boolean glGetBoolean(int pname) {
        return org.lwjgl.opengl.GL11C.glGetBoolean(pname);
    }
}