package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.ui.widget.node.ColorRgbaEditorNode;
import net.minecraft.client.gui.Font;

class MaterialSection extends EditSection {
    private final ColorRgbaEditorNode colorEditor;
    private final LabeledFloatRow intensityRow;

    MaterialSection() {
        this.colorEditor = new ColorRgbaEditorNode();
        this.addChild(this.colorEditor);
        this.colorEditor.setChangeListener(value -> {
            if (this.struct != null) {
                this.struct.getColor().set(value);
            }
        });

        this.intensityRow = new LabeledFloatRow("Intensity:", 16);
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
        if (this.struct == null) return;
        this.colorEditor.setColor(this.struct.getColor());
        this.intensityRow.sync(this.struct.getIntensity());
    }
}
