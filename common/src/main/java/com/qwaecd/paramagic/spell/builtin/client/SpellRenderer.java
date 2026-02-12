package com.qwaecd.paramagic.spell.builtin.client;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.client.ClientSessionView;

@PlatformScope(PlatformScopeType.CLIENT)
public class SpellRenderer {

    public SpellRenderer() {
    }

    /**
     * 在游戏循环内的 tick, 不是渲染循环
     */
    public void gameTick(ClientSessionView session, SpellPhaseType currentPhase) {}


    public void onPhaseChanged(ClientSessionView session, SpellPhaseType oldPhase, SpellPhaseType currentPhase) {}

    /**
     * 当 session 尝试结束时会调用该函数以询问是否应该结束.
     * @return 是否应该结束.
     */
    public boolean canFinish() {
        return true;
    }

    public void onSessionClose() {}

    public void onInterrupt() {}
}
