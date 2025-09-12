package com.qwaecd.paramagic.data.para;

/**
 * Exception thrown when conversion fails.<br>
 * 转换失败时抛出的异常。
 */
public class ConversionException extends Exception {

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(String componentId, Class<?> dataType) {
        super(String.format("Failed to convert component '%s' of type '%s'",
                componentId, dataType.getSimpleName()));
    }

    public ConversionException(String componentId, Class<?> dataType, Throwable cause) {
        super(String.format("Failed to convert component '%s' of type '%s': %s",
                componentId, dataType.getSimpleName(), cause.getMessage()), cause);
    }
}
