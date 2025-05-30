package com.qwaecd.paramagic.util;

import com.qwaecd.paramagic.feature.ManaLine;
import com.qwaecd.paramagic.feature.ManaNode;
import com.qwaecd.paramagic.feature.Spell;

import java.util.UUID;

public class SpellBuilder {
    private final Spell spell;

    public SpellBuilder(String name) {
        this.spell = new Spell(UUID.randomUUID().toString(), name);
    }

    public SpellBuilder(String id, String name) {
        this.spell = new Spell(id, name);
    }

    public SpellBuilder description(String description) {
        spell.setDescription(description);
        return this;
    }

    public SpellBuilder addNode(String id, String name) {
        ManaNode node = new ManaNode(id, name);
        spell.addNode(node);
        return this;
    }

    public SpellBuilder addNodeWithMagic(String id, String name, String... magicMapIds) {
        ManaNode node = new ManaNode(id, name);
        for (String mapId : magicMapIds) {
            node.addMagicMap(mapId);
        }
        spell.addNode(node);
        return this;
    }

    public SpellBuilder addLine(String id, String... nodeIds) {
        ManaLine line = new ManaLine(id);
        for (String nodeId : nodeIds) {
            line.addNode(nodeId);
        }
        spell.addManaLine(line);
        return this;
    }

    public SpellBuilder setNodeAsEnd(String nodeId) {
        ManaNode node = spell.getNode(nodeId);
        if (node != null) {
            node.setEndNode(true);
        }
        return this;
    }

    public SpellBuilder setNodeManaMultiplier(String nodeId, int multiplier) {
        ManaNode node = spell.getNode(nodeId);
        if (node != null) {
            node.setManaMultiplier(multiplier);
        }
        return this;
    }

    public Spell build() {
        if (!spell.isValid()) {
            throw new IllegalStateException("Invalid spell configuration");
        }
        return spell;
    }
}
