package com.qwaecd.paramagic.ui_project.wand;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.network.packet.inventory.S2CSpellTreeEditRejectedPacket;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.menu.SpellEditMenu;
import com.qwaecd.paramagic.ui.screen.MCContainerScreen;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.widget.UIButton;
import com.qwaecd.paramagic.ui.widget.UILabel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class WandEditScreen extends MCContainerScreen<SpellEditMenu> {
    @Nonnull
    private final InventoryHolder playerInv;
    private final UINode rootNode;
    private final SpellTreeEditClientState editState;
    private final WandEditUI editUI;

    public static final float WIDTH = 510.0f;
    public static final float HEIGHT = 300.0f;

    public WandEditScreen(@Nonnull SpellEditMenu menu, @Nonnull Inventory inventory, @Nonnull Component title) {
        this(menu, inventory, title, menu.getPlayerInventory(), new UINode());
    }

    private WandEditScreen(
            @Nonnull SpellEditMenu menu,
            @Nonnull Inventory inventory,
            @Nonnull Component title,
            @Nonnull InventoryHolder playerInv,
            @Nonnull UINode rootNode
    ) {
        super(menu, inventory, title, rootNode);
        this.rootNode = rootNode;
        this.playerInv = playerInv;
        this.editState = new SpellTreeEditClientState(this.playerInv, menu::getCarried, menu.getEditEpoch());
        this.editUI = new WandEditUI(this.playerInv, this.editState);
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

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBackground(guiGraphics);
    }

    @Nonnull
    public SpellTreeEditClientState getEditState() {
        return this.editState;
    }

    public void handleSpellTreeEditRejected(@Nonnull S2CSpellTreeEditRejectedPacket packet) {
        if (this.editState.acceptRejectedEdit(packet.getEditEpoch(), packet.getTreeData())) {
            this.editUI.onTreeDataRebuilt();
        }
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
                    UIManager uiManager = context.getManager();
                    uiManager.setDebugMod(!uiManager.isDebugMod());
                    context.consume();
                }
        );
        button.addListener(
                AllUIEvents.MOUSE_DOUBLE_CLICK,
                EventPhase.CAPTURING,
                (context) -> {
                    UIManager uiManager = context.getManager();
                    uiManager.setDebugMod(!uiManager.isDebugMod());
                    context.consume();
                }
        );
        button.getLayoutParams().botton();
        rootNode.addChild(button);
    }
}
