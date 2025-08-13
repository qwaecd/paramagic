package com.qwaecd.paramagic.core.render.state;

public final class GLStateGuard implements AutoCloseable {
    private final GLStateSnapshot snapshot;

    private GLStateGuard(GLStateSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public static GLStateGuard capture() {
        return new GLStateGuard(GLStateSnapshot.capture());
    }

    @Override
    public void close() {
        snapshot.restore();
    }
}
