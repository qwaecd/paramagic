package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.thaumaturgy.ArcaneProcessor;
import com.qwaecd.paramagic.thaumaturgy.ParaContext;
import com.qwaecd.paramagic.thaumaturgy.ParaTree;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ArcSessionServer extends ServerSession {
    private final ArcaneProcessor processor;
    @Nonnull
    private final ParaTree tree;
    @Nonnull
    private final ParaContext context;

    public ArcSessionServer(UUID sessionId, @Nonnull SpellCaster caster, ServerLevel level, @Nonnull ParaData paraData) {
        super(sessionId, caster, level);
        this.tree = new ParaTree(paraData);
        this.context = new ParaContext(this, level, caster);
        this.processor = new ArcaneProcessor(this.tree, this.context);
    }

    @Override
    public void tickOnLevel(ServerLevel level, float deltaTime) {
        this.processor.tick();
    }

    @Override
    public void interrupt() {

    }
}
