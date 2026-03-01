package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.ui_project.edit_table.leftside.ParaPathNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PlatformScope(PlatformScopeType.CLIENT)
public class ParaEditCache {
    @Nullable
    private static ParaEditCache cache;

    @Nonnull
    private final ParaPathNode rootNode;
    private boolean dirty = false;

    private ParaEditCache(@Nonnull ParaPathNode rootNode) {
        this.rootNode = rootNode;
    }

    public void markDirty() {
        this.dirty = true;
    }

    @Nullable
    public static ParaEditCache getCache() {
        return cache;
    }

    @Nonnull
    public ParaPathNode getRootNode() {
        return rootNode;
    }

    public static boolean hasCache() {
        return cache != null;
    }

    @Nonnull
    public static ParaEditCache createCache(ParaPathNode rootNode) {
        cache = new ParaEditCache(rootNode);
        return cache;
    }
}
