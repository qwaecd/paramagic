package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.Rect;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.api.AllUIEvents;
import com.qwaecd.paramagic.ui.item.MouseCaptureNode;
import com.qwaecd.paramagic.ui.widget.UIButton;
import com.qwaecd.paramagic.ui.widget.UIWindow;

import javax.annotation.Nonnull;

public class SpellEditTableUI extends UINode {
    private final UIColor color = new UIColor(UIColor.fromRGBA(255, 0, 0, 100));
    public SpellEditTableUI() {
        super();
        {
            UINode uiNode = new MouseCaptureNode();
            uiNode.localRect.set(50, 30, 100, 80);
            uiNode.setBackgroundColor(UIColor.BLUE);
            {
                UINode subNode = new MouseCaptureNode();
                subNode.localRect.set(150, 80, 90, 80);
                subNode.setBackgroundColor(UIColor.WHITE);
                uiNode.addChild(subNode);
            }
            this.addChild(uiNode);
        }
        {
            UIButton button = new UIButton(new Rect(100, 40, 100, 40));
            button.addListener(
                    AllUIEvents.MOUSE_CLICK,
                    EventPhase.CAPTURING,
                    (context) -> {
                        context.getManager().forEachUINode(node -> node.setShowDebugOutLine(!node.isShowDebugOutLine()));
                        context.consume();
                    }
            );
            button.addListener(
                    AllUIEvents.MOUSE_DOUBLE_CLICK,
                    EventPhase.CAPTURING,
                    (context) -> {
                        context.getManager().forEachUINode(node -> node.setShowDebugOutLine(!node.isShowDebugOutLine()));
                        context.consume();
                    }
            );
            this.addChild(button);
        }
        {
            UIWindow window = new UIWindow(new Rect(50, 40, 150, 100), "Test Window");
            this.addChild(window);
        }
        {
            UINode uiNode = new MouseCaptureNode();
            uiNode.localRect.set(10, 20, 80, 60);
            uiNode.setBackgroundColor(UIColor.BLACK);
            this.addChild(uiNode);
        }
        {
            UINode uiNode = new MouseCaptureNode();
            uiNode.localRect.set(70, 30, 200, 60);
            uiNode.setBackgroundColor(UIColor.GREEN);
            this.addChild(uiNode);
        }
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        this.localRect.set(50.0f, 30.0f, 200.0f, 150.0f);
        super.layout(this.localRect.x, this.localRect.y, this.localRect.w, this.localRect.h);
    }

    @Override
    public void render(@Nonnull UIRenderContext context) {
        context.drawQuad(this.worldRect, color);
    }
}
