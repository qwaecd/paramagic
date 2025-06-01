package com.qwaecd.paramagic.magic;

import com.qwaecd.paramagic.api.IMagicMap;
import com.qwaecd.paramagic.api.ManaContext;
import net.minecraft.world.level.Level;

public class TestMagic implements IMagicMap {
    @Override
    public void execute(ManaContext context) {
        Level level = context.getLevel();
        double x = context.getCenter().getX();
        double y = context.getCenter().getY();
        double z = context.getCenter().getZ();
        level.explode(null,x,y,z,1.0f, Level.ExplosionInteraction.MOB);
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
