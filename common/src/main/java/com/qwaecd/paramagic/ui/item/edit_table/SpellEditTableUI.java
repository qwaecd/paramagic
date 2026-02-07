package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.Rect;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.api.AllUIEvents;
import com.qwaecd.paramagic.ui.widget.UIButton;
import com.qwaecd.paramagic.ui.widget.UILabel;

import javax.annotation.Nonnull;

public class SpellEditTableUI extends UINode {
    private final UIColor color = UIColor.TRANSPARENT;
    public SpellEditTableUI() {
        super();
        this.addChild(new EditWindow());
        this.addDebugButton();
        this.addSelectBars();
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

    private void addSelectBars() {
        this.addChild(new ParaSelectBar());
        this.addChild(new ParaCrystalSelectBar());
    }

    private void addDebugButton() {
        UIButton button = new UIButton(new Rect(0, 0, 60, 20));
        UILabel label = new UILabel("outline");
        label.getLayoutParams().center();
        button.addChild(label);
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
        button.getLayoutParams().botton();
        this.addChild(button);
    }
}
