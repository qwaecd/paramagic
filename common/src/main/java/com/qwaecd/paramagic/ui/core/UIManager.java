package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.hit.UIHitResult;
import com.qwaecd.paramagic.ui.io.MouseEvent;
import com.qwaecd.paramagic.ui.io.MouseEventType;
import com.qwaecd.paramagic.ui.io.MouseStateMachine;
import lombok.Getter;

/**
 * 一个 screen 对应一个 UIManager 实例，管理整个 UI 树的布局和渲染
 */
public class UIManager {
    private final MouseStateMachine mouseStateMachine;

    @Getter
    public final UINode rootNode;

    public UIManager(UINode rootNode) {
        this.rootNode = rootNode;
        this.mouseStateMachine = new MouseStateMachine();
    }

    public void init() {
        this.rootNode.layout(this.rootNode.localRect.x, this.rootNode.localRect.y, this.rootNode.localRect.w, this.rootNode.localRect.h);
    }

    public void render(UIRenderContext context) {
        this.rootNode.renderTree(context);
    }

    /**
     * 处理鼠标事件
     * @param event 鼠标事件
     * @return 事件是否被框架消费
     */
    public boolean handleMouseEvent(MouseEvent event) {
        this.mouseStateMachine.updateState(event);
        UIHitResult hitResult = this.rootNode.createHitPath(
                (float) event.mouseX, (float) event.mouseY, UIHitResult.createEmpty()
        );
        if (event.type == MouseEventType.CLICK) {
            System.out.println("命中" + hitResult.getHitPath().size() + "个节点");
            while (!hitResult.isEmpty()) {
                UINode uiNode = hitResult.getHitPath().pop();
                System.out.println("Hit Node: " + uiNode.worldRect.x + ", " + uiNode.worldRect.y + ", " + uiNode.worldRect.w + ", " + uiNode.worldRect.h);
            }
        }
        return true;
    }
}
