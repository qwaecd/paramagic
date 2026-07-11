package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.MenuContent;
import com.qwaecd.paramagic.ui.animation.BaseUIAnimator;
import com.qwaecd.paramagic.ui.animation.UIAnimationSystem;
import com.qwaecd.paramagic.ui.api.TooltipContent;
import com.qwaecd.paramagic.ui.api.TooltipQuery;
import com.qwaecd.paramagic.ui.api.TooltipRenderer;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.api.event.UIEventKey;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.UIEvent;
import com.qwaecd.paramagic.ui.event.impl.*;
import com.qwaecd.paramagic.ui.io.mouse.CursorType;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;
import com.qwaecd.paramagic.ui.nativewidget.NativeWidgetNode;
import com.qwaecd.paramagic.ui.overlay.OverlayRoot;
import com.qwaecd.paramagic.ui.screen.NativeWidgetHost;
import com.qwaecd.paramagic.ui.widget.ContextMenu;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * 一个 screen 对应一个 UIManager 实例，管理整个 UI 树的布局和渲染
 */
public class UIManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIManager.class);

    @Nullable
    private static UIManager instance;

    private boolean initialized = false;

    @Nonnull
    private final UIAnimationSystem animationSystem;

    @Nullable
    private final MenuContent menuContent;
    private final MouseStateMachine mouseStateMachine;

    private final Map<TaskStage, Queue<UITask>> deferredTasks = new EnumMap<>(TaskStage.class);
    @Getter
    @Nonnull
    private final TooltipRenderer tooltipRenderer;

    @Nullable
    private final NativeWidgetHost nativeWidgetHost;

    @Nullable
    private UINode capturedNode;
    @Nullable
    private ContextMenu contextMenu;

    private final Set<UINode> mouseMovingListeners = new HashSet<>();
    private final Set<UIKeyListener> keyListeners = new LinkedHashSet<>();

    @Getter
    public final UINode rootNode;

    @Nullable
    private UINode mouseOver = null;

    @Nullable
    public UINode getMouseOverNode() {
        return this.mouseOver;
    }

    @Getter
    private final OverlayRoot overlayRoot;
    private boolean layoutTaskScheduled = false;
    private boolean layoutTaskRunning = false;

    public UIManager(UINode rootNode, @Nonnull TooltipRenderer tooltipRenderer, @Nullable MenuContent menuContent) {
        this.animationSystem = new UIAnimationSystem();
        this.menuContent = menuContent;
        this.rootNode = rootNode;
        this.mouseStateMachine = new MouseStateMachine();
        this.tooltipRenderer = tooltipRenderer;
        this.nativeWidgetHost = null;
        this.overlayRoot = new OverlayRoot();
    }

    public UIManager(UINode rootNode, @Nonnull TooltipRenderer tooltipRenderer, @Nullable MenuContent menuContent, @Nullable NativeWidgetHost nativeWidgetHost) {
        this.animationSystem = new UIAnimationSystem();
        this.menuContent = menuContent;
        this.rootNode = rootNode;
        this.mouseStateMachine = new MouseStateMachine();
        this.tooltipRenderer = tooltipRenderer;
        this.nativeWidgetHost = nativeWidgetHost;
        this.overlayRoot = new OverlayRoot();
    }

    /**
     * 个人不是很建议使用这个函数，因为 UIManager 的生命周期无法随时保证是正确的
     */
    @Nullable
    public static UIManager getInstance() {
        return instance;
    }

    public void init() {
        instance = this;
        this.rootNode.setLayoutRect(
                0.0f, 0.0f,
                UIManager.getWindowWidth() / UIManager.getGuiScale(),
                UIManager.getWindowHeight() / UIManager.getGuiScale()
        );
        if (this.initialized) {
            this.onResize();
            return;
        }
        this.initialized = true;
        this.rootNode.attachToManager(this);
        this.layoutAll();
    }

    private void onResize() {
        this.layoutAll();
    }

    public void prepareRender(UIRenderContext context) {
        this.processDeferredTasks(TaskStage.BEFORE_RENDER);
        this.animationSystem.updateAll(context.getDeltaTime());
    }

    public void render(UIRenderContext context) {
        this.rootNode.renderTree(context);
        this.overlayRoot.renderOverlay(context);
        if (this.contextMenu != null) {
            this.contextMenu.renderTree(context);
        }
        this.renderTooltip(context);

        this.processDeferredTasks(TaskStage.AFTER_RENDER);
    }

    private void renderTooltip(@Nonnull UIRenderContext context) {
        TooltipQuery.Trigger trigger = this.capturedNode == null
                ? TooltipQuery.Trigger.HOVER
                : TooltipQuery.Trigger.CAPTURED;
        TooltipContent tooltip = this.resolveTooltip(new TooltipQuery(context.mouseX, context.mouseY, trigger));
        if (tooltip == null || tooltip.isEmpty()) {
            return;
        }
        context.renderTooltip(tooltip, context.mouseX, context.mouseY);
    }

    /**
     * 拖拽期间由 captured 节点决定 tooltip；其余时候使用当前 mouse over 节点。
     * 当前节点之外最多向上回溯三层父节点。
     */
    @Nullable
    private TooltipContent resolveTooltip(@Nonnull TooltipQuery query) {
        UINode node = this.capturedNode != null ? this.capturedNode : this.mouseOver;
        for (int depth = 0; node != null && depth <= 3; depth++) {
            TooltipContent tooltip = node.getTooltip(query);
            if (tooltip != null) {
                return tooltip;
            }
            node = node.getParent();
        }
        return null;
    }

    /**
     * 添加一个延迟任务
     */
    public void offerDeferredTask(@Nonnull UITask task) {
        this.deferredTasks.computeIfAbsent(task.taskStage, (k) -> new ArrayDeque<>()).offer(task);
    }

    public void requestLayout() {
        if (this.layoutTaskScheduled || this.layoutTaskRunning) {
            return;
        }
        this.layoutTaskScheduled = true;
        this.offerDeferredTask(UITask.create(UIManager::runScheduledLayout, TaskStage.BEFORE_RENDER));
    }

    private void runScheduledLayout() {
        this.layoutTaskScheduled = false;
        this.layoutTaskRunning = true;
        try {
            this.layoutAll();
        } finally {
            this.layoutTaskRunning = false;
        }
    }

    public void flushMouseOvering() {
        this.processMouseOverAndLeave(this.mouseStateMachine.mouseX(), this.mouseStateMachine.mouseY());
    }

    public void offerOveringTestTask() {
        this.offerOveringTestTask(false);
    }

    public void offerOveringTestTask(boolean now) {
        if (now) {
            this.processMouseOverAndLeave(this.mouseStateMachine.mouseX(), this.mouseStateMachine.mouseY());
        } else {
            this.offerDeferredTask(UITask.create(UIManager::flushMouseOvering, TaskStage.AFTER_RENDER));
        }
    }

    public void displayContextMenu(@Nonnull ContextMenu contextMenu) {
        this.cancelContextMenu();
        this.contextMenu = contextMenu;
        this.contextMenu.measure(LayoutConstraints.loose(this.rootNode.layoutRect.w, this.rootNode.layoutRect.h));
        this.contextMenu.arrange(this.rootNode.layoutRect.x, this.rootNode.layoutRect.y, this.rootNode.layoutRect.w, this.rootNode.layoutRect.h);
    }

    public void cancelContextMenu() {
        if (this.contextMenu != null) {
            this.contextMenu.cancel();
        }
        this.contextMenu = null;
    }

    /**
     * 处理鼠标点击事件
     * @return 事件是否被消费
     */
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        boolean accepted = this.mouseStateMachine.onMouseClick(mouseX, mouseY, button);
        if (!accepted) {
            return false;
        }

        if (!this.isNativeWidgetTarget(mouseX, mouseY)) {
            this.clearNativeWidgetFocus();
        }

        if (this.mouseStateMachine.isDoubleClick()) {
            DoubleClick event = new DoubleClick(mouseX, mouseY, button);
            return this.dispatchEvent(AllUIEvents.MOUSE_DOUBLE_CLICK, event, mouseX, mouseY);
        }

        MouseClick event = new MouseClick(mouseX, mouseY, button);
        return this.dispatchEvent(AllUIEvents.MOUSE_CLICK, event, mouseX, mouseY);
    }

    /**
     * 处理鼠标释放事件
     * @return 事件是否被消费
     */
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        boolean accepted = this.mouseStateMachine.onMouseRelease(mouseX, mouseY, button);
        if (!accepted) {
            return false;
        }

        MouseRelease event = new MouseRelease(mouseX, mouseY, button);
        return this.dispatchEvent(AllUIEvents.MOUSE_RELEASE, event, mouseX, mouseY);
    }

    /**
     * 处理鼠标滚轮事件
     * @return 事件是否被消费
     */
    public boolean onMouseScroll(double mouseX, double mouseY, double delta) {
        WheelEvent event = new WheelEvent(mouseX, mouseY, delta);
        return this.dispatchEvent(AllUIEvents.WHEEL, event, mouseX, mouseY);
    }

    /**
     * 处理鼠标移动事件
     */
    public void onMouseMove(double mouseX, double mouseY) {
        this.mouseStateMachine.onMouseMove(mouseX, mouseY);
        if (this.capturedNode != null) {
            this.capturedNode.onMouseMove(mouseX, mouseY, this.mouseStateMachine);
        } else {
            // 如果当前没有捕获的节点，那么进行一次 over/leave 判定
            this.processMouseOverAndLeave(mouseX, mouseY);
        }
        for (UINode listener : this.mouseMovingListeners) {
            listener.mouseMoveListener(mouseX, mouseY, this.mouseStateMachine);
        }
    }

    private void processMouseOverAndLeave(double mouseX, double mouseY) {
        UINode newOver = null;
        if (this.contextMenu != null) {
            newOver = this.contextMenu.getMouseOverNode((float) mouseX, (float) mouseY);
        }

        if (newOver == null) {
            newOver = this.rootNode.getMouseOverNode((float) mouseX, (float) mouseY);
        }

        if (this.mouseOver == null && newOver == null) {
            // 上一帧和当前帧都是 null
            return;
        }

        if (this.mouseOver == newOver) {
            // 在元素内移动
            return;
        }

        if (this.mouseOver != null) {
            this.mouseOver.onMouseLeave(
                    new UIEventContext<>(this, this.mouseOver, AllUIEvents.MOUSE_LEAVE, new MouseLeave(mouseX, mouseY))
            );
        }

        this.mouseOver = newOver;

        if (this.mouseOver != null) {
            this.mouseOver.onMouseOver(
                    new UIEventContext<>(this, this.mouseOver, AllUIEvents.MOUSE_OVER, new MouseOver(mouseX, mouseY))
            );
        }
    }

    /**
     * 捕获阶段 -> 目标阶段 -> 冒泡阶段
     * @param eventKey 事件类型
     * @param event 事件实例
     * @return 事件是否被消费
     */
    private <E extends UIEvent> boolean dispatchEvent(UIEventKey<E> eventKey, E event, double mouseX, double mouseY) {
        UIEventContext<E> context;
        if (this.capturedNode != null) {
            context = new UIEventContext<>(this, this.capturedNode, eventKey, event);
            // 不存局部变量会因为当前帧就立即释放而 NullPointerException 的喵~
            UINode captured = this.capturedNode;
            captured.handleEvent(context, EventPhase.CAPTURING);
            if (!context.isPropagationStopped()) {
                captured.dispatchTargetEvent(context);
            }
            if (!context.isPropagationStopped()) {
                captured.handleEvent(context, EventPhase.BUBBLING);
            }
        } else {
            UIHitResult hitResult = this.createHitPath(this.rootNode, mouseX, mouseY);
            context = new UIEventContext<>(this, hitResult.getTop(), eventKey, event);

            // 先发给 ContextMenu
            if (this.contextMenu != null) {
                if (this.contextMenu.hitTest((float) mouseX, (float) mouseY)) {
                    UIHitResult internalResult = this.createHitPath(this.contextMenu, mouseX, mouseY);
                    UIEventContext<E> internalEvent = new UIEventContext<>(this, internalResult.getTop(), eventKey, event);
                    List<UINode> menuResultPath = internalResult.getHitPath();
                    // 在 ContextMenu 内部的目标阶段
                    if (!menuResultPath.isEmpty() && !internalEvent.isPropagationStopped()) {
                        UINode target = menuResultPath.get(menuResultPath.size() - 1);
                        target.dispatchTargetEvent(internalEvent);
                    }
                    // 在 ContextMenu 内部的冒泡阶段
                    for (int i = menuResultPath.size() - 1; i >= 0 && !internalEvent.isPropagationStopped(); i--) {
                        menuResultPath.get(i).handleEvent(internalEvent, EventPhase.BUBBLING);
                    }
                    if (internalEvent.isConsumed()) {
                        context.consume();
                    }
                    if (internalEvent.isPropagationStopped()) {
                        context.stopPropagation();
                    }
                } else {
                    this.cancelContextMenu();
                }
            }

            List<UINode> hitPath = hitResult.getHitPath();

            // 捕获阶段：从根到目标, 包括目标（索引 0 是根，索引越大越深）
            for (int i = 0; i < hitPath.size() && !context.isPropagationStopped(); i++) {
                hitPath.get(i).handleEvent(context, EventPhase.CAPTURING);
            }

            // 目标阶段
            if (!hitPath.isEmpty() && !context.isPropagationStopped()) {
                UINode target = hitPath.get(hitPath.size() - 1);
                target.dispatchTargetEvent(context);
            }

            // 冒泡阶段：从目标到根, 包括目标
            for (int i = hitPath.size() - 1; i >= 0 && !context.isPropagationStopped(); i--) {
                hitPath.get(i).handleEvent(context, EventPhase.BUBBLING);
            }
        }

        this.processDeferredTasks(TaskStage.AFTER_EVENT);

        return context.isConsumed();
    }

    /**
     * 获取完整的命中路径, 类似于函数调用栈, 列表末尾(栈顶)是最深层的节点<br>
     * 所有路径上的节点都会被加入到命中路径中, 包括没有经过命中测试的节点.
     */
    @Nonnull
    public UIHitResult createHitPath(UINode beginNode, double mouseX, double mouseY) {
        UINode topmost = beginNode.getTopmostHitNode((float) mouseX, (float) mouseY);
        UIHitResult hitResult = UIHitResult.createEmpty();

        if (topmost != null) {
            hitResult.pushNode(topmost);
            UINode node = topmost.getParent();
            while (node != null) {
                hitResult.pushNode(node);
                node = node.getParent();
            }
        }

        // 相信标准库, 不要尝试自己写算法 :)
        // 反转列表, 使得索引越大越深
        hitResult.reverse();
        return hitResult;
    }

    public void captureNode(@Nullable UINode node) {
        this.capturedNode = node;
    }

    public void releaseCapture() {
        this.capturedNode = null;
    }

    public void registerMouseMovingListener(UINode node) {
        this.mouseMovingListeners.add(node);
    }

    public void unregisterMouseMovingListener(UINode node) {
        this.mouseMovingListeners.remove(node);
    }

    public void registerKeyListener(@Nonnull UIKeyListener listener) {
        this.keyListeners.add(listener);
    }

    public void unregisterKeyListener(@Nonnull UIKeyListener listener) {
        this.keyListeners.remove(listener);
    }

    /**
     * 依注册顺序分发键盘按下事件，首个消费事件的监听器终止分发。
     */
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        for (UIKeyListener listener : List.copyOf(this.keyListeners)) {
            if (listener.onKeyPressed(this, keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 遍历所有 UI 节点, 包括根节点, 请不要在该 lambda 内修改节点树的结构.
     * @throws ConcurrentModificationException 如果在遍历过程中修改了节点树结构.
     */
    public void forEachUINode(Consumer<UINode> action) {
        try {
            this.rootNode.forEachNodeSafe(action);
        } catch (ConcurrentModificationException cme) {
            LOGGER.error("Concurrent modification detected when traversing UI nodes. Make sure not to modify the UI tree structure within the action.", cme);
        } catch (Exception e) {
            LOGGER.error("Exception occurred when traversing UI nodes.", e);
        }
    }

    /**
     * 获取 menu, 如果是 null 说明是纯 screen UI
     */
    @Nullable
    public MenuContent getMenuContent() {
        return this.menuContent;
    }

    @Nonnull
    public MenuContent getMenuContentOrThrow() {
        if (this.menuContent == null) {
            throw new NullPointerException("MenuContent is null. This UIManager is not associated with a MenuContent.");
        }
        return this.menuContent;
    }

    public void onClose() {
        this.rootNode.detachFromManager();
        this.keyListeners.clear();
        if (instance == this) {
            instance = null;
        }
        this.animationSystem.close();
        CursorType.setCursor(CursorType.ARROW);
    }

    public static boolean hasShiftKeyDown() {
        return Screen.hasShiftDown();
    }

    public static boolean hasCtrlKeyDown() {
        return Screen.hasControlDown();
    }

    public static boolean hasAltKeyDown() {
        return Screen.hasAltDown();
    }

    private void processDeferredTasks(TaskStage stage) {
        Queue<UITask> tasks = this.deferredTasks.get(stage);
        if (tasks != null) {
            while (tasks.peek() != null) {
                UITask task = tasks.poll();
                try {
                    task.execute(this);
                } catch (Exception e) {
                    LOGGER.error("Exception occurred while executing deferred UI task.", e);
                }
            }
        }
    }

    public void layoutAll() {
        this.layoutTaskScheduled = false;
        LayoutConstraints rootConstraints = LayoutConstraints.loose(this.rootNode.layoutRect.w, this.rootNode.layoutRect.h);
        this.rootNode.measure(rootConstraints);
        this.rootNode.arrange(this.rootNode.layoutRect.x, this.rootNode.layoutRect.y, this.rootNode.layoutRect.w, this.rootNode.layoutRect.h);
        if (this.contextMenu != null) {
            this.contextMenu.measure(rootConstraints);
            this.contextMenu.arrange(this.rootNode.layoutRect.x, this.rootNode.layoutRect.y, this.rootNode.layoutRect.w, this.rootNode.layoutRect.h);
        }
        this.syncNativeWidgets();
    }

    public void bindNativeWidget(@Nonnull NativeWidgetNode<?, ?> node) {
        if (this.nativeWidgetHost == null) {
            LOGGER.warn("Attempted to bind a native widget node, but this UIManager does not have a NativeWidgetHost.");
            return;
        }
        this.nativeWidgetHost.bind(node);
    }

    public void unbindNativeWidget(@Nonnull NativeWidgetNode<?, ?> node) {
        if (this.nativeWidgetHost == null) {
            return;
        }
        this.nativeWidgetHost.unbind(node);
    }

    public void syncNativeWidget(@Nonnull NativeWidgetNode<?, ?> node) {
        if (this.nativeWidgetHost == null) {
            return;
        }
        this.nativeWidgetHost.sync(node);
    }

    public void syncNativeWidgets() {
        if (this.nativeWidgetHost == null) {
            return;
        }
        this.nativeWidgetHost.syncAll();
    }

    public void focusNativeWidget(@Nonnull NativeWidgetNode<?, ?> node) {
        if (this.nativeWidgetHost == null) {
            return;
        }
        this.nativeWidgetHost.focus(node);
    }

    public void clearNativeWidgetFocus() {
        if (this.nativeWidgetHost == null) {
            return;
        }
        this.nativeWidgetHost.clearFocus();
    }

    public boolean dispatchNativeWidgetMouseClick(@Nonnull NativeWidgetNode<?, ?> node, double mouseX, double mouseY, int button) {
        if (this.nativeWidgetHost == null) {
            return false;
        }
        return this.nativeWidgetHost.dispatchMouseClick(node, mouseX, mouseY, button);
    }

    public boolean forwardMouseClickToVanilla(double mouseX, double mouseY, int button) {
        if (this.nativeWidgetHost == null) {
            LOGGER.warn("Attempted to forward mouse click to vanilla, but this UIManager does not have a NativeWidgetHost.");
            return false;
        }
        return this.nativeWidgetHost.forwardVanillaMouseClick(mouseX, mouseY, button);
    }

    public boolean forwardMouseReleaseToVanilla(double mouseX, double mouseY, int button) {
        if (this.nativeWidgetHost == null) {
            LOGGER.warn("Attempted to forward mouse release to vanilla, but this UIManager does not have a NativeWidgetHost.");
            return false;
        }
        return this.nativeWidgetHost.forwardVanillaMouseRelease(mouseX, mouseY, button);
    }

    public void addAnimator(@Nonnull BaseUIAnimator<?> animator) {
        this.animationSystem.addAnimator(animator);
    }

    public void addAnimator(@Nullable UINode owner, @Nonnull BaseUIAnimator<?> animator) {
        this.animationSystem.addAnimator(owner, animator);
    }

    public void addAnimator(@Nullable UINode owner, @Nullable String key, @Nonnull BaseUIAnimator<?> animator) {
        this.animationSystem.addAnimator(owner, key, animator);
    }

    public void removeAnimator(BaseUIAnimator<?> animator) {
        if (animator == null) {
            return;
        }
        this.animationSystem.removeAnimator(animator);
    }

    @Nullable
    public BaseUIAnimator<?> getAnimator(@Nonnull UINode owner, @Nonnull String key) {
        return this.animationSystem.getAnimator(owner, key);
    }

    public void replaceAnimator(@Nonnull UINode owner, @Nonnull String key, @Nonnull BaseUIAnimator<?> animator) {
        this.animationSystem.replaceAnimator(owner, key, animator);
    }

    public void removeAnimators(@Nonnull UINode owner) {
        this.animationSystem.removeNodeAnimators(owner);
    }

    public void removeAnimatorsInSubtree(@Nonnull UINode root) {
        this.animationSystem.removeAnimatorsInSubtree(root);
    }


    public static float getWindowWidth() {
        return Minecraft.getInstance().getWindow().getWidth();
    }

    public static float getWindowHeight() {
        return Minecraft.getInstance().getWindow().getHeight();
    }

    public static float getGuiScale() {
        return (float) Minecraft.getInstance().getWindow().getGuiScale();
    }

    void onNodeDetached(@Nonnull UINode node) {
        this.animationSystem.removeAnimatorsInSubtree(node);
        if (node.containsInSubtree(this.capturedNode)) {
            this.capturedNode = null;
        }
        if (node.containsInSubtree(this.mouseOver)) {
            this.mouseOver = null;
        }
        if (node.containsInSubtree(this.contextMenu)) {
            this.contextMenu = null;
        }
        this.mouseMovingListeners.removeIf(node::containsInSubtree);
    }

    private boolean isNativeWidgetTarget(double mouseX, double mouseY) {
        UINode target;
        if (this.capturedNode != null) {
            target = this.capturedNode;
        } else if (this.contextMenu != null && this.contextMenu.hitTest((float) mouseX, (float) mouseY)) {
            target = this.createHitPath(this.contextMenu, mouseX, mouseY).getTop();
        } else {
            target = this.createHitPath(this.rootNode, mouseX, mouseY).getTop();
        }
        return target instanceof NativeWidgetNode<?, ?>;
    }
}
