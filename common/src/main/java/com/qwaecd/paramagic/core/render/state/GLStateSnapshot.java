package com.qwaecd.paramagic.core.render.state;

import java.util.Map;
import java.util.HashMap;

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

    // Fbo
    public final int fboId;

    // Texture
    public final Map<Integer, Integer> textureBindings;
    private static final int MAX_TEXTURE_UNITS = 8;
    // Active texture unit token (GL_TEXTURE0 + i)
    public final int activeTexture;

    private GLStateSnapshot(
            boolean depthTest, boolean depthWrite, int depthFunc,
            boolean blend, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha, int eqRGB, int eqAlpha,
            boolean cull, int cullFaceMode, int frontFace,
            int fboId,
            Map<Integer, Integer> textureBindings,
            int activeTexture
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
        this.fboId = fboId;
        this.textureBindings = textureBindings;
        this.activeTexture = activeTexture;
    }

    public static GLStateSnapshot capture() {
        // Preserve and capture texture state (active unit + per-unit 2D binding) with minimal overhead
        int currentActive = glGetInteger(GL_ACTIVE_TEXTURE);
        Map<Integer, Integer> bindings = getTextureBindings(currentActive);

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
                glGetInteger(GL_FRONT_FACE),

                glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING),

                bindings,
                currentActive
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

        glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);

        try {
            for (int unit = 0; unit < MAX_TEXTURE_UNITS; unit++) {
                int token = GL_TEXTURE0 + unit;
                Integer tex = textureBindings.get(token);
                if (tex == null) continue; // if not captured, skip
                glActiveTexture(token);
                glBindTexture(GL_TEXTURE_2D, tex);
            }
        } finally {
            glActiveTexture(this.activeTexture);
        }
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

    private static Map<Integer, Integer> getTextureBindings(int originalActiveTexture) {
        Map<Integer, Integer> result = new HashMap<>(MAX_TEXTURE_UNITS);
        for (int i = 0; i < MAX_TEXTURE_UNITS; i++) {
            int token = GL_TEXTURE0 + i;
            glActiveTexture(token);
            int bound = glGetInteger(GL_TEXTURE_BINDING_2D);
            result.put(token, bound);
        }
        glActiveTexture(originalActiveTexture);
        return result;
    }
}