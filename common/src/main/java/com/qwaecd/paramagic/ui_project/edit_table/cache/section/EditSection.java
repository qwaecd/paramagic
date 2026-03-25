package com.qwaecd.paramagic.ui_project.edit_table.cache.section;

import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.node.TypingBox;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaEditCache;
import com.qwaecd.paramagic.ui_project.edit_table.cache.ParaStruct;
import com.qwaecd.paramagic.ui_project.edit_table.util.EditInputRules;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EditSection extends UINode {
    static final float ROW_GAP = 2.0f;
    public static final float INPUT_HEIGHT = 14.0f;
    public static final float LABEL_HEIGHT = 10.0f;
    public static final float COMPONENT_GAP = 4.0f;

    @Nullable
    protected ParaStruct struct;

    protected EditSection() {
        this.setHitTestable(false);
    }

    public void setStruct(@Nullable ParaStruct struct) {
        this.struct = struct;
    }

    public abstract void layoutContent(Font font, float contentW);

    public abstract void syncFromStruct();

    protected UILabel createLabel(Component text) {
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

    public static void markCacheDirty() {
        ParaEditCache cache = ParaEditCache.getCache();
        if (cache != null) {
            cache.markDirty();
        }
    }

    @Nonnull
    protected static String validateStringInput(@Nullable String text) {
        return EditInputRules.validateString(text);
    }

    @FunctionalInterface
    public interface FloatSupplier {
        float get();
    }

    @FunctionalInterface
    public interface FloatConsumer {
        void accept(float value);
    }

    @FunctionalInterface
    public interface IntSupplier {
        int get();
    }

    @FunctionalInterface
    public interface IntConsumer {
        void accept(int value);
    }
}
