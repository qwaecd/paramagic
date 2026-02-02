package com.qwaecd.paramagic.ui.io.mouse;

public abstract class MouseEvent {
    public final MouseEventType type;
    public final double mouseX;
    public final double mouseY;

    protected MouseEvent(MouseEventType type, double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.type = type;
    }

    public boolean isClick() {
        return this.type == MouseEventType.CLICK;
    }

    public boolean isClickOrDouble() {
        return this.type == MouseEventType.CLICK || this.type == MouseEventType.DOUBLE_CLICK;
    }

    public boolean isRelease() {
        return this.type == MouseEventType.RELEASE;
    }

    public boolean isScroll() {
        return this.type == MouseEventType.SCROLL;
    }

    public static class Click extends MouseEvent {
        public final int button;
        public Click(double mouseX, double mouseY, int button) {
            super(MouseEventType.CLICK, mouseX, mouseY);
            this.button = button;
        }
    }

    public static class DoubleClick extends MouseEvent {
        public final int button;
        public DoubleClick(double mouseX, double mouseY, int button) {
            super(MouseEventType.DOUBLE_CLICK, mouseX, mouseY);
            this.button = button;
        }

        public DoubleClick(Click clickEvent) {
            this(clickEvent.mouseX, clickEvent.mouseY, clickEvent.button);
        }
    }

    public static class Released extends MouseEvent {
        public final int button;
        public Released(double mouseX, double mouseY, int button) {
            super(MouseEventType.RELEASE, mouseX, mouseY);
            this.button = button;
        }
    }

    public static class Scrolled extends MouseEvent {
        public final double scrollAmount;
        public Scrolled(double mouseX, double mouseY, double scrollAmount) {
            super(MouseEventType.SCROLL, mouseX, mouseY);
            this.scrollAmount = scrollAmount;
        }
    }
}
