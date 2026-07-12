package com.qwaecd.paramagic.ui.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Tooltip 的纯内容描述。非 null 的空内容表示明确阻止父节点的 tooltip 回溯。
 */
public record TooltipContent(
        @Nonnull List<Component> lines,
        @Nonnull Optional<TooltipComponent> visualComponent
) {
    @Nonnull
    public static final TooltipContent EMPTY = new TooltipContent(List.of(), Optional.empty());

    public TooltipContent {
        lines = List.copyOf(lines);
        visualComponent = visualComponent == null ? Optional.empty() : visualComponent;
    }

    public boolean isEmpty() {
        return this.lines.isEmpty() && this.visualComponent.isEmpty();
    }

    @Nonnull
    public static TooltipContent of(@Nullable List<Component> lines) {
        if (lines == null) {
            return EMPTY;
        }
        return new TooltipContent(lines, Optional.empty());
    }

    @Nonnull
    public static TooltipContent of(@Nonnull Component... line) {
        return TooltipContent.of(Arrays.stream(line).toList());
    }
}
