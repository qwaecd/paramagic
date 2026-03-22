package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.ui.MenuContent;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.core.TaskStage;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UITask;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.inventory.ContainerHolder;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import com.qwaecd.paramagic.ui.menu.SpellEditTableMenu;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.widget.UIButton;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.node.SlotNode;
import com.qwaecd.paramagic.ui_project.edit_table.leftside.ParaSelectBar;
import com.qwaecd.paramagic.ui_project.edit_table.leftside.SideBar;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SpellEditTableUI extends UINode {
    private UIManager manager;

    private TableContainerNode tableContainerNode;
    private final UIModeButtonGroup buttonGroup;
    private final EditWindow editWindow;
    private final SideBar sideBar;
    private final ParaSelectBar paraSelectBar;
    private final ParaCrystalSelectBar crystalSelectBar;

    @Nonnull
    private BarState barState = BarState.NULL;

    public SpellEditTableUI() {
        super();
        this.editWindow = new EditWindow();
        this.sideBar = new SideBar();
        this.paraSelectBar = new ParaSelectBar();
        this.crystalSelectBar = new ParaCrystalSelectBar();
        this.buttonGroup = new UIModeButtonGroup(this.sideBar, this, this.editWindow.localRect.w);
        this.addChild(this.editWindow);
        this.addChild(this.buttonGroup);
        this.addChild(this.crystalSelectBar);

        if (Paramagic.isDevEnv()) {
            this.addDebugButton();
        }
    }

    public void init(UIManager manager) {
        if (this.manager != null) {
            throw new IllegalStateException("Manager has already been set");
        }
        this.manager = manager;
        MenuContent menu = manager.getMenuContent();
        InventoryHolder inventory = Objects.requireNonNull(menu, "menu couldn't be null.").getPlayerInventory();
        int inventorySize = this.crystalSelectBar.initInventory(inventory);

        ContainerHolder tableContainer = ((SpellEditTableMenu) menu.getMenu()).getContainer();
        tableContainer.registerListener(this::onContainerChanged);
        UISlot containerUISlot = new UISlot(tableContainer, 0, inventorySize + tableContainer.size() - 1);
        SlotNode containerSlot = new SlotNode(containerUISlot);
        this.tableContainerNode = new TableContainerNode(containerSlot);
        this.addChild(tableContainerNode);

        this.sideBar.initContainer(this, tableContainer, containerUISlot);
    }

    private void onContainerChanged(InventoryHolder container, UISlot slot) {
        this.editWindow.onContainerChanged(this, container, slot);
        this.sideBar.onContainerChanged(this, container, slot);
    }

    public TableContainerNode getContainerNode() {
        return this.tableContainerNode;
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        this.setToFullScreen();
        super.layout(this.localRect.x, this.localRect.y, this.localRect.w, this.localRect.h);
        this.tableContainerNode.localRect.setXY(
                editWindow.localRect.x + editWindow.localRect.w + 16.0f,
                editWindow.localRect.y
        );
        this.buttonGroup.localRect.setXY(
                editWindow.localRect.x,
                editWindow.localRect.y - buttonGroup.localRect.h - 8.0f
        );

        this.tableContainerNode.layout(this.localRect.x, this.localRect.y, this.localRect.w, this.localRect.h);
        this.buttonGroup.layout(this.localRect.x, this.localRect.y, this.localRect.w, this.localRect.h);
    }

    @Override
    public void render(@Nonnull UIRenderContext context) {
        super.render(context);
    }

    public void changeBarState(BarState state) {
        if (state == this.barState) {
            return;
        }
        if (state == BarState.PARA_SELECT) {
            this.sideBar.changeToParaSelectBar();
            this.removeChild(this.sideBar);
            if (!this.containsChild(this.paraSelectBar)) {
                this.addChild(this.paraSelectBar);
            }
            this.editWindow.setTreeEditActive(true);
        }
        if (state == BarState.CRYSTAL_EDIT) {
            this.sideBar.changeToCrystalEdit();
            if (!this.containsChild(this.sideBar)) {
                this.addChild(this.sideBar);
            }
            this.removeChild(this.paraSelectBar);
            this.editWindow.setTreeEditActive(false);
        }
        if (state == BarState.NULL) {
            this.sideBar.changeToNull();
            this.removeChild(this.sideBar);
            this.removeChild(this.paraSelectBar);
            this.editWindow.setTreeEditActive(false);
        }
        this.barState = state;
        this.deferredLayout();
    }

    private void deferredLayout() {
        this.manager.offerDeferredTask(
                UITask.create(
                        manager -> this.layout(this.localRect.x, this.localRect.y, this.localRect.w, this.localRect.h),
                        TaskStage.AFTER_EVENT
                )
        );
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
