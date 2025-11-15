package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.PacketByteBufCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class ParaDataSerializer implements EntityDataSerializer<ParaData> {
    @Override
    public void write(FriendlyByteBuf buffer, ParaData value) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        codec.writeObject(null, value);
    }

    @Override
    public ParaData read(FriendlyByteBuf buffer) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        return codec.readObject(null, ParaData::fromCodec);
    }

    @Override
    public ParaData copy(ParaData value) {
        return value.copy();
    }
}
