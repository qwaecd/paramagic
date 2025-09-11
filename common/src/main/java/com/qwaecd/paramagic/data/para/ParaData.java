package com.qwaecd.paramagic.data.para;

import java.util.UUID;

public class ParaData {
    public final UUID circleUUID;
    public final String schemaVersion = "1.0";
    public final ParaComponentData rootComponent;
    public static final String PARENT_ID = "root";
    public ParaData(ParaComponentData rootComponent) {
        this.rootComponent = rootComponent;
        this.circleUUID = UUID.randomUUID();
        generateComponentIds(rootComponent, PARENT_ID);
    }
    public ParaData(ParaComponentData rootComponent, UUID uuid) {
        this.rootComponent = rootComponent;
        this.circleUUID = uuid;
        generateComponentIds(rootComponent, PARENT_ID);
    }
    private void generateComponentIds(ParaComponentData parent, String parentId) {
        parent.componentId = parentId;
        for (int i = 0; i < parent.children.size(); i++) {
            ParaComponentData child = parent.children.get(i);
            String childId = parentId + "." + i;
            generateComponentIds(child, childId);
        }
    }
}
