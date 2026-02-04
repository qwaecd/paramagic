package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.listener.PhaseBucket;
import com.qwaecd.paramagic.ui.event.UIEvent;
import com.qwaecd.paramagic.ui.event.api.AllUIEvents;
import com.qwaecd.paramagic.ui.event.api.UIEventContext;
import com.qwaecd.paramagic.ui.event.api.UIEventKey;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.event.impl.WheelEvent;
import com.qwaecd.paramagic.ui.event.listener.UIEventListener;
import com.qwaecd.paramagic.ui.event.listener.UIEventListenerEntry;
import com.qwaecd.paramagic.ui.hit.UIHitResult;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UINode {
    private final List<UINode> children;
    @Nullable
    private Map<UIEventKey<?>, PhaseBucket> eventListeners;
    @Nullable
    private UINode parent;
    private boolean visible;
    private boolean hitTestable = true;
    @Setter
    @Getter
    @Nonnull
    private ClipMod clipMod;

    @Setter
    @Getter
    @Nonnull
    private SizeMode sizeMode;
    
    /**
     * 相对父节点的位置和尺寸
     */
    @Getter
    @Nonnull
    public final Rect localRect;

    /**
     * 缓存的屏幕绝对矩形（layout 后写入）
     */
    @Getter
    @Nonnull
    public final Rect worldRect;

    protected boolean showDebugOutLine = false;
    @Nonnull
    protected UIColor backgroundColor = UIColor.TRANSPARENT;

    public UINode() {
        this.children = new ArrayList<>();
        this.parent = null;
        this.visible = true;
        this.clipMod = ClipMod.NONE;
        this.sizeMode = SizeMode.FIXED;
        this.localRect = new Rect();
        this.worldRect = new Rect();
    }

    public UINode(@Nonnull UINode parent) {
        this.children = new ArrayList<>();
        this.parent = parent;
        this.visible = true;
        this.clipMod = ClipMod.NONE;
        this.sizeMode = SizeMode.FIXED;
        this.localRect = new Rect();
        this.worldRect = new Rect();

        parent.addChild(this);
    }


    public final <E extends UIEvent> UIEventListenerEntry<E> addListener(
            UIEventKey<E> key,
            EventPhase phase,
            int priority,
            UIEventListener<E> listener
    ) {
        if (this.eventListeners == null) {
            this.eventListeners = new HashMap<>();
        }

        PhaseBucket bucket = this.eventListeners.computeIfAbsent(key, k -> new PhaseBucket());
        return bucket.addListener(phase, priority, listener);
    }

    public final void removeListener(UIEventKey<?> key, UIEventListenerEntry<?> entry) {
        if (this.eventListeners == null) {
            return;
        }

        PhaseBucket bucket = this.eventListeners.get(key);
        if (bucket != null) {
            bucket.removeListener(entry);
        }
    }

    public void addChild(UINode child) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }

        this.children.add(child);
        child.parent = this;
    }

    public void removeChild(UINode child) {
        if (this.children.remove(child)) {
            child.parent = null;
        }
    }

    /**
     * 当此节点为事件目标时，处理鼠标点击事件
     */
    protected void onMouseClick(UIEventContext<MouseClick> context) {
    }

    /**
     * 当此节点为事件目标时，处理鼠标释放事件
     */
    protected void onMouseRelease(UIEventContext<MouseRelease> context) {
    }

    /**
     * 当此节点为事件目标时，处理鼠标双击事件
     */
    protected void onDoubleClick(UIEventContext<DoubleClick> context) {
    }

    /**
     * 当此节点为事件目标时，处理鼠标滚轮事件
     */
    protected void onMouseScroll(UIEventContext<WheelEvent> context) {
    }

    @SuppressWarnings("unchecked")
    final void dispatchTargetEvent(UIEventContext<? extends UIEvent> context) {
        UIEventKey<?> key = context.getEventKey();
        if (key == AllUIEvents.MOUSE_CLICK) {
            this.onMouseClick((UIEventContext<MouseClick>) context);
        } else if (key == AllUIEvents.MOUSE_RELEASE) {
            this.onMouseRelease((UIEventContext<MouseRelease>) context);
        } else if (key == AllUIEvents.MOUSE_DOUBLE_CLICK) {
            this.onDoubleClick((UIEventContext<DoubleClick>) context);
        } else if (key == AllUIEvents.WHEEL) {
            this.onMouseScroll((UIEventContext<WheelEvent>) context);
        }
    }

    /**
     * 将事件派发给注册的监听器执行.
     * @param context 本次事件的 context 实例.
     * @param phase 当前事件派发阶段.
     */
    public final <E extends UIEvent> void handleEvent(UIEventContext<E> context, EventPhase phase) {
        if (this.eventListeners == null) {
            return;
        }

        PhaseBucket bucket = this.eventListeners.get(context.getEventKey());
        if (bucket == null) {
            return;
        }

        bucket.dispatch(context, phase);
    }

    /**
     * 当节点被 manager captured 时, 每次鼠标移动都会调用此方法
     */
    public void onMouseMove(double mouseX, double mouseY) {
    }

    /**
     * 获取完整的命中路径, 类似于函数调用栈, 栈顶是最深层的节点
     */
    @Nonnull
    public UIHitResult createHitPath(float mouseX, float mouseY, @Nonnull UIHitResult parentHitResult) {
        // 先命中的在栈底
        if (this.hitTest(mouseX, mouseY)) {
            parentHitResult.pushNode(this);
        }

        // 深度越大的节点越接近栈顶, 同一层下, index 越大的节点越接近栈顶
        for (int i = this.children.size() - 1; i >= 0; --i) {
            int sizeBefore = parentHitResult.size();
            this.children.get(i).createHitPath(mouseX, mouseY, parentHitResult);
            if (parentHitResult.size() > sizeBefore) {
                break;
            }
        }
        return parentHitResult;
    }

    /**
     * 获取命中的节点，优先返回最上层的子节点
     * @return 命中的节点，未命中返回 null
     */
    @Nullable
    public UINode getHitNode(float mouseX, float mouseY) {
        for (int i = this.children.size() - 1; i >= 0 ; --i) {
            UINode hitNode = this.children.get(i).getHitNode(mouseX, mouseY);
            if (hitNode != null) {
                return hitNode;
            }
        }

        if (!this.hitTest(mouseX, mouseY)) {
            return null;
        }

        return this;
    }

    /**
     * 计算此节点及其子节点的屏幕绝对坐标
     * @param parentX 父节点的屏幕X坐标
     * @param parentY 父节点的屏幕Y坐标
     * @param parentW 父节点的宽度
     * @param parentH 父节点的高度
     */
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        this.worldRect.x = parentX + this.localRect.x;
        this.worldRect.y = parentY + this.localRect.y;
        
        this.computeSize(parentW, parentH);

        // 布局可以不考虑同层级先后顺序
        for (UINode child : this.children) {
            child.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
        }
    }

    private void computeSize(float parentW, float parentH) {
        switch (this.sizeMode) {
            case FIXED -> {
                this.worldRect.w = this.localRect.w;
                this.worldRect.h = this.localRect.h;
            }
            case FILL -> {
                this.worldRect.w = parentW;
                this.worldRect.h = parentH;
            }
            case FILL_WIDTH -> {
                this.worldRect.w = parentW;
                this.worldRect.h = this.localRect.h;
            }
            case FILL_HEIGHT -> {
                this.worldRect.w = this.localRect.w;
                this.worldRect.h = parentH;
            }
            default -> throw new IllegalStateException("Unexpected value: " + this.sizeMode);
        }
    }

    /**
     * 渲染节点自身, 不应该渲染其子节点.<br>
     * 子类应该重写该方法来实现自定义渲染内容.
     */
    public void render(@Nonnull UIRenderContext context) {
        if (!this.visible) {
            return;
        }
        context.drawQuad(this.worldRect, this.backgroundColor);
        if (this.showDebugOutLine) {
            context.renderOutline(this.worldRect, UIColor.RED);
        }
    }

    /**
     * 渲染节点及其子节点的树
     */
    public void renderTree(UIRenderContext context) {
        boolean hasClip = (this.clipMod == ClipMod.RECT);
        if (hasClip) {
            context.pushClipRect(this.worldRect);
        }

        if (this.isVisible()) {
            this.render(context);
        }

        this.forEachChild(node -> node.renderTree(context));

        if (hasClip) {
            context.popClipRect();
        }
    }

    public boolean hitTest(float x, float y) {
        if (!this.hitTestable) {
            return false;
        }
        return x >= this.worldRect.x && x < this.worldRect.x + this.worldRect.w
            && y >= this.worldRect.y && y < this.worldRect.y + this.worldRect.h;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isHitTestable() {
        return this.hitTestable;
    }

    public void setHitTestable(boolean hitTestable) {
        this.hitTestable = hitTestable;
    }

    public void enableHitTest() {
        this.hitTestable = true;
    }

    public void disableHitTest() {
        this.hitTestable = false;
    }

    public List<UINode> getChildren() {
        return this.children;
    }

    @Nullable
    public UINode getParent() {
        return this.parent;
    }

    public void setShowDebugOutLine(boolean showDebugOutLine) {
        this.showDebugOutLine = showDebugOutLine;
    }

    public boolean isShowDebugOutLine() {
        return this.showDebugOutLine;
    }

    @Nonnull
    public UIColor getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(@Nonnull UIColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private void forEachChild(@Nonnull Consumer<UINode> action) {
        for (UINode child : this.children) {
            action.accept(child);
        }
    }

    private void forEachChildInReverseOrder(@Nonnull Consumer<UINode> action) {
        for (int i = this.children.size() - 1; i >= 0; --i) {
            action.accept(this.children.get(i));
        }
    }
}
