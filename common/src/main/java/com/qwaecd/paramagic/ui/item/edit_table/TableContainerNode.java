package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.ui.MenuContent;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.inventory.UISlot;
import com.qwaecd.paramagic.ui.io.mouse.MouseButton;
import com.qwaecd.paramagic.ui.widget.node.SlotNode;
import net.minecraft.world.inventory.ClickType;

import javax.annotation.Nonnull;

public class TableContainerNode extends UINode {
    private final SlotNode slot;

    public TableContainerNode(@Nonnull SlotNode slot) {
        this.slot = slot;
        this.addChild(slot);
        this.localRect.setWH(slot.localRect.w, slot.localRect.h);


        this.addListener(
                AllUIEvents.MOUSE_CLICK,
                EventPhase.BUBBLING,
                this::handleItemNodeClick
        );
        this.addListener(
                AllUIEvents.MOUSE_DOUBLE_CLICK,
                EventPhase.BUBBLING,
                this::handleItemNodeDoubleClick
        );
        this.addListener(
                AllUIEvents.MOUSE_RELEASE,
                EventPhase.BUBBLING,
                this::handleItemNodeRelease
        );
    }

    private void handleItemNodeClick(UIEventContext<MouseClick> context) {
        UINode targetNode = context.targetNode;
        if (!(targetNode instanceof SlotNode node)) {
            return;
        }
        MenuContent menu = context.manager.getMenuContentOrThrow();
        UISlot slot = node.getSlot();
        menu.getScreen().slotClicked(slot, context.event.button, ClickType.PICKUP);
        context.consume();
    }

    private void handleItemNodeRelease(UIEventContext<MouseRelease> context) {
        context.consume();
    }

    private void handleItemNodeDoubleClick(UIEventContext<DoubleClick> context) {
        UINode targetNode = context.targetNode;
        if (!(targetNode instanceof SlotNode node)) {
            return;
        }
        MenuContent menu = context.manager.getMenuContentOrThrow();

        if (menu.getCarried().isEmpty()) {
            return;
        }

        if (context.event.button != MouseButton.LEFT.code) {
            this.handleItemNodeClick(UIEventContext.upcast(AllUIEvents.MOUSE_CLICK, context));
            return;
        }

        UISlot slot = node.getSlot();
        menu.getScreen().slotClicked(slot, context.event.button, ClickType.PICKUP_ALL);
        context.consume();
    }
}
