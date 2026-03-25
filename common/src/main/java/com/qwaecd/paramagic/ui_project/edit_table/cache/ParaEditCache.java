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
    private static long nextCacheToken = 1L;

    @Nonnull
    private final ParaPathNode rootNode;
    private final long cacheToken;
    private boolean dirty = false;
    private boolean submitted = false;
    private int modificationVersion = 0;
    private int pendingSubmissionVersion = -1;

    private ParaEditCache(@Nonnull ParaPathNode rootNode, long cacheToken) {
        this.rootNode = rootNode;
        this.cacheToken = cacheToken;
    }

    public void markDirty() {
        this.dirty = true;
        this.submitted = false;
        this.modificationVersion++;
        this.pendingSubmissionVersion = -1;
    }

    public void markSubmitted() {
        this.dirty = false;
        this.submitted = true;
        this.pendingSubmissionVersion = -1;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public boolean isSubmitted() {
        return this.submitted;
    }

    public boolean isCurrentVersionPendingSubmission() {
        return this.pendingSubmissionVersion == this.modificationVersion && this.pendingSubmissionVersion >= 0;
    }

    @Nonnull
    public SubmissionMarker beginSubmission() {
        this.pendingSubmissionVersion = this.modificationVersion;
        return new SubmissionMarker(this.cacheToken, this.modificationVersion);
    }

    public boolean matchesSubmission(long cacheToken, int modificationVersion) {
        return this.cacheToken == cacheToken && this.modificationVersion == modificationVersion;
    }

    private void clearPendingSubmissionIfCurrent(long cacheToken, int modificationVersion) {
        if (this.matchesSubmission(cacheToken, modificationVersion)
                && this.pendingSubmissionVersion == modificationVersion) {
            this.pendingSubmissionVersion = -1;
        }
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
        cache = new ParaEditCache(realRootNode.deepCopy(), nextCacheToken++);
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
     * 清空当前缓存。通常在容器内容发生变化后调用，使缓存立即失效。
     */
    public static void clearCache() {
        if (cache != null) {
            Paramagic.LOG.debug("ParaEditCache cleared.");
        }
        cache = null;
    }

    public static void markSubmittedIfMatches(long cacheToken, int modificationVersion) {
        if (cache == null) {
            return;
        }
        if (!cache.matchesSubmission(cacheToken, modificationVersion)) {
            return;
        }
        if (cache.pendingSubmissionVersion != modificationVersion) {
            return;
        }
        cache.markSubmitted();
    }

    public static void clearPendingSubmissionIfMatches(long cacheToken, int modificationVersion) {
        if (cache == null) {
            return;
        }
        cache.clearPendingSubmissionIfCurrent(cacheToken, modificationVersion);
    }

    public record SubmissionMarker(long cacheToken, int modificationVersion) {}
}
