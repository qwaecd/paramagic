package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.ui.util.UINetwork;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaEditBuildResult;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaEditCache;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaEditCacheParaDataBuilder;

import javax.annotation.Nonnull;

public final class ParaEditSubmitController {
    private ParaEditSubmitController() {}

    public static boolean canSubmit(@Nonnull ParaEditCache cache) {
        return ParaEditCacheRules.canSubmit(cache);
    }

    public static void submit(@Nonnull ParaEditCache cache) {
        if (!canSubmit(cache)) {
            if (cache.isCurrentVersionPendingSubmission()) {
                Paramagic.LOG.debug("Submit ignored because the current cache version is still awaiting server confirmation.");
            }
            return;
        }

        ParaEditBuildResult result = new ParaEditCacheParaDataBuilder().buildFromCache(cache);
        if (!result.isSuccess() || result.getParaData() == null) {
            for (String warning : result.getWarnings()) {
                Paramagic.LOG.warn("Submit build failed: {}", warning);
            }
            return;
        }

        for (String warning : result.getWarnings()) {
            Paramagic.LOG.warn("Submit build warning: {}", warning);
        }

        ParaEditCache.SubmissionMarker marker = cache.beginSubmission();
        UINetwork.sendSubmitEditedParaData(result.getParaData(), marker);
    }
}
