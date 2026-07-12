package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolator;
import com.qwaecd.paramagic.ui.animation.UIAnimator;
import com.qwaecd.paramagic.ui.animation.ValueSetter;
import com.qwaecd.paramagic.ui.animation.fast.FloatUIAnimator;
import com.qwaecd.paramagic.ui.api.TooltipContent;
import com.qwaecd.paramagic.ui.api.TooltipQuery;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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
    @Nullable
    private UIManager manager;
    /**
     * 控制节点是否可见，当为 false 时，节点会截断整棵子树：不渲染、不命中、不参与 mouse over
     */
    protected boolean visible;

    /**
     * 控制本节点是否可以被命中，只表示当前节点自身不能成为事件目标，不阻断可见子节点命中
     */
    protected boolean hitTestable = true;

    @Getter
    @Nonnull
    protected ClipMod clipMod;

    @Getter
    @Nonnull
    protected SizeMode sizeMode;

    @Getter
    @Nonnull
    protected final LayoutParams layoutParams;
    
    /**
     * 布局输入矩形：描述节点在父级布局空间中的期望位置和尺寸。
     */
    @Getter
    @Nonnull
    public final Rect layoutRect;

    /**
     * 布局输出矩形：由 arrange/layout 计算出的最终屏幕空间矩形。
     */
    @Getter
    @Nonnull
    public final Rect finalRect;

    /**
     * 用于渲染内容的矩形，渲染节点各项内容的时候应当使用该矩形来进行渲染，以兼容动画
     */
    @Nonnull
    public final Rect presentationRect;

    protected boolean debugMod = false;
    @Nonnull
    protected UIColor backgroundColor = UIColor.TRANSPARENT;
    protected boolean layoutDirty = true;
    protected boolean measureDirty = true;
    @Getter
    @Nonnull
    protected MeasureResult measureResult = MeasureResult.ZERO;
    @Getter
    protected float measuredWidth = 0.0f;
    @Getter
    protected float measuredHeight = 0.0f;

    public UINode() {
        this.children = new ArrayList<>();
        this.parent = null;
        this.manager = null;
        this.visible = true;
        this.clipMod = ClipMod.NONE;
        this.sizeMode = SizeMode.FIXED;
        this.layoutParams = new LayoutParams();
        this.layoutRect = new Rect();
        this.finalRect = new Rect();
        this.presentationRect = new Rect(this.finalRect);
        this.layoutParams.setChangeListener(this::requestLayout);
    }

    public UINode(@Nonnull UINode parent) {
        this.children = new ArrayList<>();
        this.parent = parent;
        this.manager = null;
        this.visible = true;
        this.clipMod = ClipMod.NONE;
        this.sizeMode = SizeMode.FIXED;
        this.layoutParams = new LayoutParams();
        this.layoutRect = new Rect();
        this.finalRect = new Rect();
        this.presentationRect = new Rect(this.finalRect);
        this.layoutParams.setChangeListener(this::requestLayout);

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

    public boolean containsChild(UINode node) {
        return this.children.contains(node);
    }

    public void addChild(UINode child) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }

        this.children.add(child);
        child.parent = this;
        if (this.manager != null) {
            child.attachToManager(this.manager);
        }
        this.requestMeasure();
    }

    public void addChild(Collection<UINode> child) {
        for (UINode node : child) {
            this.addChild(node);
        }
    }

    public void removeChild(UINode child) {
        if (this.children.remove(child)) {
            if (this.manager != null) {
                child.detachFromManager();
            }
            child.parent = null;
            this.requestMeasure();
        }
    }

    public void removeChild(Collection<UINode> child) {
        for (UINode node : child) {
            this.removeChild(node);
        }
    }

    public void requestLayout() {
        this.layoutDirty = true;
        if (this.parent != null) {
            this.parent.requestLayout();
            return;
        }
        if (this.manager != null) {
            this.manager.requestLayout();
        }
    }

    public void requestMeasure() {
        this.measureDirty = true;
        this.layoutDirty = true;
        if (this.parent != null) {
            this.parent.requestMeasure();
            return;
        }
        if (this.manager != null) {
            this.manager.requestLayout();
        }
    }

    public boolean isLayoutDirty() {
        return this.layoutDirty;
    }

    public boolean isMeasureDirty() {
        return this.measureDirty;
    }

    protected void markLayoutClean() {
        this.layoutDirty = false;
        this.measureDirty = false;
    }

    public void setClipMod(@Nonnull ClipMod clipMod) {
        this.clipMod = clipMod;
        this.requestLayout();
    }

    public void setSizeMode(@Nonnull SizeMode sizeMode) {
        this.sizeMode = sizeMode;
        this.requestMeasure();
    }

    public void setLayoutRect(float x, float y, float w, float h) {
        this.layoutRect.set(x, y, w, h);
        this.requestMeasure();
    }

    public void setLayoutRect(@Nonnull Rect rect) {
        this.layoutRect.set(rect);
        this.requestMeasure();
    }

    public void setLayoutPosition(float x, float y) {
        this.layoutRect.setXY(x, y);
        this.requestLayout();
    }

    public void setLayoutSize(float w, float h) {
        this.layoutRect.setWH(w, h);
        this.requestMeasure();
    }

    protected <T> UIAnimator<T> animate(
            UIAnimator<T> animator
    ) {
        if (this.manager != null) {
            this.manager.addAnimator(this, animator);
        }
        return animator;
    }

    protected <T> UIAnimator<T> animate(
            @Nonnull String key,
            UIAnimator<T> animator
    ) {
        if (this.manager != null) {
            this.manager.addAnimator(this, key, animator);
        }
        return animator;
    }

    protected <T> UIAnimator<T> animate(
            T start,
            T end,
            float duration,
            Interpolator<T> interpolator,
            ValueSetter<T> setter
    ) {
        UIAnimator<T> animator = new UIAnimator<>(start, end, duration, interpolator, setter);
        this.animate(animator);
        return animator;
    }

    protected <T> UIAnimator<T> animate(
            @Nonnull String key,
            T start,
            T end,
            float duration,
            Interpolator<T> interpolator,
            ValueSetter<T> setter
    ) {
        UIAnimator<T> animator = new UIAnimator<>(start, end, duration, interpolator, setter);
        this.animate(key, animator);
        return animator;
    }

    protected <T> UIAnimator<T> replaceAnimation(
            @Nonnull String key,
            UIAnimator<T> animator
    ) {
        if (this.manager != null) {
            this.manager.replaceAnimator(this, key, animator);
        }
        return animator;
    }

    protected <T> UIAnimator<T> replaceAnimation(
            @Nonnull String key,
            T start,
            T end,
            float duration,
            Interpolator<T> interpolator,
            ValueSetter<T> setter
    ) {
        UIAnimator<T> animator = new UIAnimator<>(start, end, duration, interpolator, setter);
        this.replaceAnimation(key, animator);
        return animator;
    }

    protected FloatUIAnimator replaceFloatAnimation(
            @Nonnull String key,
            FloatUIAnimator animator
    ) {
        if (this.manager != null) {
            this.manager.replaceAnimator(this, key, animator);
        }
        return animator;
    }

    protected FloatUIAnimator animateFloat(
            float start,
            float end,
            float duration,
            FloatUIAnimator.FloatInterpolator interpolator,
            FloatUIAnimator.FloatValueSetter setter
    ) {
        FloatUIAnimator animator = new FloatUIAnimator(start, end, duration, interpolator, setter);
        if (this.manager != null) {
            this.manager.addAnimator(this, animator);
        }
        return animator;
    }

    protected FloatUIAnimator animateFloat(
            float start,
            float end,
            float duration,
            EasingFunction easingFunction,
            FloatUIAnimator.FloatInterpolator interpolator,
            FloatUIAnimator.FloatValueSetter setter
    ) {
        FloatUIAnimator animator = new FloatUIAnimator(start, end, duration, easingFunction, interpolator, setter);
        if (this.manager != null) {
            this.manager.addAnimator(this, animator);
        }
        return animator;
    }

    protected FloatUIAnimator animateFloat(
            @Nonnull String key,
            float start,
            float end,
            float duration,
            EasingFunction easingFunction,
            FloatUIAnimator.FloatInterpolator interpolator,
            FloatUIAnimator.FloatValueSetter setter
    ) {
        FloatUIAnimator animator = new FloatUIAnimator(start, end, duration, easingFunction, interpolator, setter);
        if (this.manager != null) {
            this.manager.addAnimator(this, key, animator);
        }
        return animator;
    }

    protected FloatUIAnimator animateFloat(
            @Nonnull String key,
            float start,
            float end,
            float duration,
            FloatUIAnimator.FloatInterpolator interpolator,
            FloatUIAnimator.FloatValueSetter setter
    ) {
        FloatUIAnimator animator = new FloatUIAnimator(start, end, duration, interpolator, setter);
        if (this.manager != null) {
            this.manager.addAnimator(this, key, animator);
        }
        return animator;
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

    protected void onAttached(@Nonnull UIManager manager) {
    }

    protected void onDetached(@Nonnull UIManager manager) {
    }

    /**
     * 获取当前鼠标位置与节点状态下的 tooltip 内容。返回 {@code null} 时，UIManager 会继续查询父节点；
     * 返回 {@link TooltipContent#EMPTY} 时，会阻止父节点 tooltip 的回溯但不绘制内容。
     */
    @Nullable
    public TooltipContent getTooltip(@Nonnull TooltipQuery query) {
        if (this.isDebugMod()) {
            return TooltipContent.of(
                    Component.literal("Debug Node: " + this.getClass().getSimpleName()),
                    Component.literal("Layout Rect: " + this.layoutRect),
                    Component.literal("Final Rect: " + this.finalRect),
                    Component.literal("Presentation Rect: " + this.presentationRect),
                    Component.literal("Measured Size: (" + this.measuredWidth + ", " + this.measuredHeight + ")")
            );
        }
        return null;
    }

    @Nullable
    public static TooltipContent getTooltipFromItem(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        if (itemStack.isEmpty()) {
            return TooltipContent.EMPTY;
        }
        Minecraft minecraft = Minecraft.getInstance();
        return new TooltipContent(Screen.getTooltipFromItem(minecraft, itemStack), itemStack.getTooltipImage());
    }


    @SuppressWarnings("unchecked")
    protected final void dispatchTargetEvent(UIEventContext<? extends UIEvent> context) {
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

    /**
     * 当在 UIManager 里注册了需要时刻监听鼠标移动时，每次鼠标移动都会调用此方法.
     */
    public void mouseMoveListener(double mouseX, double mouseY, MouseStateMachine mouseState) {
    }

    @Nullable
    public UINode getTopmostHitNode(float mouseX, float mouseY) {
        if (!this.visible) {
            return null;
        }
        // 如果 clipMod == ClipMod.RECT, 那么裁切区域之外的子树不会继续命中。
        if (this.clipMod == ClipMod.RECT && !this.isPointInside(mouseX, mouseY)) {
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
     * 递归获取鼠标当前位置下的最上层的元素, 由于每次鼠标移动都有可能进行一次判定, 需要使用时间复杂度较低的算法.
     * @return 鼠标位置下最上层的节点, 为 null 则表示不存在.
     */
    @Nullable
    public UINode getMouseOverNode(float mouseX, float mouseY) {
        if (!this.visible) {
            return null;
        }
        // 如果 clipMod == ClipMod.RECT, 那么裁切区域之外的子树不会继续 mouseover。
        if (this.clipMod == ClipMod.RECT && !this.isPointInside(mouseX, mouseY)) {
            return null;
        }

        for (int i = this.children.size() - 1; i >= 0 ; --i) {
            UINode hitNode = this.children.get(i).getMouseOverNode(mouseX, mouseY);
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
     * 计算当前节点自己的最终屏幕矩形，不处理子节点。
     */
    protected void arrangeSelf(float parentX, float parentY, float parentW, float parentH) {
        UILayout.layout(this.layoutRect, this.finalRect, this.layoutParams, this.sizeMode, parentX, parentY, parentW, parentH);
        this.presentationRect.set(this.finalRect);
    }

    /**
     * 根据当前节点的最终矩形摆放子节点。默认行为为绝对布局。
     */
    protected void arrangeChildren() {
        // 布局可以不考虑同层级先后顺序
        for (UINode child : this.children) {
            child.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
        }
    }

    /**
     * 计算节点在父级约束下的自然尺寸。
     */
    public MeasureResult measure(LayoutConstraints constraints) {
        MeasureResult result = this.measureSelf(constraints);
        this.setMeasureResult(result);
        this.measureChildren(constraints);
        this.measureDirty = false;
        return this.measureResult;
    }

    /**
     * 计算当前节点自己的自然尺寸，不处理子节点。
     */
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        float width = UILayout.resolveWidth(this.sizeMode, this.layoutRect, constraints.getMaxWidth());
        float height = UILayout.resolveHeight(this.sizeMode, this.layoutRect, constraints.getMaxHeight());
        return MeasureResult.of(width, height);
    }

    /**
     * 根据当前节点测量结果继续测量子节点。默认行为保持旧的父尺寸传递语义。
     */
    protected void measureChildren(@Nonnull LayoutConstraints constraints) {
        LayoutConstraints childConstraints = LayoutConstraints.loose(this.measuredWidth, this.measuredHeight);
        for (UINode child : this.children) {
            child.measure(childConstraints);
        }
    }

    protected void setMeasureResult(@Nonnull MeasureResult result) {
        this.measureResult = result;
        this.measuredWidth = result.getWidth();
        this.measuredHeight = result.getHeight();
        this.layoutRect.setWH(this.measuredWidth, this.measuredHeight);
    }

    /**
     * 将节点摆放到父级坐标系中。
     */
    public void arrange(float parentX, float parentY, float parentW, float parentH) {
        this.arrangeSelf(parentX, parentY, parentW, parentH);
        this.arrangeChildren();
        this.markLayoutClean();
    }

    /**
     * 渲染节点自身, 不应该渲染其子节点.<br>
     * 子类应该重写该方法来实现自定义渲染内容.
     */
    protected void render(@Nonnull UIRenderContext context) {
        if (!this.visible) {
            return;
        }
        context.fillRect(this.finalRect, this.backgroundColor);
    }

    /**
     * 在进行裁切之前渲染背景内容，渲染的内容不会被本节点的裁切所影响，但是会受父节点影响.
     */
    protected void renderBackGround(UIRenderContext context) {
    }

    /**
     * 在调试模式下调用该函数渲染调试信息.
     */
    public void renderDebug(@Nonnull UIRenderContext context) {
        context.renderOutline(this.finalRect, UIColor.RED);
    }

    /**
     * 渲染节点及其子节点的树
     */
    public void renderTree(UIRenderContext context) {
        if (!this.visible) {
            return;
        }

        this.renderBackGround(context);
        boolean hasClip = (this.clipMod == ClipMod.RECT);
        if (hasClip) {
            context.pushClipRect(this.getClipRect());
        }

        this.render(context);

        if (this.debugMod) {
            this.renderDebug(context);
        }

        this.renderChildrenTree(context);

        if (hasClip) {
            context.popClipRect();
        }
    }

    protected void renderChildrenTree(UIRenderContext context) {
        for (UINode child : this.children) {
            child.renderTree(context);
        }
    }

    @Nonnull
    protected Rect getClipRect() {
        return this.finalRect;
    }

    @Nonnull
    protected Rect getpresentationRect() {
        return this.presentationRect;
    }

    /**
     * 检查鼠标是否可以命中该元素自身。
     *
     * <p>该方法只判断当前节点是否能作为事件目标。父节点用于裁切子树时应使用
     * {@link #isPointInside(float, float)}，避免 {@code hitTestable=false}
     * 的父节点阻断子节点命中。
     */
    public boolean hitTest(float x, float y) {
        if (!this.visible || !this.hitTestable) {
            return false;
        }
        return this.isPointInside(x, y);
    }

    /**
     * 只检查坐标是否落在当前节点裁切矩形内，不考虑 visible / hitTestable。
     */
    protected boolean isPointInside(float x, float y) {
        return x >= this.getClipRect().x && x < this.getClipRect().x + this.getClipRect().w
            && y >= this.getClipRect().y && y < this.getClipRect().y + this.getClipRect().h;
    }

    public void enable() {
        this.visible = true;
        this.hitTestable = true;
        this.requestLayout();
    }

    public void disable() {
        this.visible = false;
        this.hitTestable = false;
        this.requestLayout();
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        this.requestLayout();
    }

    public boolean isHitTestable() {
        return this.hitTestable;
    }

    public void setHitTestable(boolean hitTestable) {
        this.hitTestable = hitTestable;
        this.requestLayout();
    }

    public void enableHitTest() {
        this.hitTestable = true;
        this.requestLayout();
    }

    public void disableHitTest() {
        this.hitTestable = false;
        this.requestLayout();
    }

    public List<UINode> getChildren() {
        return this.children;
    }

    @Nullable
    public UINode getParent() {
        return this.parent;
    }

    @Nullable
    public UIManager getManager() {
        return this.manager;
    }

    public boolean isAttached() {
        return this.manager != null;
    }

    public boolean isSameAsOrDescendantOf(@Nullable UINode node) {
        UINode current = this;
        while (current != null) {
            if (current == node) {
                return true;
            }
            current = current.parent;
        }
        return false;
    }

    public boolean containsInSubtree(@Nullable UINode node) {
        return node != null && node.isSameAsOrDescendantOf(this);
    }

    public void setDebugMod(boolean debugMod) {
        this.debugMod = debugMod;
    }

    public boolean isDebugMod() {
        return this.debugMod;
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

    final void attachToManager(@Nonnull UIManager manager) {
        if (this.manager == manager) {
            return;
        }
        if (this.manager != null) {
            throw new IllegalStateException("This node is already attached to another UIManager.");
        }

        this.manager = manager;
        this.onAttached(manager);
        for (UINode child : List.copyOf(this.children)) {
            child.attachToManager(manager);
        }
        this.afterChildAttachedToManager();
    }

    protected void afterChildAttachedToManager() {
    }

    final void detachFromManager() {
        if (this.manager == null) {
            return;
        }

        UIManager currentManager = this.manager;
        for (UINode child : List.copyOf(this.children)) {
            child.detachFromManager();
        }
        currentManager.onNodeDetached(this);
        this.onDetached(currentManager);
        this.manager = null;
    }

    protected void setToFullScreen() {
        this.setLayoutRect(
                0.0f, 0.0f,
                UIManager.getWindowWidth() / UIManager.getGuiScale(), UIManager.getWindowHeight() / UIManager.getGuiScale()
        );
    }
}
