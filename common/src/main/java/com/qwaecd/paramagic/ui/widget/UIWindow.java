package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.Rect;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import com.qwaecd.paramagic.ui.event.api.UIEventContext;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.io.mouse.MouseButton;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("RedundantMethodOverride")
public class UIWindow extends MouseCaptureNode {
    @Nullable
    protected final UILabel titleLabel;

    /**
     * 控制该 node 是否可以被鼠标捕获移动
     */
    protected boolean allowMove = true;

    public UIWindow(@Nonnull Rect localRect, @Nullable Component title) {
        super();
        this.backgroundColor = UIColor.of(200, 200, 200, 255);
        if (title == null) {
            this.titleLabel = null;
        } else {
            this.titleLabel = new UILabel(title);
            this.titleLabel.getLayoutParams().top();
            this.addChild(this.titleLabel);
        }

        this.localRect.set(localRect);
    }

    public UIWindow(@Nonnull Rect localRect, @Nullable String title) {
        this(localRect, title == null ? null : Component.literal(title));
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        this.captureIfAllowed(context);
    }

    @Override
    protected void onDoubleClick(UIEventContext<DoubleClick> context) {
        this.captureIfAllowed(context);
    }

    public boolean isAllowMove(float mouseX, float mouseY) {
        return this.allowMove && this.hitTest(mouseX, mouseY);
    }

    protected void captureIfAllowed(UIEventContext<? extends MouseClick> context) {
        // 不区分双击还是单击, 但是只允许左键捕获
        MouseClick event = context.event;
        if (!MouseButton.LEFT.is(event.button)) {
            return;
        }

        if (this.captured || !this.isAllowMove((float) event.mouseX, (float) event.mouseY)) {
            return;
        }

        context.getManager().captureNode(this);
        this.captured = true;
        this.grabOffsetX = (float) event.mouseX - this.worldRect.x;
        this.grabOffsetY = (float) event.mouseY - this.worldRect.y;
        context.consume();
    }

    @Override
    protected void onMouseRelease(UIEventContext<MouseRelease> context) {
        if (this.captured) {
            context.getManager().releaseCapture();
            this.captured = false;
            context.consume();
        }
    }

    @Override
    public void render(@Nonnull UIRenderContext context) {
        super.render(context);
        if (this.titleLabel != null) {
            this.titleLabel.render(context);
        }
    }
}
