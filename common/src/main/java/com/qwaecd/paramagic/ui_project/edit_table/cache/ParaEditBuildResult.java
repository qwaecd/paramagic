package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@PlatformScope(PlatformScopeType.CLIENT)
public final class ParaEditBuildResult {
    private final boolean success;
    @Nullable
    private final ParaData paraData;
    private final int droppedNodeCount;
    private final int depthOverflowCount;
    private final int rebuildFailureCount;
    @Nonnull
    private final List<String> warnings;

    private ParaEditBuildResult(
            boolean success,
            @Nullable ParaData paraData,
            int droppedNodeCount,
            int depthOverflowCount,
            int rebuildFailureCount,
            @Nonnull List<String> warnings
    ) {
        this.success = success;
        this.paraData = paraData;
        this.droppedNodeCount = droppedNodeCount;
        this.depthOverflowCount = depthOverflowCount;
        this.rebuildFailureCount = rebuildFailureCount;
        this.warnings = List.copyOf(warnings);
    }

    @Nonnull
    static ParaEditBuildResult success(
            @Nonnull ParaData paraData,
            int droppedNodeCount,
            int depthOverflowCount,
            int rebuildFailureCount,
            @Nonnull List<String> warnings
    ) {
        return new ParaEditBuildResult(
                true,
                paraData,
                droppedNodeCount,
                depthOverflowCount,
                rebuildFailureCount,
                warnings
        );
    }

    @Nonnull
    static ParaEditBuildResult failure(
            int droppedNodeCount,
            int depthOverflowCount,
            int rebuildFailureCount,
            @Nonnull List<String> warnings
    ) {
        return new ParaEditBuildResult(
                false,
                null,
                droppedNodeCount,
                depthOverflowCount,
                rebuildFailureCount,
                warnings
        );
    }

    public boolean isSuccess() {
        return this.success;
    }

    @Nullable
    public ParaData getParaData() {
        return this.paraData;
    }

    public int getDroppedNodeCount() {
        return this.droppedNodeCount;
    }

    public int getDepthOverflowCount() {
        return this.depthOverflowCount;
    }

    public int getRebuildFailureCount() {
        return this.rebuildFailureCount;
    }

    @Nonnull
    public List<String> getWarnings() {
        return this.warnings;
    }

    public boolean hasWarnings() {
        return !this.warnings.isEmpty();
    }
}
