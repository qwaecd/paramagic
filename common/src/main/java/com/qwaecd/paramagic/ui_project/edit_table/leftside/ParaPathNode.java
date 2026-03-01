package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.*;
import com.qwaecd.paramagic.ui.io.mouse.MouseButton;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui_project.edit_table.EditContextMenu;
import com.qwaecd.paramagic.ui_project.edit_table.EditTableSprite;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaStruct;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaPathNode extends UINode {
    private static final UIColor hoveringTextColor = UIColor.of(255, 231, 136, 255);
    private static final EditTableSprite arrowRight = new EditTableSprite(
            64, 0,
            8, 8,
            0, 0
    );
    private static final EditTableSprite arrowDown = new EditTableSprite(
            72, 0,
            8, 8,
            0, 0
    );

    protected boolean selected = false;

    private static final float childrenIndentation = 8.0f;
    private static final float defaultNodeHeight = 8.0f;

    private boolean folded = false;

    private boolean mouseOvering = false;

    @Nullable
    private Component path;

    private final ParaStruct struct = new ParaStruct();

    ParaPathNode(@Nullable Component path) {
        this.path = path;
    }

    public ParaPathNode(@Nonnull ParaComponentData data) {
        this.path = Component.literal(data.getComponentId());
        this.struct.updateFromParaComponent(data);
    }

    public void setName(String name) {
        this.struct.setName(name);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void removeName() {
        this.struct.setName(null);
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
        if (folded) {
            // 折叠递归禁用子节点
            for (UINode child : this.children) {
                if (child instanceof ParaPathNode paraPathNode) {
                    paraPathNode.setFolded(true);
                    paraPathNode.disable();
                }
            }
        } else {
            // 展开只展开直接子节点
            for (UINode child : this.children) {
                if (child instanceof ParaPathNode paraPathNode) {
                    paraPathNode.enable();
                }
            }
        }
    }

    public void setParaPath(Component path) {
        this.path = path;
    }

    private float getNodeHeight() {
        if (this.path != null) {
            Font font = Minecraft.getInstance().font;
            return font.lineHeight;
        }
        return defaultNodeHeight;
    }

    public float getSubtreeHeight() {
        float totalHeight = this.getNodeHeight();
        if (this.folded) {
            return totalHeight;
        }
        for (UINode child : this.children) {
            if (child instanceof ParaPathNode paraPathNode) {
                totalHeight += paraPathNode.getSubtreeHeight();
            } else {
                totalHeight += child.localRect.h;
            }
        }
        return totalHeight;
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        context.consume();
    }

    @Override
    protected void onMouseRelease(UIEventContext<MouseRelease> context) {
        MouseRelease event = context.event;
        if (event.button == MouseButton.RIGHT.code) {
            this.createRightClickMenu(context.manager, (int) event.mouseX, (int) event.mouseY);
        }
    }

    private void createRightClickMenu(UIManager manager, float mouseX, float mouseY) {
        EditContextMenu menu = new EditContextMenu(mouseX, mouseY);
        manager.createContextMenu(menu);
    }

    @Override
    protected void onDoubleClick(UIEventContext<DoubleClick> context) {
        this.setFolded(!this.folded);
        context.consume();
    }

    @Override
    protected void onMouseOver(UIEventContext<MouseOver> context) {
        this.mouseOvering = true;
    }

    @Override
    protected void onMouseLeave(UIEventContext<MouseLeave> context) {
        this.mouseOvering = false;
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        float nodeWidth = this.parent == null ? parentW : parent.localRect.w;
        float nodeHeight = defaultNodeHeight;
        if (this.path != null) {
            Font font = Minecraft.getInstance().font;
            nodeHeight = font.lineHeight;
        }
        this.localRect.setWH(nodeWidth, nodeHeight);

        if (this.parent == null) {
            this.localRect.setXY(0.0f, 0.0f);
        } else {
            this.localRect.setXY(childrenIndentation, this.localRect.y);
        }

        float nextChildY = nodeHeight;
        for (UINode child : this.children) {
            if (this.folded) {
                child.disable();
                continue;
            }
            child.localRect.setXY(childrenIndentation, nextChildY);
            if (child instanceof ParaPathNode paraPathNode) {
                nextChildY += paraPathNode.getSubtreeHeight();
            } else {
                nextChildY += child.localRect.h;
            }
        }
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        if (!this.isVisible()) {
            return;
        }
        if (this.path != null) {
            final float offsetX = 2.0f;
            final float offsetY = 1.0f;
            int lineHeight = context.getLineHeight();
            if (this.localRect.w != lineHeight + offsetY * 2.0f) {
                this.localRect.w = lineHeight + offsetY * 2.0f;
            }
            UIColor textColor;
            if (this.selected) {
                textColor = UIColor.GREEN;
            } else if (this.mouseOvering) {
                textColor = hoveringTextColor;
            } else {
                textColor = UIColor.WHITE;
            }
            context.drawText(this.path, this.worldRect.x + offsetX, this.worldRect.y + offsetY, textColor);
        }
        if (this.children.isEmpty()) {
            return;
        }

        final float arrowX = this.worldRect.x - 8.0f;
        if (this.folded) {
            context.renderSprite(arrowRight, arrowX, this.worldRect.y);
        } else {
            context.renderSprite(arrowDown, arrowX, this.worldRect.y);
        }
    }
}
