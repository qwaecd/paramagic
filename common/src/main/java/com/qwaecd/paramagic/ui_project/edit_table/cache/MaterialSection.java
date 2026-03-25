package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.ui.widget.node.ColorRgbaEditorNode;
import net.minecraft.client.gui.Font;

public class MaterialSection extends EditSection {
    private final ColorRgbaEditorNode colorEditor;
    private final LabeledFloatRow intensityRow;

    public MaterialSection() {
        this.colorEditor = new ColorRgbaEditorNode();
        this.addChild(this.colorEditor);
        this.colorEditor.setChangeListener(value -> {
            if (this.struct != null) {
                boolean changed =
                        Float.compare(this.struct.getColor().x, value.x) != 0
                                || Float.compare(this.struct.getColor().y, value.y) != 0
                                || Float.compare(this.struct.getColor().z, value.z) != 0
                                || Float.compare(this.struct.getColor().w, value.w) != 0;
                this.struct.getColor().set(value);
                if (changed) {
                    markCacheDirty();
                }
            }
        });

        this.intensityRow = new LabeledFloatRow(LabelTexts.intensityRowText, 16);
        this.addChild(this.intensityRow);
        this.intensityRow.bind(
                () -> this.struct != null ? this.struct.getIntensity() : 0,
                v -> { if (this.struct != null) this.struct.setIntensity(v); });
    }

    @Override
    void layoutContent(Font font, float contentW) {
        float y = 0;

        this.colorEditor.localRect.set(0, y, contentW, 0);
        this.colorEditor.layoutContent(font, contentW);
        y += this.colorEditor.localRect.h + ROW_GAP;

        this.intensityRow.localRect.set(0, y, contentW, 0);
        this.intensityRow.layoutContent(font, contentW);
        y += this.intensityRow.localRect.h;

        this.localRect.h = y;
    }

    @Override
    void syncFromStruct() {
        if (this.struct == null) {
            return;
        }
        this.colorEditor.setColor(this.struct.getColor());
        this.intensityRow.sync(this.struct.getIntensity());
    }
}
