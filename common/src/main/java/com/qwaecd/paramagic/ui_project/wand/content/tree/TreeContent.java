package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UINode;

public final class TreeContent extends UINode {
    private static final float DEFAULT_CONTENT_WIDTH = 420.0f;
    private static final float DEFAULT_CONTENT_HEIGHT = 320.0f;

    public TreeContent() {
        super();
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        return MeasureResult.of(DEFAULT_CONTENT_WIDTH, DEFAULT_CONTENT_HEIGHT);
    }
}
