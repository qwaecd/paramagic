package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.data.para.util.ParaComponentBuilder;
import com.qwaecd.paramagic.feature.effect.explosion.ExplosionParaNode;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.widget.node.CanvasNode;
import com.qwaecd.paramagic.ui.widget.node.PTTreeNode;

public class EditWindow extends UINode {

    private final CanvasNode canvas;

    public EditWindow() {
        this.localRect.setWH(220, 180);
        this.backgroundColor = UIColor.of(129, 64, 0, 255);
        this.layoutParams.center();
        this.clipMod = ClipMod.RECT;
        this.canvas = new CanvasNode();
        this.addChild(this.canvas);

        this.initCanvas();
    }

    private void initCanvas() {
//        ParaData paraData = new ParaData(ExplosionParaNode.createParaData("edit"));
        ParaComponentBuilder builder = new ParaComponentBuilder()
                .beginChild()
                    .beginChild()
                        .beginChild()
                        .endChild()
                    .endChild()
                .endChild()

                .beginChild()
                    .beginChild()
                        .beginChild()
                        .endChild()
                    .endChild()

                    .beginChild()
                    .endChild()
                .endChild()

                .beginChild()
                .endChild()

                .beginChild()
                    .beginChild()
                        .beginChild()
                            .beginChild()
                                .beginChild()
                                .endChild()
                            .endChild()
                        .endChild()
                    .endChild()
                .endChild();
        for (int i = 0; i < 4; i++) {
            var builder1 = builder.beginChild();
            for (int j = 0; j < 6; j++) {
                builder1.beginChild().endChild();
            }
            builder1.endChild();
        }
        ParaData paraData = new ParaData(builder.build());
        PTTreeNode treeNode = new PTTreeNode(new ParaTree(paraData));
        this.canvas.addChild(treeNode);
    }
}
