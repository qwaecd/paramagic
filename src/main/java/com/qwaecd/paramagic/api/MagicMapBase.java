package com.qwaecd.paramagic.api;

public abstract class MagicMapBase implements IMagicMap{
    protected final String id;
    protected final String displayName;
    protected final String description;
    protected final MagicMapType type;
    protected final int manaCost;

    public MagicMapBase(String id, String displayName, String description, MagicMapType type, int manaCost) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.manaCost = manaCost;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public MagicMapType getType() {
        return type;
    }

    @Override
    public int getManaCost() {
        return manaCost;
    }
}
