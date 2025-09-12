package com.qwaecd.paramagic.assembler;

/**
 * An exception thrown when an error occurs during the assembly of a MagicCircle.
 * <p>
 * 在装配 MagicCircle 过程中发生错误时抛出的异常。
 */
public class AssemblyException extends Exception {
    public AssemblyException(String message, Throwable cause) {
        super(message, cause);
    }
}
