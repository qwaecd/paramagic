package com.qwaecd.paramagic.ui_project.wand;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.SizeMode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.widget.UIButton;
import com.qwaecd.paramagic.ui.widget.UILabel;

import javax.annotation.Nonnull;

public class SpellEditTableUI extends UINode {
    private UIManager manager;

    public SpellEditTableUI() {
        super();
        this.setSizeMode(SizeMode.FILL);

        if (Paramagic.isDevEnv()) {
            this.addDebugButton();
        }
    }

    public void init(UIManager manager) {
        if (this.manager != null) {
            throw new IllegalStateException("Manager has already been set");
        }
        this.manager = manager;
    }

    @Override
    public void render(@Nonnull UIRenderContext context) {
        super.render(context);
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
                    context.getManager().forEachUINode(node -> node.setDebugMod(!node.isDebugMod()));
                    context.consume();
                }
        );
        button.addListener(
                AllUIEvents.MOUSE_DOUBLE_CLICK,
                EventPhase.CAPTURING,
                (context) -> {
                    context.getManager().forEachUINode(node -> node.setDebugMod(!node.isDebugMod()));
                    context.consume();
                }
        );
        button.getLayoutParams().botton();
        this.addChild(button);
    }
}
