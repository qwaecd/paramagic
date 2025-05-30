package com.qwaecd.paramagic.feature;

import java.util.*;

public class Spell {
    private final String id;
    private String name;
    private String description;
    private final Map<String, ManaNode> nodes;
    private final List<ManaLine> manaLines;
    private final Map<String, Object> metadata;

    public Spell(String id, String name) {
        this.id = id;
        this.name = name;
        this.description = "";
        this.nodes = new HashMap<>();
        this.manaLines = new ArrayList<>();
        this.metadata = new HashMap<>();
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Collection<ManaNode> getNodes() {
        return new ArrayList<>(nodes.values());
    }
    public List<ManaLine> getManaLines() {
        return new ArrayList<>(manaLines);
    }

    // Node management
    public void addNode(ManaNode node) {
        nodes.put(node.getId(), node);
    }

    public void removeNode(String nodeId) {
        nodes.remove(nodeId);
        // Remove from all mana lines
        for (ManaLine line : manaLines) {
            line.removeNode(nodeId);
        }
    }

    public ManaNode getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    // Mana line management
    public void addManaLine(ManaLine line) {
        manaLines.add(line);
    }

    public void removeManaLine(String lineId) {
        manaLines.removeIf(line -> line.getId().equals(lineId));
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    public boolean isValid() {
        // Check if spell has at least one node and one line
        if (nodes.isEmpty() || manaLines.isEmpty()) {
            return false;
        }

        // Check if all nodes in lines exist
        for (ManaLine line : manaLines) {
            for (String nodeId : line.getNodeSequence()) {
                if (!nodes.containsKey(nodeId)) {
                    return false;
                }
            }
        }

        return true;
    }
}