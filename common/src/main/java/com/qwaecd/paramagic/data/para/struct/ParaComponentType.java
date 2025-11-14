package com.qwaecd.paramagic.data.para.struct;

public enum ParaComponentType {
    VOID(0),
    RING(1),
    POLYGON(2),
    CURVY_STAR(3);

    final int ID;
    ParaComponentType(int id) {
        this.ID = id;
    }
    public int ID() {
        return this.ID;
    }
}
