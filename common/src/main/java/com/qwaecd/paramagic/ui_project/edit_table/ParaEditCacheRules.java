package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaEditCache;

import javax.annotation.Nullable;

public final class ParaEditCacheRules {
    private ParaEditCacheRules() {}

    public static ParaEditCacheState resolveState(@Nullable ParaEditCache cache) {
        if (cache == null) {
            return ParaEditCacheState.NO_CACHE;
        }
        if (cache.isCurrentVersionPendingSubmission()) {
            return ParaEditCacheState.PENDING_CONFIRMATION;
        }
        if (cache.isSubmitted()) {
            return ParaEditCacheState.SUBMITTED;
        }
        return ParaEditCacheState.UNSUBMITTED;
    }

    public static boolean canCreateCache(boolean hasCacheSeedRoot, @Nullable ParaEditCache cache) {
        return hasCacheSeedRoot && cache == null;
    }

    public static boolean canRebuildCache(boolean hasCacheSeedRoot, @Nullable ParaEditCache cache) {
        return hasCacheSeedRoot && cache != null;
    }

    public static boolean canSubmit(@Nullable ParaEditCache cache) {
        return cache != null
                && cache.isDirty()
                && resolveState(cache) == ParaEditCacheState.UNSUBMITTED;
    }
}
