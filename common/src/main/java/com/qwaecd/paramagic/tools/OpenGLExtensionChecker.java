package com.qwaecd.paramagic.tools;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class OpenGLExtensionChecker {
    public static final String transformFeedbackExt = "GL_EXT_transform_feedback";

    private static final Set<String> supportedExtensions = new HashSet<>();

    /**
     * 在 OpenGL 上下文创建成功后调用此方法
     */
    public static void loadSupportedExtensions() {
        // 首先检查 GL_NUM_EXTENSIONS 是否可用 (OpenGL 3.0+ 的标志)
        int numExtensions = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS);

        for (int i = 0; i < numExtensions; i++) {
            String extensionName = GL30.glGetStringi(GL11.GL_EXTENSIONS, i);
            supportedExtensions.add(extensionName);
        }
        System.out.println("Loaded " + supportedExtensions.size() + " OpenGL extensions.");
    }

    /**
     * 检查特定的扩展是否被支持
     * @param extensionName 例如 "GL_EXT_transform_feedback"
     * @return 如果支持则为 true, 否则为 false
     */
    public static boolean isExtensionSupported(String extensionName) {
        return supportedExtensions.contains(extensionName);
    }
}
