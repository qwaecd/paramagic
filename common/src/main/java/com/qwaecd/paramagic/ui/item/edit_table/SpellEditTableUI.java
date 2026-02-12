package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.UIButton;
import com.qwaecd.paramagic.ui.widget.UILabel;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SpellEditTableUI extends UINode {
    private UIManager manager;

    private final EditWindow editWindow;
    private final ParaSelectBar paraSelectBar;
    private final ParaCrystalSelectBar crystalSelectBar;
    private final UIColor color = UIColor.TRANSPARENT;

    public SpellEditTableUI() {
        super();
        this.editWindow = new EditWindow();
        this.paraSelectBar = new ParaSelectBar();
        this.crystalSelectBar = new ParaCrystalSelectBar();
        this.addChild(this.editWindow);
        this.addChild(this.paraSelectBar);
        this.addChild(this.crystalSelectBar);

        this.addDebugButton();
    }

    public void setManager(UIManager manager) {
        if (this.manager != null) {
            throw new IllegalStateException("Manager has already been set");
        }
        this.manager = manager;
        InventoryHolder inventory = Objects.requireNonNull(manager.getMenuContent(), "menu couldn't be null.").getPlayerInventory();
        this.crystalSelectBar.setInventory(inventory);
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
