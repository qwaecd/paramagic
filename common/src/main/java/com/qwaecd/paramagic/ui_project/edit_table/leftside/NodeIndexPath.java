package com.qwaecd.paramagic.ui_project.edit_table.leftside;

import com.qwaecd.paramagic.ui.core.UINode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record NodeIndexPath(@Nonnull int[] childIndices) {
    @Nullable
    public static NodeIndexPath fromRootToNode(@Nonnull ParaPathNode root, @Nonnull ParaPathNode target) {
        List<Integer> reversed = new ArrayList<>();
        UINode current = target;
        while (current != root) {
            UINode parent = current.getParent();
            if (!(parent instanceof ParaPathNode parentPathNode)) {
                return null;
            }

            int childIndex = parentPathNode.getChildren().indexOf(current);
            if (childIndex < 0) {
                return null;
            }

            reversed.add(childIndex);
            current = parentPathNode;
        }

        Collections.reverse(reversed);
        int[] indices = new int[reversed.size()];
        for (int i = 0; i < reversed.size(); i++) {
            indices[i] = reversed.get(i);
        }
        return new NodeIndexPath(indices);
    }

    @Nullable
    public ParaPathNode resolve(@Nonnull ParaPathNode root) {
        ParaPathNode current = root;
        for (int childIndex : this.childIndices) {
            if (childIndex < 0 || childIndex >= current.getChildren().size()) {
                return null;
            }

            UINode child = current.getChildren().get(childIndex);
            if (!(child instanceof ParaPathNode pathNode)) {
                return null;
            }
            current = pathNode;
        }
        return current;
    }
}
