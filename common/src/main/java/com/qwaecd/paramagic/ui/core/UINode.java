package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.api.event.UIEventKey;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.UIEvent;
import com.qwaecd.paramagic.ui.event.impl.*;
import com.qwaecd.paramagic.ui.event.listener.PhaseBucket;
import com.qwaecd.paramagic.ui.event.listener.UIEventListener;
import com.qwaecd.paramagic.ui.event.listener.UIEventListenerEntry;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;
import com.qwaecd.paramagic.ui.util.LayoutParams;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.util.UILayout;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "unused", "BooleanMethodIsAlwaysInverted"})
public class UINode {
    private static final Logger LOGGER = LoggerFactory.getLogger(UINode.class);
    protected final List<UINode> children;
    @Nullable
    protected Map<UIEventKey<?>, PhaseBucket> eventListeners;
    @Nullable
    protected UINode parent;
    protected boolean visible;
    protected boolean hitTestable = true;
    @Setter
    @Getter
    @Nonnull
    protected ClipMod clipMod;

    @Setter
    @Getter
    @Nonnull
    protected SizeMode sizeMode;

    @Getter
    @Nonnull
    protected final LayoutParams layoutParams;
    
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
        this.layoutParams = new LayoutParams();
        this.localRect = new Rect();
        this.worldRect = new Rect();
    }

    public UINode(@Nonnull UINode parent) {
        this.children = new ArrayList<>();
        this.parent = parent;
        this.visible = true;
        this.clipMod = ClipMod.NONE;
        this.sizeMode = SizeMode.FIXED;
        this.layoutParams = new LayoutParams();
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

    public final <E extends UIEvent> UIEventListenerEntry<E> addListener(
            UIEventKey<E> key,
            EventPhase phase,
            UIEventListener<E> listener
    ) {
        return this.addListener(key, phase, 0, listener);
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

    public void addChild(Collection<UINode> child) {
        for (UINode node : child) {
            this.addChild(child);
        }
    }

    public void removeChild(UINode child) {
        if (this.children.remove(child)) {
            child.parent = null;
        }
    }

    public void removeChild(Collection<UINode> child) {
        for (UINode node : child) {
            this.removeChild(child);
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

    /**
     * 在鼠标移入节点时调用, 鼠标在本节点内移动不会重复调用此方法.
     */
    protected void onMouseOver(UIEventContext<MouseOver> context) {
    }

    /**
     * 在鼠标移出节点时调用.
     */
    protected void onMouseLeave(UIEventContext<MouseLeave> context) {
    }


    @SuppressWarnings("unchecked")
    final void dispatchTargetEvent(UIEventContext<? extends UIEvent> context) {
        UIEventKey<?> key = context.getEventKey();
        switch (key.eventId) {
            case 0 -> this.onMouseClick((UIEventContext<MouseClick>) context);
            case 1 -> this.onMouseRelease((UIEventContext<MouseRelease>) context);
            case 2 -> this.onDoubleClick((UIEventContext<DoubleClick>) context);
            case 3 -> this.onMouseScroll((UIEventContext<WheelEvent>) context);
            case 4 -> this.onMouseOver((UIEventContext<MouseOver>) context);
            case 5 -> this.onMouseLeave((UIEventContext<MouseLeave>) context);
            default -> LOGGER.warn("No target event handler for event ID {}", key.eventId);
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
    public void onMouseMove(double mouseX, double mouseY, MouseStateMachine mouseState) {
    }

    @Nullable
    public UINode getTopmostHitNode(float mouseX, float mouseY) {
        if (this.clipMod == ClipMod.RECT && !this.hitTest(mouseX, mouseY)) {
            return null;
        }

        for (int i = this.children.size() - 1; i >= 0; --i) {
            UINode hitNode = this.children.get(i).getTopmostHitNode(mouseX, mouseY);
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
     * 获取命中的节点，优先返回最上层的子节点
     * @return 命中的节点，未命中返回 null
     */
    @Nullable
    public UINode getHitNode(float mouseX, float mouseY) {
        // 如果 Code clipMod != ClipMod.RECT, 那么后续的 hitTest 也不会执行了
        if (this.clipMod == ClipMod.RECT && !this.hitTest(mouseX, mouseY)) {
            return null;
        }

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
     * 判断鼠标坐标是否在当前元素的范围内, 该函数用于每帧进行 mouseover 判定, 需要使用时间复杂度较低的算法.<br>
     *
     * 当元素不可见时, 该操作不应该成功.
     * @see #hitTest(float mouseX, float mouseY)
     */
    public boolean contains(float mouseX, float mouseY) {
        return this.visible && this.hitTest(mouseX, mouseY);
    }

    /**
     * 递归获取鼠标当前位置下的最上层的元素, 由于每次鼠标移动都有可能进行一次判定, 需要使用时间复杂度较低的算法.
     * @return 鼠标位置下最上层的节点, 为 null 则表示不存在.
     */
    @Nullable
    public UINode getMouseOverNode(float mouseX, float mouseY) {
        // 如果 Code clipMod != ClipMod.RECT, 那么后续的 contains 也不会执行了
        if (this.clipMod == ClipMod.RECT && !this.contains(mouseX, mouseY)) {
            return null;
        }

        for (int i = this.children.size() - 1; i >= 0 ; --i) {
            UINode hitNode = this.children.get(i).getMouseOverNode(mouseX, mouseY);
            if (hitNode != null) {
                return hitNode;
            }
        }

        if (!this.contains(mouseX, mouseY)) {
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
        UILayout.layout(this.localRect, this.worldRect, this.layoutParams, this.sizeMode, parentX, parentY, parentW, parentH);

        // 布局可以不考虑同层级先后顺序
        for (UINode child : this.children) {
            child.layout(this.worldRect.x, this.worldRect.y, this.worldRect.w, this.worldRect.h);
        }
    }

    /**
     * 渲染节点自身, 不应该渲染其子节点.<br>
     * 子类应该重写该方法来实现自定义渲染内容.
     */
    protected void render(@Nonnull UIRenderContext context) {
        if (!this.visible) {
            return;
        }
        context.drawQuad(this.worldRect, this.backgroundColor);
    }

    /**
     * 在调试模式下调用该函数渲染调试信息.
     */
    public void renderDebug(@Nonnull UIRenderContext context) {
        context.renderOutline(this.worldRect, UIColor.RED);
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

        if (this.showDebugOutLine) {
            this.renderDebug(context);
        }

        for (UINode child : this.children) {
            child.renderTree(context);
        }

        if (hasClip) {
            context.popClipRect();
        }
    }

    /**
     * 检查鼠标是否可以命中该元素, 元素不可见的情况下也可能命中.
     * @see #contains(float mouseX, float mouseY)
     */
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

    protected void forEachChild(@Nonnull Consumer<UINode> action) {
        for (UINode child : this.children) {
            action.accept(child);
        }
    }

    public void forEachNodeSafe(@Nonnull Consumer<UINode> action) {
        try {
            action.accept(this);
        } catch (Exception e) {
            LOGGER.error("Exception occurred when processing UINode.", e);
        }
        for (UINode child : this.children) {
            try {
                child.forEachNodeSafe(action);
            } catch (Exception e) {
                LOGGER.error("Exception occurred when processing child UINode.", e);
            }
        }
    }

    protected void forEachChildInReverseOrder(@Nonnull Consumer<UINode> action) {
        for (int i = this.children.size() - 1; i >= 0; --i) {
            action.accept(this.children.get(i));
        }
    }

    protected float getWindowWidth() {
        return Minecraft.getInstance().getWindow().getWidth();
    }

    protected float getWindowHeight() {
        return Minecraft.getInstance().getWindow().getHeight();
    }

    protected float getGuiScale() {
        return (float) Minecraft.getInstance().getWindow().getGuiScale();
    }

    protected void setToFullScreen() {
        this.localRect.set(
                0.0f, 0.0f,
                this.getWindowWidth() / this.getGuiScale(), this.getWindowHeight() / this.getGuiScale()
        );
    }
}
