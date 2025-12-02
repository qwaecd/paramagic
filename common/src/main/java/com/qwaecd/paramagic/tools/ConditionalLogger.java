package com.qwaecd.paramagic.tools;

import com.qwaecd.paramagic.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

@SuppressWarnings("ClassCanBeRecord")
public class ConditionalLogger {
    private final Logger logger;
    public ConditionalLogger(Logger logger) {
        this.logger = logger;
    }

    public void logIfDev(Consumer<Logger> logAction) {
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            logAction.accept(this.logger);
        }
    }

    public static ConditionalLogger create(Logger logger) {
        return new ConditionalLogger(logger);
    }

    public static ConditionalLogger create(Class<?> clazz) {
        return new ConditionalLogger(LoggerFactory.getLogger(clazz));
    }
}
