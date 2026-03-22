package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.node.TypingBox;
import net.minecraft.client.gui.Font;

import javax.annotation.Nullable;

public abstract class EditSection extends UINode {
    static final float ROW_GAP = 2.0f;
    static final float INPUT_HEIGHT = 14.0f;
    static final float LABEL_HEIGHT = 10.0f;
    static final float COMPONENT_GAP = 4.0f;

    @Nullable
    protected ParaStruct struct;

    protected EditSection() {
        this.setHitTestable(false);
    }

    void setStruct(@Nullable ParaStruct struct) {
        this.struct = struct;
    }

    abstract void layoutContent(Font font, float contentW);

    abstract void syncFromStruct();

    protected UILabel createLabel(String text) {
        UILabel label = new UILabel(text);
        label.setHitTestable(false);
        this.addChild(label);
        return label;
    }

    protected TypingBox createInputBox(int maxLength) {
        TypingBox box = new TypingBox();
        box.localRect.set(0, 0, 0, INPUT_HEIGHT);
        box.setMaxLength(maxLength);
        this.addChild(box);
        return box;
    }

    @FunctionalInterface
    interface FloatSupplier {
        float get();
    }

    @FunctionalInterface
    interface FloatConsumer {
        void accept(float value);
    }

    @FunctionalInterface
    interface IntSupplier {
        int get();
    }

    @FunctionalInterface
    interface IntConsumer {
        void accept(int value);
    }
}
