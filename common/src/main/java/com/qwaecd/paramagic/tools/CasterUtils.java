package com.qwaecd.paramagic.tools;

import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import com.qwaecd.paramagic.spell.view.EntityTFSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public final class CasterUtils {
    @Nullable
    public static CasterTransformSource tryFindCaster(Level level, SpellSessionRef sessionRef) {
        Entity entity = level.getEntity(sessionRef.casterNetworkId);
        if (entity == null)
            return null;
        if (!entity.getUUID().equals(sessionRef.casterEntityUuid))
            return null;
        if (!entity.isAlive())
            return null;
        return EntityTFSource.create(entity);
    }
}
