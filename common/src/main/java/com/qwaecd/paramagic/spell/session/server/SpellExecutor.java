package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import net.minecraft.server.level.ServerLevel;

@PlatformScope(PlatformScopeType.SERVER)
public class SpellExecutor {

    public SpellExecutor() {
    }

    public void tick(ServerSessionView session, SpellPhaseType currentPhase, ServerLevel level) {}

    public void onPhaseChanged(ServerSessionView session, SpellPhaseType oldPhase, SpellPhaseType currentPhase) {}

    /**
     * 当 session 尝试结束时会调用该函数以询问是否应该结束.
     * @return 是否应该结束
     */
    public boolean canFinish() {
        return true;
    }

    public void onSessionClose() {}

    public void onInterrupt() {}
}
