package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.ui.MCEditBox;
import com.qwaecd.paramagic.ui.nativewidget.NativeWidgetAdapter;
import com.qwaecd.paramagic.ui.nativewidget.NativeWidgetNode;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TypingBox extends NativeWidgetNode<MCEditBox, TypingBox> {
    private static final NativeWidgetAdapter<TypingBox, MCEditBox> ADAPTER = new NativeWidgetAdapter<>() {
        @Override
        @Nonnull
        public MCEditBox createWidget(@Nonnull TypingBox node) {
            MCEditBox box = new MCEditBox(0, 0, 0, 0, Component.empty());
            box.setResponder(node::onTextChanged);
            return box;
        }

        @Override
        public void syncWidget(@Nonnull TypingBox node, @Nonnull MCEditBox widget) {
            widget.resize(node.worldRect);
            widget.active = node.isHitTestable();
            widget.visible = node.isVisible();
            widget.setEditable(node.editable);
            widget.setMaxLength(node.maxLength);
            if (!Objects.equals(widget.getValue(), node.text)) {
                widget.setValue(node.text);
            }
        }
    };

    @Nonnull
    private String text = "";
    private boolean editable = true;
    private int maxLength = 32;

    public TypingBox() {
        super(ADAPTER);
    }

    private void onTextChanged(@Nonnull String text) {
        this.text = text;
    }

    public void setText(@Nonnull String text) {
        this.text = text;
        this.syncNativeWidget();
    }

    @Nonnull
    public String getText() {
        return this.text;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        this.syncNativeWidget();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        this.syncNativeWidget();
    }
}
