package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.data.para.util.ParaComponentBuilder;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui_project.edit_table.leftside.ParaPathNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@PlatformScope(PlatformScopeType.CLIENT)
public final class ParaEditCacheParaDataBuilder {

    @Nonnull
    public ParaEditBuildResult buildFromCache(@Nonnull ParaEditCache cache) {
        return this.buildFromRootNode(cache.getRootNode());
    }

    @Nonnull
    public ParaEditBuildResult buildFromRootNode(@Nonnull ParaPathNode cacheRoot) {
        BuildStats stats = new BuildStats();
        BuildNodeResult rootResult = this.tryBuildNode(cacheRoot, 1, ParaData.PARENT_ID, true, stats);
        if (!rootResult.success()) {
            return ParaEditBuildResult.failure(
                    stats.droppedNodeCount,
                    stats.depthOverflowCount,
                    stats.rebuildFailureCount,
                    stats.warnings
            );
        }

        try {
            ParaComponentBuilder rootBuilder = new ParaComponentBuilder(rootResult.requireData());
            this.appendChildren(rootBuilder, cacheRoot, 2, ParaData.PARENT_ID, stats);
            ParaData paraData = new ParaData(rootBuilder);
            return ParaEditBuildResult.success(
                    paraData,
                    stats.droppedNodeCount,
                    stats.depthOverflowCount,
                    stats.rebuildFailureCount,
                    stats.warnings
            );
        } catch (RuntimeException e) {
            int droppedNodeCount = Math.max(stats.droppedNodeCount, this.countSubtreeNodes(cacheRoot));
            stats.recordBuildFailure(ParaData.PARENT_ID, e, droppedNodeCount);
            return ParaEditBuildResult.failure(
                    stats.droppedNodeCount,
                    stats.depthOverflowCount,
                    stats.rebuildFailureCount,
                    stats.warnings
            );
        }
    }

    private void appendChildren(
            @Nonnull ParaComponentBuilder parentBuilder,
            @Nonnull ParaPathNode parentNode,
            int depth,
            @Nonnull String tracePath,
            @Nonnull BuildStats stats
    ) {
        int childIndex = 0;
        for (UINode rawChild : parentNode.getChildren()) {
            if (!(rawChild instanceof ParaPathNode childNode)) {
                continue;
            }

            String childTracePath = tracePath + "." + childIndex;
            BuildNodeResult childResult = this.tryBuildNode(childNode, depth, childTracePath, false, stats);
            if (childResult.success()) {
                ParaComponentBuilder childBuilder = parentBuilder.beginChild(childResult.requireData());
                this.appendChildren(childBuilder, childNode, depth + 1, childTracePath, stats);
                childBuilder.endChild();
            }
            childIndex++;
        }
    }

    @Nonnull
    private BuildNodeResult tryBuildNode(
            @Nonnull ParaPathNode node,
            int depth,
            @Nonnull String tracePath,
            boolean isRoot,
            @Nonnull BuildStats stats
    ) {
        int droppedNodeCount = this.countSubtreeNodes(node);
        if (depth > ParaTree.recursionLimit) {
            stats.recordDepthOverflow(tracePath, depth, droppedNodeCount, isRoot);
            return BuildNodeResult.failed();
        }

        ParaStruct struct = node.getStruct();
        try {
            ParaComponentData data = struct.rebuildComponentData();
            if (data == null) {
                stats.recordRebuildFailure(tracePath, "rebuild returned null", droppedNodeCount, isRoot);
                return BuildNodeResult.failed();
            }
            return BuildNodeResult.success(data);
        } catch (RuntimeException e) {
            stats.recordRebuildFailure(tracePath, this.buildExceptionMessage(e), droppedNodeCount, isRoot);
            return BuildNodeResult.failed();
        }
    }

    private int countSubtreeNodes(@Nonnull ParaPathNode node) {
        int count = 1;
        for (UINode child : node.getChildren()) {
            if (child instanceof ParaPathNode childNode) {
                count += this.countSubtreeNodes(childNode);
            }
        }
        return count;
    }

    @Nonnull
    private String buildExceptionMessage(@Nonnull RuntimeException e) {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            return e.getClass().getSimpleName();
        }
        return e.getClass().getSimpleName() + ": " + message;
    }

    private static final class BuildStats {
        private int droppedNodeCount;
        private int depthOverflowCount;
        private int rebuildFailureCount;
        private final List<String> warnings = new ArrayList<>();

        private void recordDepthOverflow(@Nonnull String tracePath, int depth, int droppedNodeCount, boolean isRoot) {
            this.droppedNodeCount += droppedNodeCount;
            this.depthOverflowCount++;
            this.warnings.add(
                    (isRoot ? "Root" : "Subtree")
                            + " dropped at "
                            + tracePath
                            + " because depth "
                            + depth
                            + " exceeds ParaTree.recursionLimit="
                            + ParaTree.recursionLimit
                            + ". Dropped nodes: "
                            + droppedNodeCount
            );
        }

        private void recordRebuildFailure(
                @Nonnull String tracePath,
                @Nonnull String reason,
                int droppedNodeCount,
                boolean isRoot
        ) {
            this.droppedNodeCount += droppedNodeCount;
            this.rebuildFailureCount++;
            this.warnings.add(
                    (isRoot ? "Root" : "Subtree")
                            + " dropped at "
                            + tracePath
                            + " because rebuild failed: "
                            + reason
                            + ". Dropped nodes: "
                            + droppedNodeCount
            );
        }

        private void recordBuildFailure(@Nonnull String tracePath, @Nonnull RuntimeException e, int droppedNodeCount) {
            this.droppedNodeCount = Math.max(this.droppedNodeCount, droppedNodeCount);
            this.rebuildFailureCount++;
            this.warnings.add(
                    "ParaData build failed at "
                            + tracePath
                            + ": "
                            + (e.getMessage() == null || e.getMessage().isBlank()
                            ? e.getClass().getSimpleName()
                            : e.getClass().getSimpleName() + ": " + e.getMessage())
                            + "."
            );
        }
    }

    private static final class BuildNodeResult {
        private static final BuildNodeResult FAILED = new BuildNodeResult(false, null);

        private final boolean success;
        @Nullable
        private final ParaComponentData data;

        private BuildNodeResult(boolean success, @Nullable ParaComponentData data) {
            this.success = success;
            this.data = data;
        }

        @Nonnull
        private static BuildNodeResult success(@Nonnull ParaComponentData data) {
            return new BuildNodeResult(true, data);
        }

        @Nonnull
        private static BuildNodeResult failed() {
            return FAILED;
        }

        private boolean success() {
            return this.success;
        }

        @Nonnull
        private ParaComponentData requireData() {
            if (this.data == null) {
                throw new IllegalStateException("BuildNodeResult does not contain ParaComponentData");
            }
            return this.data;
        }
    }
}
