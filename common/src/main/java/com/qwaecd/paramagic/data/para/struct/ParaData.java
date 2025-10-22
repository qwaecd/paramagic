package com.qwaecd.paramagic.data.para.struct;

import com.qwaecd.paramagic.data.para.util.ParaComponentBuilder;

import java.util.UUID;

public class ParaData {
    public final UUID circleUUID;
    public final String schemaVersion = "1.0";
    public final ParaComponentData rootComponent;
    public static final String PARENT_ID = "root";

    public ParaData(ParaComponentBuilder builder) {
        this(builder.build());
    }

    /**
     * You should prefer to use ParaComponentBuilder to build ParaData, unless you really know what you are doing.<br>
     * 你最好应该使用 ParaComponentBuilder 来构建 ParaData, 除非你非常清楚自己在做什么.
     * @see com.qwaecd.paramagic.data.para.util.ParaComponentBuilder
     */
    public ParaData(ParaComponentData rootComponent) {
        this(rootComponent, UUID.randomUUID());
    }

    private ParaData(ParaComponentData rootComponent, UUID uuid) {
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
