package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.ui_project.edit_table.leftside.ParaPathNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PlatformScope(PlatformScopeType.CLIENT)
public final class ParaEditCache {
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

    public boolean isDirty() {
        return this.dirty;
    }

    @Nullable
    public static ParaEditCache getCache() {
        return cache;
    }

    @Nonnull
    public ParaPathNode getRootNode() {
        return this.rootNode;
    }

    public static boolean hasCache() {
        return cache != null;
    }

    /**
     * 以给定的真实数据根节点创建缓存，会对根节点进行深拷贝，确保缓存与真实数据不共享任何可变对象引用。
     */
    @Nonnull
    public static ParaEditCache createCache(@Nonnull ParaPathNode realRootNode) {
        cache = new ParaEditCache(realRootNode.deepCopy());
        return cache;
    }

    /**
     * 确保缓存存在。如果缓存不存在，则从提供的真实数据根节点深拷贝创建缓存。
     */
    @Nonnull
    public static ParaEditCache ensureCache(@Nonnull ParaPathNode realRootNode) {
        if (cache == null) {
            return createCache(realRootNode);
        }
        return cache;
    }

    /**
     * 清空缓存。提交成功或构建 ParaData 失败时调用。
     */
    public static void clearCache() {
        if (cache != null) {
            Paramagic.LOG.debug("ParaEditCache cleared.");
        }
        cache = null;
    }
}
