package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaEditCache;

import javax.annotation.Nonnull;

public final class ParaEditSubmitController {
    private ParaEditSubmitController() {}

    public static boolean canSubmit(@Nonnull ParaEditCache cache) {
        return ParaEditCacheRules.canSubmit(cache);
    }

    public static void submit(@Nonnull ParaEditCache cache) {
        Paramagic.LOG.warn("The retired edit-table UI no longer has a network submission path.");
    }
}
