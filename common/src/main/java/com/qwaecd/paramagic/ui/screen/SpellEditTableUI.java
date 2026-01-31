package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import org.jetbrains.annotations.NotNull;

public class SpellEditTableUI extends UINode {
    public SpellEditTableUI() {
        super();
        {
            UINode uiNode = new UINode();
            uiNode.setShowDebugOutLine(true);
            uiNode.localRect.set(50, 30, 100, 80);
            uiNode.setBackgroundColor(UIColor.BLUE);
            this.addChild(uiNode);
        }
        {
            UINode uiNode = new UINode();
            uiNode.setShowDebugOutLine(true);
            uiNode.localRect.set(10, 20, 80, 60);
            uiNode.setBackgroundColor(UIColor.BLACK);
            this.addChild(uiNode);
        }
        {
            UINode uiNode = new UINode();
            uiNode.setShowDebugOutLine(true);
            uiNode.localRect.set(70, 30, 200, 60);
            uiNode.setBackgroundColor(UIColor.GREEN);
            this.addChild(uiNode);
        }
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        this.localRect.set(50.0f, 30.0f, 200.0f, 150.0f);
        this.showDebugOutLine = true;
        super.layout(this.localRect.x, this.localRect.y, this.localRect.w, this.localRect.h);
    }

    @Override
    public void render(@NotNull UIRenderContext context) {
        context.drawQuad(this.worldRect, new UIColor(UIColor.fromRGBA(255, 0, 0, 100)));
        if (this.showDebugOutLine) {
            context.renderOutline(this.worldRect, UIColor.RED);
        }
    }
}
