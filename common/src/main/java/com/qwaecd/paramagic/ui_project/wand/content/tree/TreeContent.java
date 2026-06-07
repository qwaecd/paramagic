package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui_project.wand.SpellTreeEditClientState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public final class TreeContent extends UINode {
    private static final float DEFAULT_CONTENT_WIDTH = 420.0f;
    private static final float DEFAULT_CONTENT_HEIGHT = 320.0f;
    @Nonnull
    private final SpellTreeEditClientState editState;

    public TreeContent(@Nonnull SpellTreeEditClientState editState) {
        super();
        this.editState = editState;
    }

    public void onTreeDataRebuilt() {
        this.requestLayout();
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        return MeasureResult.of(DEFAULT_CONTENT_WIDTH, DEFAULT_CONTENT_HEIGHT);
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
        context.fillRect(this.finalRect, UIColor.WHITE);
    }
}
