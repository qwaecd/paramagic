package com.qwaecd.paramagic.feature;

import com.qwaecd.paramagic.api.IMagicMap;
import com.qwaecd.paramagic.init.MagicMapRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManaNode {
    private final String id;
    private final String name;
    private final List<String> boundMagicMaps;
    private final Map<String, Object> properties;
    private boolean isEndNode;
    private int manaMultiplier;

    public ManaNode(String id, String name) {
        this.id = id;
        this.name = name;
        this.boundMagicMaps = new ArrayList<>();
        this.properties = new HashMap<>();
        this.isEndNode = false;
        this.manaMultiplier = 1;
    }

    // Getters and setters
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<String> getBoundMagicMaps() {
        return new ArrayList<>(boundMagicMaps); }
    public boolean isEndNode() {
        return isEndNode;
    }
    public int getManaMultiplier() {
        return manaMultiplier;
    }

    public void addMagicMap(String magicMapId) {
        if (!boundMagicMaps.contains(magicMapId)) {
            boundMagicMaps.add(magicMapId);
        }
    }

    public void removeMagicMap(String magicMapId) {
        boundMagicMaps.remove(magicMapId);
    }

    public void setEndNode(boolean endNode) {
        this.isEndNode = endNode;
    }

    public void setManaMultiplier(int multiplier) {
        this.manaMultiplier = Math.max(1, multiplier);
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public int consumeMana(int availableMana) {
        // Base consumption calculation
        int totalCost = 0;
        for (String mapId : boundMagicMaps) {
            IMagicMap map = MagicMapRegistry.get(mapId);
            if (map != null) {
                totalCost += map.getManaCost();
            }
        }

        totalCost *= manaMultiplier;
        return Math.min(totalCost, availableMana);
    }
}
