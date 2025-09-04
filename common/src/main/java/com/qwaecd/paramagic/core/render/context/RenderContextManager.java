package com.qwaecd.paramagic.core.render.context;

import java.util.Objects;
import java.util.function.Supplier;

public class RenderContextManager {
    private static final ThreadLocal<RenderContext> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setContext(RenderContext context) {
        CONTEXT_HOLDER.set(context);
    }

    public static RenderContext getContext() {
        return Objects.requireNonNull(CONTEXT_HOLDER.get(), "RenderContext not set for this thread");
    }

    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * Executes a block of code with a specific render context.
     * Ensures the context is cleared afterwards.
     *
     * @param context The render context to use.
     * @param code    The code to execute.
     */
    @Deprecated
    public static void withContext(RenderContext context, Runnable code) {
        try {
            setContext(context);
            code.run();
        } finally {
            clearContext();
        }
    }

    /**
     * Executes a block of code that returns a value with a specific render context.
     * Ensures the context is cleared afterwards.
     *
     * @param context The render context to use.
     * @param code    The code to execute.
     * @return The value returned by the code.
     */
    @Deprecated
    public static <T> T withContext(RenderContext context, Supplier<T> code) {
        try {
            setContext(context);
            return code.get();
        } finally {
            clearContext();
        }
    }

}

