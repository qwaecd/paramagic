package com.qwaecd.paramagic.core.render.shader;

import com.qwaecd.paramagic.core.render.RenderContext;
import com.qwaecd.paramagic.core.render.buffer.WorldBuffer;
import lombok.experimental.UtilityClass;

import java.awt.*;
import static org.lwjgl.opengl.GL33.*;


@UtilityClass
public class RenderHelper {

    public WorldBuffer startLines() {
        glEnable(GL_LINE_SMOOTH);
        return new WorldBuffer(GL_LINES, ShaderManager.getPositionColorShader());
    }

    public void endLines(WorldBuffer buffer) {
        glEnable(GL_BLEND);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);
        buffer.draw();
        glDepthMask(true);
        glDisable(GL_BLEND);
    }
    public void drawLine(WorldBuffer buffer, float x1, float y1, float z1, float x2, float y2, float z2, Color color) {
        buffer.vert(x1, y1, z1, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        buffer.vert(x2, y2, z2, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

}
