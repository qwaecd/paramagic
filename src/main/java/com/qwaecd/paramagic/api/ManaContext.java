package com.qwaecd.paramagic.api;

import com.qwaecd.paramagic.capability.ManaCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class ManaContext {
    private final Level level;
    private final Player caster;
    private final Vec3 center;
    private final Map<String, Object> parameters;
    private int availableMana;

    private ManaContext(Level level, Player caster, Vec3 center) {
        this.level = level;
        this.caster = caster;
        this.center = center;
        this.parameters = new HashMap<>();
        this.availableMana = 0;
    }

    public ManaContext(Level level, Player caster, Vec3 center, ItemStack wandItem) {
        this(level, caster, center);
        var cap = wandItem.getCapability(ManaCapability.MANA_STORAGE);
        if (cap.isPresent()) {
            ManaCapability.IManaStorage mana = cap.resolve().get();
            this.availableMana = mana.getMana();
        }
    }

    // Getters and setters
    public Level getLevel() {
        return level;
    }

    public Player getCaster() {
        return caster;
    }

    public Vec3 getCenter() {
        return center;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public int getAvailableMana() {
        return availableMana;
    }

    public void setAvailableMana(int mana) {
        this.availableMana = mana;
    }

    public void addParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public <T> T getParameter(String key, Class<T> type) {
        Object value = parameters.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }
}
