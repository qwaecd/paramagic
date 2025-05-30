package com.qwaecd.paramagic.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ManaContext {
    private final ServerLevel level;
    private final Player caster;
    private final ItemStack wand;
    private final BlockPos targetPos;
    private final Map<String, Object> parameters;
    private int availableMana;

    public ManaContext(ServerLevel level, Player caster, ItemStack wand, BlockPos targetPos, int availableMana) {
        this.level = level;
        this.caster = caster;
        this.wand = wand;
        this.targetPos = targetPos;
        this.availableMana = availableMana;
        this.parameters = new HashMap<>();
    }

    public ServerLevel getLevel() {
        return level;
    }
    public Player getCaster() {
        return caster;
    }
    public ItemStack getWand() {
        return wand;
    }
    public BlockPos getTargetPos() {
        return targetPos;
    }
    public int getAvailableMana() {
        return availableMana;
    }
    public Map<String, Object> getParameters() {
        return parameters;
    }

    public boolean consumeMana(int amount) {
        if (availableMana >= amount) {
            availableMana -= amount;
            return true;
        }
        return false;
    }

    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return parameters.get(key);
    }
}
