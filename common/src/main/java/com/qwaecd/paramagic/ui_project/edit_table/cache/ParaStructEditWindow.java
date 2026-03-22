package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.util.NineSliceSprite;
import com.qwaecd.paramagic.ui.widget.node.MouseCaptureNode;
import com.qwaecd.paramagic.ui_project.edit_table.EditTableSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaStructEditWindow extends MouseCaptureNode {
    private static final NineSliceSprite sprite =
            NineSliceSprite.builder(ModRL.inModSpace("textures/gui/edit_table.png"), EditTableSprite.TEX_W, EditTableSprite.TEX_H)
                    .slice(0, 80,  224, 16, 16)
                    .slice(1, 96,  224, 16, 16)
                    .slice(2, 112, 224, 16, 16)
                    .slice(3, 80,  240, 16, 16)
                    .slice(4, 96,  240, 16, 16)
                    .slice(5, 112, 240, 16, 16)
                    .slice(6, 80,  256, 16, 16)
                    .slice(7, 96,  256, 16, 16)
                    .slice(8, 112, 256, 16, 16)
                    .build();

    private static final float PADDING = 6.0f;
    private static final float SECTION_GAP = 2.0f;
    private static final float WINDOW_WIDTH = 200.0f;

    @Nullable
    private ParaStruct struct;

    private final EditSection[] sections;

    public ParaStructEditWindow(@Nonnull ParaStruct struct) {
        this.struct = struct;
        this.localRect.setWH(WINDOW_WIDTH, 0);
        this.localRect.setXY(50.0f, 40.0f);

        this.sections = new EditSection[]{
                new BasicInfoSection(),
                new TransformSection(),
                new MaterialSection()
        };

        for (EditSection section : this.sections) {
            section.setStruct(struct);
            this.addChild(section);
        }
    }

    @Override
    protected void afterChildAttachedToManager() {
        this.syncFromStruct();
    }

    public void setEditStruct(@Nonnull ParaStruct struct) {
        this.struct = struct;
        for (EditSection section : this.sections) {
            section.setStruct(struct);
        }
        this.syncFromStruct();
    }

    private void syncFromStruct() {
        if (this.struct == null) return;
        for (EditSection section : this.sections) {
            section.syncFromStruct();
        }
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        Font font = Minecraft.getInstance().font;
        float contentW = this.localRect.w - PADDING * 2;
        float y = PADDING;

        for (int i = 0; i < this.sections.length; i++) {
            EditSection section = this.sections[i];
            section.localRect.set(PADDING, y, contentW, 0);
            section.layoutContent(font, contentW);
            y += section.localRect.h;
            if (i < this.sections.length - 1) {
                y += SECTION_GAP;
            }
        }

        this.localRect.h = y + PADDING;
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void renderBackGround(UIRenderContext context) {
        context.renderNineSliceSprite(
                sprite,
                (int) this.worldRect.x,
                (int) this.worldRect.y,
                (int) this.worldRect.w,
                (int) this.worldRect.h
        );
    }
}
