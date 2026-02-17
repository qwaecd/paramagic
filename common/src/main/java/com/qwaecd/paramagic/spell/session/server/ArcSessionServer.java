package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.thaumaturgy.runtime.ArcaneProcessor;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ArcSessionServer extends ServerSession {
    private final ArcaneProcessor processor;

    public ArcSessionServer(UUID sessionId, @Nonnull SpellCaster caster, ServerLevel level, @Nonnull ParaData paraData) {
        super(sessionId, caster, level);

        ParaTree tree = new ParaTree(paraData);
        ParaContext paraContext = new ParaContext(this, level, caster);
        this.processor = new ArcaneProcessor(tree, paraContext);
        this.processor.init();
    }

    @Override
    public void tickOnLevel(ServerLevel level, float deltaTime) {
        this.processor.tick();
    }
}
