package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.UIColor;

public interface UIRenderBackend {
    void pushClipRect(Rect rect);
    void popClipRect();
    void drawQuad(Rect rect, UIColor color);
}
