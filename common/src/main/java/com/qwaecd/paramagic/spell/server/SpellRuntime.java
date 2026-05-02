package com.qwaecd.paramagic.spell.server;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.core.EndSpellReason;

/**
 * 固定的硬编码法术类型，由实现类来决定到底如何执行
 */
@PlatformScope(PlatformScopeType.SERVER)
public interface SpellRuntime {
    void onStart(ServerSpellContext context);

    void tick(ServerSpellContext context);

    void interrupt(ServerSpellContext context, EndSpellReason reason);

    boolean isFinished();

    void dispose(ServerSpellContext context);
}
