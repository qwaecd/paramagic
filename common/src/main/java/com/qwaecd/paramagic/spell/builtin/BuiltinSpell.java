package com.qwaecd.paramagic.spell.builtin;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.session.server.MachineSessionServer;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;

import javax.annotation.Nonnull;

@PlatformScope(PlatformScopeType.COMMON)
public interface BuiltinSpell {
    @Nonnull
    SpellStateMachine createMachine();

    /**
     * 该法术在 服务端 Session 创建时被调用.<br>
     * @param session 创建的 MachineSessionServer.
     */
    default void onCreateSession(MachineSessionServer session) {}
}
