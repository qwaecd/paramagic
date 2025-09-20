package com.qwaecd.paramagic.core.render.shader;

public record ShaderInfo(String path, String fileName, String[] feedbackVaryings) {
    public ShaderInfo(String path, String fileName) {
        this(path, fileName, null);
    }
}
