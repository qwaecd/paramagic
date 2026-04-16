package com.qwaecd.paramagic.network.serializer;

import com.qwaecd.paramagic.network.codec.PacketByteBufCodec;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.world.entity.projectile.BaseProjectile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnnecessaryLocalVariable")
public class ProjectileRuntimeModifierSerializer implements EntityDataSerializer<List<ParaOpId>> {
    @Override
    public void write(FriendlyByteBuf buffer, List<ParaOpId> value) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        BaseProjectile.writeRecordedOperators(codec, value);
    }

    @Override
    public List<ParaOpId> read(FriendlyByteBuf buffer) {
        PacketByteBufCodec codec = new PacketByteBufCodec(buffer);
        ParaOpId[] paraOpIds = BaseProjectile.readRecordedOperators(codec);
        List<ParaOpId> list = Arrays.stream(paraOpIds).collect(ArrayList::new, List::add, List::addAll);
        return list;
    }

    @Override
    public List<ParaOpId> copy(List<ParaOpId> value) {
        List<ParaOpId> list = value.stream().collect(ArrayList::new, List::add, List::addAll);
        return list;
    }
}
