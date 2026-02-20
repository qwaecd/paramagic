package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.session.SessionState;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.thaumaturgy.runtime.ArcaneProcessor;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ArcSessionServer extends ServerSession {
    private final ArcaneProcessor processor;

    public ArcSessionServer(UUID sessionId, @Nonnull SpellCaster caster, ServerLevel level, @Nonnull ParaTree paraTree) {
        super(sessionId, caster, level);

        ParaContext paraContext = new ParaContext(this, level, caster);
        this.processor = new ArcaneProcessor(paraTree, paraContext);
        this.processor.init();
    }

    @Override
    public void tickOnLevel(ServerLevel level, float deltaTime) {
        this.processor.tick();
    }

    @Override
    public boolean canRemoveFromManager() {
        return !isState(SessionState.RUNNING);
    }
}
