package com.qwaecd.paramagic.core.render.shader;

public record ShaderInfo(String path, String fileName, String[] feedbackVaryings, boolean isComputeShader) {
    public ShaderInfo(String path, String fileName, String[] feedbackVaryings) {
        this(path, fileName, feedbackVaryings, false);
    }
    public ShaderInfo(String path, String fileName) {
        this(path, fileName, null, false);
    }
}
