package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.PacketByteBufCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

import javax.annotation.Nonnull;

public class ParaDataSerializer implements EntityDataSerializer<ParaData> {
    @Override
    public void write(@Nonnull FriendlyByteBuf buffer, @Nonnull ParaData value) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        codec.writeObject(null, value);
    }

    @Nonnull
    @Override
    public ParaData read(@Nonnull FriendlyByteBuf buffer) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        return codec.readObject(null, ParaData::fromCodec);
    }

    @Nonnull
    @Override
    public ParaData copy(@Nonnull ParaData value) {
        return value.copy();
    }
}
