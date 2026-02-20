package com.qwaecd.paramagic.thaumaturgy.runtime;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class ParaContext {
    @Getter
    @Nonnull
    public final ServerSession session;
    @Getter
    @Nonnull
    public final ServerLevel level;
    @Getter
    public final SpellCaster caster;
    private final Map<OperatorType, List<ParaOperator>> operators = new EnumMap<>(OperatorType.class);
    private final List<ProjectileEntity> projectiles = new ArrayList<>();

    public ParaContext(@Nonnull ServerSession session, @Nonnull ServerLevel level, SpellCaster caster) {
        this.session = session;
        this.level = level;
        this.caster = caster;
    }

    public void execute() {
        this.prepareProjectiles();
        this.iterate(OperatorType.MODIFIER);
        this.iterate(OperatorType.ALPHA);
        this.iterate(OperatorType.FLOW);

        for (ProjectileEntity projectile : this.projectiles) {
            projectile.shoot();
        }
    }

    private void iterate(OperatorType type) {
        List<ParaOperator> list = this.operators.get(type);
        if (list == null) {
            return;
        }
        Iterator<ParaOperator> iterator = list.iterator();
        while (iterator.hasNext()) {
            ParaOperator operator = iterator.next();
            boolean applied = operator.apply(this);
            if (applied) {
                iterator.remove();
            }
        }
    }

    private void prepareProjectiles() {
        this.projectiles.clear();
        var list = this.operators.get(OperatorType.PROJECTILE);
        if (list == null) {
            return;
        }
        for (ParaOperator operator : list) {
            operator.apply(this);
        }

        this.operators.remove(OperatorType.PROJECTILE);
    }

    public void addProjectile(ProjectileEntity projectile) {
        this.projectiles.add(projectile);
    }

    public void addOperator(@Nonnull ParaOperator operator) {
        OperatorType type = operator.getType();
        this.operators.computeIfAbsent(type, t -> new ArrayList<>()).add(operator);
    }

    public void forEachProjectiles(ProjectileModifier action) {
        for (int i = 0; i < this.projectiles.size(); i++) {
            action.modify(this.projectiles.get(i), i);
        }
    }

    @Nullable
    public ProjectileEntity getProjectile(int index) {
        return this.projectiles.get(index);
    }

    @FunctionalInterface
    public interface ProjectileModifier {
        void modify(ProjectileEntity projectile, int indexInList);
    }
}
