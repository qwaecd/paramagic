package com.qwaecd.paramagic.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class ManaContext {
    private final Level level;
    private final Player caster;
    private final BlockPos center;
    private final Map<String, Object> parameters;
    private int availableMana;

    public ManaContext(Level level, Player caster, BlockPos center) {
        this.level = level;
        this.caster = caster;
        this.center = center;
        this.parameters = new HashMap<>();
        this.availableMana = 0;
    }

    // Getters and setters
    public Level getLevel() { return level; }
    public Player getCaster() { return caster; }
    public BlockPos getCenter() { return center; }
    public Map<String, Object> getParameters() { return parameters; }
    public int getAvailableMana() { return availableMana; }
    public void setAvailableMana(int mana) { this.availableMana = mana; }

    public void addParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public <T> T getParameter(String key, Class<T> type) {
        Object value = parameters.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }
}
