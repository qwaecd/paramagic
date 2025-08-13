package com.qwaecd.paramagic.core.render.state;


import static org.lwjgl.opengl.GL33.*;

public class GLStateCache {
    private RenderState last;

    public void apply(RenderState state) {
        if (last == null) {
            forceApply(state);
            last = state;
            return;
        }
        if (last.depthTest != state.depthTest)
            toggle(GL_DEPTH_TEST, state.depthTest);
        if (last.depthWrite != state.depthWrite)
            glDepthMask(state.depthWrite);

        if (last.cullFace != state.cullFace)
            toggle(GL_CULL_FACE, state.cullFace);

        if (last.blendMode != state.blendMode) {
            switch (state.blendMode) {
                case NONE -> {
                    glDisable(GL_BLEND);
                }
                case ALPHA -> {
                    glEnable(GL_BLEND);
                    glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
                    glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
                }
                case ADDITIVE -> {
                    glEnable(GL_BLEND);
                    glBlendFuncSeparate(GL_ONE, GL_ONE, GL_ONE, GL_ONE);
                    glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
                }
            }
        }
        last = state;
    }

    private void forceApply(RenderState s) {
        toggle(GL_DEPTH_TEST, s.depthTest);
        glDepthMask(s.depthWrite);

        toggle(GL_CULL_FACE, s.cullFace);
        switch (s.blendMode) {
            case NONE -> glDisable(GL_BLEND);
            case ALPHA -> {
                glEnable(GL_BLEND);
                glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
                glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
            }
            case ADDITIVE -> {
                glEnable(GL_BLEND);
                glBlendFuncSeparate(GL_ONE, GL_ONE, GL_ONE, GL_ONE);
                glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
            }
        }
    }

    private static void toggle(int cap, boolean enable) {
        if (enable) {
            glEnable(cap);
        } else {
            glDisable(cap);
        }
    }

    public void reset() {
        last = null;
    }
}
