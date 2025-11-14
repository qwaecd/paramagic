package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import net.minecraft.network.syncher.EntityDataSerializer;

public final class AllEntityDataSerializers {
    public static final EntityDataSerializer<ParaData> PARA_DATA = new ParaDataSerializer();
}
