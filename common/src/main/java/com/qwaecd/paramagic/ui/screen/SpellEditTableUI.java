package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.Rect;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.api.AllUIEvents;
import com.qwaecd.paramagic.ui.event.api.UIEventContext;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.listener.UIEventListener;
import com.qwaecd.paramagic.ui.item.MouseCaptureNode;
import com.qwaecd.paramagic.ui.widget.UIButton;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.UIWindow;

import javax.annotation.Nonnull;

public class SpellEditTableUI extends UINode {
    private final UIColor color = new UIColor(UIColor.fromRGBA(50, 50, 50, 80));
    public SpellEditTableUI() {
        super();
        {
            UINode uiNode = new MouseCaptureNode();
            uiNode.localRect.set(50, 30, 100, 80);
            uiNode.setBackgroundColor(UIColor.BLUE);
            {
                UINode subNode = new UINode();
                subNode.localRect.set(150, 80, 50, 30);
                subNode.setBackgroundColor(UIColor.WHITE);
                subNode.getLayoutParams().center();
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
            button.getLayoutParams().center();
            this.addChild(button);
        }
        {
            UIWindow window = new UIWindow(new Rect(50, 40, 150, 100), UILabel.pangram);
            {
                UIButton button = new UIButton(new Rect(0, 0, 20, 20));
                button.getLayoutParams().center();
                UILabel buttonLabel = new UILabel("click me!");
                buttonLabel.getLayoutParams().center();
                button.addChild(buttonLabel);
                button.addListener(
                        AllUIEvents.MOUSE_CLICK,
                        EventPhase.CAPTURING,
                        new UIEventListener<>() {
                            private int clickCount = 0;
                            @Override
                            public void handleEvent(UIEventContext<MouseClick> context) {
                                clickCount++;
                                if (clickCount % 4 == 0) {
                                    button.getLayoutParams().top();
                                }
                                if (clickCount % 4 == 1) {
                                    button.getLayoutParams().botton();
                                }
                                if (clickCount % 4 == 2) {
                                    button.getLayoutParams().left();
                                }
                                if (clickCount % 4 == 3) {
                                    button.getLayoutParams().right();
                                }
                                button.layout(window.worldRect.x , window.worldRect.y, window.worldRect.w, window.worldRect.h);
                            }
                        }
                );
                window.addChild(button);
            }
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
        this.setToFullScreen();
        super.layout(this.localRect.x, this.localRect.y, this.localRect.w, this.localRect.h);
    }

    @Override
    public void render(@Nonnull UIRenderContext context) {
        context.drawQuad(this.worldRect, color);
    }
}
