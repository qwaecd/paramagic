package com.qwaecd.paramagic.magic;

import com.qwaecd.paramagic.api.IMagicMap;
import com.qwaecd.paramagic.api.ManaContext;
import net.minecraft.world.level.Level;

public class TestMagic implements IMagicMap {
    @Override
    public void execute(ManaContext context) {
        Level level = context.getLevel();
        double x = context.getCenter().x;
        double y = context.getCenter().y;
        double z = context.getCenter().z;
        level.explode(null, x, y+1, z, 1.0f, Level.ExplosionInteraction.MOB);
    }

    @Override
    public MagicMapType getType() {
        return MagicMapType.EXECUTE;
    }

    @Override
    public String getId() {
        return "test_magic";
    }

    @Override
    public int getCastDelay() {
        return 5;
    }

    @Override
    public int getManaCost() {
        return 10;
    }
}
