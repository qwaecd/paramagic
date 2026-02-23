package com.qwaecd.paramagic.ui.io.mouse;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public enum CursorType {
    ARROW(GLFW.GLFW_ARROW_CURSOR),
    IBEAM(GLFW.GLFW_IBEAM_CURSOR),
    HAND(GLFW.GLFW_POINTING_HAND_CURSOR),
    RESIZE_HORIZONTAL(GLFW.GLFW_RESIZE_EW_CURSOR),
    RESIZE_VERTICAL(GLFW.GLFW_RESIZE_NS_CURSOR),
    RESIZE_DIAG1(GLFW.GLFW_RESIZE_NWSE_CURSOR),
    RESIZE_DIAG2(GLFW.GLFW_RESIZE_NESW_CURSOR),
    RESIZE_ALL(GLFW.GLFW_RESIZE_ALL_CURSOR),
    NOT_ALLOWED(GLFW.GLFW_NOT_ALLOWED_CURSOR);

    private final int glfwCursorId;
    CursorType(int glfwCursorId) {
        this.glfwCursorId = glfwCursorId;
    }

    public static void setCursor(CursorType type) {
        long window = Minecraft.getInstance().getWindow().getWindow();
        GLFW.glfwSetCursor(window, type.create());
    }

    public long create() {
        return GLFW.glfwCreateStandardCursor(glfwCursorId);
    }
}
