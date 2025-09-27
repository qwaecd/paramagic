package com.qwaecd.paramagic.core.render.shader;

import lombok.Getter;

public class SubroutineInfo {
    @Getter
    private final String path;
    @Getter
    private final String fileName;
    @Getter
    private final ShaderType shaderType = ShaderType.Subroutine;
    /**
     * @param path        路径。
     * @param fileName    文件名，不带拓展名。
     */
    public SubroutineInfo(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }
}
