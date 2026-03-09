package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.widget.node.MouseCaptureNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaStructEditWindow extends MouseCaptureNode {
    @Nullable
    private ParaStruct struct;

    public ParaStructEditWindow(@Nonnull ParaStruct struct) {
        this.struct = struct;
    }

    public void setEditStruct(@Nonnull ParaStruct struct) {
        this.struct = struct;
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        if (this.captured) {
            return;
        }
        super.onMouseClick(context);
        context.allowMCProcessing(true);
    }

    @Override
    protected void onMouseRelease(UIEventContext<MouseRelease> context) {
        if (!this.captured) {
            return;
        }
        super.onMouseRelease(context);
        context.allowMCProcessing(true);
    }
}
