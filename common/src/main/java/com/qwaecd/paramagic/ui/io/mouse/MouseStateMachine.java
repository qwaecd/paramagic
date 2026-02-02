package com.qwaecd.paramagic.ui.io.mouse;

import javax.annotation.Nonnull;

public class MouseStateMachine {
    /**
     * 拖拽阈值, 单位是像素
     */
    private static final double DRAG_THRESHOLD = 4.0;
    /**
     * 被判定为双击的时间间隔, 单位是毫秒
     */
    private static final long DOUBLE_CLICK_TIME = 500L;
    /**
     * 被判定为双击的最大距离, 单位是像素
     */
    private static final double DOUBLE_CLICK_DISTANCE = 8.0;
    @Nonnull
    private MouseState currentState;
    private int activeButton = MouseButton.NULL.code;

    // 当前帧鼠标坐标, 在窗口初始化的时候就已经被更新过一次了
    private double mouseX = 0.0d;
    private double mouseY = 0.0d;

    // 双击判定相关
    private boolean isDoubleClick = false;
    private long lastClickTime = 0L;
    private double lastClickX = 0.0d;
    private double lastClickY = 0.0d;

    // 鼠标上一帧的位置
    private double lastMouseX = 0.0d;
    private double lastMouseY = 0.0d;

    // 拖拽判定
    // 鼠标按下时的坐标
    private double pressedX = 0.0d;
    private double pressedY = 0.0d;
    // 本帧的 delta
    private double deltaX = 0.0d;
    private double deltaY = 0.0d;

    public MouseStateMachine() {
        this.currentState = MouseState.IDLE;
    }

    public boolean isDoubleClick() {
        return this.isDoubleClick;
    }

    @Nonnull
    public MouseState getCurrentState() {
        return this.currentState;
    }

    public double mouseX() {
        return this.mouseX;
    }

    public double mouseY() {
        return this.mouseY;
    }

    /**
     * 获取鼠标在本帧的 X 轴移动距离
     */
    public double deltaX() {
        return this.deltaX;
    }

    /**
     * 获取鼠标在本帧的 Y 轴移动距离
     */
    public double deltaY() {
        return this.deltaY;
    }

    /**
     * 本函数应该在 UIManager 的更新函数开头被调用，数据获取只能在更新之后进行，否则可能会返回上一帧的数据.
     * @return 该按键是否被状态机接受, 如果为 false, 说明当前按下的按键并不是第一次按下的按键, 框架应该忽略.
     */
    public boolean updateState(MouseEvent event) {
        switch (this.currentState) {
            case IDLE -> {
                if (event.type == MouseEventType.CLICK) {
                    return this.click((MouseEvent.Click) event);
                }
            }
            case PRESSED -> {
                if (event.type == MouseEventType.RELEASE) {
                    return this.releaseWhenPressed((MouseEvent.Released) event);
                }
            }
            case DRAGGING -> {
                if (event.type == MouseEventType.RELEASE) {
                    return this.releaseWhenDragging((MouseEvent.Released) event);
                }
            }
        }
        return false;
    }

    private boolean click(MouseEvent.Click event) {
        if (this.activeButton == MouseButton.NULL.code) {
            this.activeButton = event.button;
        }

        // 忽略非绑定的按键
        if (event.button != this.activeButton) {
            return false;
        }

        this.doubleClickCheck(event.mouseX, event.mouseY);

        this.currentState = MouseState.PRESSED;

        this.pressedX = event.mouseX;
        this.pressedY = event.mouseY;

        this.lastMouseX = event.mouseX;
        this.lastMouseY = event.mouseY;
        return true;
    }

    private void doubleClickCheck(double mouseX, double mouseY) {
        long now = System.currentTimeMillis();
        double dx = mouseX - this.lastClickX;
        double dy = mouseY - this.lastClickY;
        double distSq = dx * dx + dy * dy;

        // 只有在上一次点击不是双击的情况下，才有可能构成双击
        if (!this.isDoubleClick) {
            this.isDoubleClick = (now - this.lastClickTime < DOUBLE_CLICK_TIME)
                    && (distSq < DOUBLE_CLICK_DISTANCE * DOUBLE_CLICK_DISTANCE);
        } else {
            this.isDoubleClick = false;
        }


        this.lastClickTime = now;
        this.lastClickX = mouseX;
        this.lastClickY = mouseY;
    }

    // 在 PRESSED 状态下释放鼠标
    private boolean releaseWhenPressed(MouseEvent.Released event) {
        if (event.button != this.activeButton) {
            return false;
        }
        this.releaseMouseButton();
        return true;
    }

    // 在 DRAGGING 状态下释放鼠标
    private boolean releaseWhenDragging(MouseEvent.Released event) {
        if (event.button != this.activeButton) {
            return false;
        }
        this.releaseMouseButton();
        return true;
    }

    private void releaseMouseButton() {
        this.activeButton = MouseButton.NULL.code;
        this.currentState = MouseState.IDLE;
    }

    public void onMouseMove(double mouseX, double mouseY) {
        this.deltaX = mouseX - this.lastMouseX;
        this.deltaY = mouseY - this.lastMouseY;

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        if (this.currentState == MouseState.PRESSED) {
            double dx = mouseX - this.pressedX;
            double dy = mouseY - this.pressedY;
            if (dx * dx + dy * dy > DRAG_THRESHOLD * DRAG_THRESHOLD) {
                this.currentState = MouseState.DRAGGING;
            }
        }

        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }
}
