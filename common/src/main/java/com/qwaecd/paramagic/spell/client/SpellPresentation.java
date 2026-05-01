package com.qwaecd.paramagic.spell.client;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.core.EndSpellReason;

@PlatformScope(PlatformScopeType.CLIENT)
public interface SpellPresentation {
    void onStart(ClientSpellContext context);

    /**
     * 游戏 tick 循环，不是渲染循环
     */
    void tick(ClientSpellContext context);

    void onStop(ClientSpellContext context, EndSpellReason reason);

    boolean canDispose();

    void dispose(ClientSpellContext context);
}
