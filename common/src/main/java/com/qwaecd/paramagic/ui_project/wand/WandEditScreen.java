package com.qwaecd.paramagic.ui_project.wand;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.screen.MCScreen;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.widget.UIButton;
import com.qwaecd.paramagic.ui.widget.UILabel;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class WandEditScreen extends MCScreen {
    private static final Component TITLE = Component.literal("Wand Edit");

    @Nonnull
    private final InventoryHolder playerInv;
    private final UINode rootNode;
    private final WandEditUI editUI;

    public static final float WIDTH = 510.0f;
    public static final float HEIGHT = 300.0f;

    public WandEditScreen(@Nonnull InventoryHolder playerInv) {
        super(TITLE);
        this.rootNode = new UINode();
        this.playerInv = playerInv;

        this.editUI = new WandEditUI(playerInv);
        this.manager = new UIManager(rootNode, super.createTooltipRenderer(), null, this.nativeWidgetHost);
        this.rootNode.addChild(this.editUI);
        if (Paramagic.isDevEnv()) {
            this.addDebugButton(this.rootNode);
        }
    }

    @Override
    protected void init() {
        final float windowW = UIManager.getWindowWidth() / UIManager.getGuiScale();
        final float windowH = UIManager.getWindowHeight() / UIManager.getGuiScale();
        float x = (windowW - WIDTH) / 2.0f;
        float y = (windowH - HEIGHT) / 2.0f;
        this.editUI.setLayoutRect(x, y, WIDTH, HEIGHT);
        super.init();
    }

    private void addDebugButton(UINode rootNode) {
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
        rootNode.addChild(button);
    }
}
