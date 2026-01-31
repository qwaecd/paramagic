package com.qwaecd.paramagic.ui.io;

public class MouseStateMachine {
    private MouseState currentState;

    public MouseStateMachine() {
        this.currentState = MouseState.IDLE;
    }

    public void updateState(MouseEvent event) {
        switch (this.currentState) {
            case IDLE -> {
                if (event.type == MouseEventType.CLICK) {
                    this.currentState = MouseState.PRESSED;
                }
            }
            case PRESSED -> {
                if (event.type == MouseEventType.RELEASE) {
                    this.currentState = MouseState.IDLE;
                }
            }
            case DRAGGING -> {
                if (event.type == MouseEventType.RELEASE) {
                    this.currentState = MouseState.IDLE;
                }
            }
        }
    }
}
