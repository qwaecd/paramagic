package com.qwaecd.paramagic.data.para;

import java.util.UUID;

public class ParaData {
    public final UUID circleUUID;
    public final String schemaVersion = "1.0";
    public final ParaComponentData rootComponent;
    public ParaData(ParaComponentData rootComponent) {
        this.rootComponent = rootComponent;
        this.circleUUID = UUID.randomUUID();
    }
    public ParaData(ParaComponentData rootComponent, UUID uuid) {
        this.rootComponent = rootComponent;
        this.circleUUID = uuid;
    }
}
