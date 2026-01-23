package com.qwaecd.paramagic.spell.listener;

import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.logic.ExecutionContext;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionListener;
import com.qwaecd.paramagic.spell.session.server.ServerSessionView;

import javax.annotation.Nullable;
import java.util.Objects;

public class ExecutionListener implements ServerSessionListener {
    @Nullable
    private ServerSessionView sessionView;

    public ExecutionListener() {
    }

    @Override
    public void bind(ServerSessionView sessionView) {
        this.sessionView = sessionView;
    }

    @Override
    public void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase) {
        ServerSessionView view = this.v();
        SpellDefinition definition = view.getSpell().definition;
        if (currentPhase == definition.meta.executePhase) {
            ExecutionContext context = new ExecutionContext((ServerSession) view, view.getLevel(), view.getCaster());
            view.getSpell().execute(context);
        }
    }

    @Override
    public void onSpellInterrupted() {

    }

    @Override
    public void onSpellCompleted() {

    }

    private ServerSessionView v() {
        return Objects.requireNonNull(this.sessionView, "SessionView is not bound");
    }
}
