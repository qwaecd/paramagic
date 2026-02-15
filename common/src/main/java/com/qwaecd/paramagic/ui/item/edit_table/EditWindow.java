package com.qwaecd.paramagic.ui.item.edit_table;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.feature.effect.explosion.ExplosionParaNode;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
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
        ParaData paraData = new ParaData(ExplosionParaNode.createParaData("edit"));
        PTTreeNode treeNode = new PTTreeNode(new ParaTree(paraData));
        this.canvas.addListener(
                AllUIEvents.MOUSE_CLICK,
                EventPhase.CAPTURING,
                treeNode::onMouseClick
        );
        this.canvas.addListener(
                AllUIEvents.MOUSE_DOUBLE_CLICK,
                EventPhase.CAPTURING,
                treeNode::onDoubleClick
        );
        this.canvas.addChild(treeNode);
    }
}
