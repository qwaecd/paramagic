package com.qwaecd.paramagic.api;

public abstract class MagicBase implements IMagicMap{
    protected final String id;
    protected final String displayName;
    protected final String description;
    protected final MagicType type;
    protected final int manaCost;

    public MagicBase(String id, String displayName, String description, MagicType type, int manaCost) {
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
    public MagicType getType() {
        return type;
    }

    @Override
    public int getManaCost() {
        return manaCost;
    }
}
