package com.qwaecd.paramagic.core.exception;

/**
 * 基础着色器异常，所有 shader 相关运行期异常继承自它。
 */
public class ShaderException extends RuntimeException {
    public ShaderException(String message) {
        super(message);
    }
    public ShaderException(String message, Throwable cause) {
        super(message, cause);
    }
}

