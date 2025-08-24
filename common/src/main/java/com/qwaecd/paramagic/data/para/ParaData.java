package com.qwaecd.paramagic.data.para;

import java.util.UUID;

public class ParaData {
    public final UUID circleUUID;
    public final String schemaVersion = "1.0";
    public ParaComponentData rootComponent;
    public ParaData() {
        this.circleUUID = UUID.randomUUID();
    }
    public ParaData(UUID uuid) {
        this.circleUUID = uuid;
    }
}
