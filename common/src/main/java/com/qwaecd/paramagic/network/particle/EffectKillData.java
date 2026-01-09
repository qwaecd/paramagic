package com.qwaecd.paramagic.network.particle;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
public class EffectKillData implements IDataSerializable {
    @Getter
    public final int netId;

    public EffectKillData(int netId) {
        this.netId = netId;
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("netId", this.netId);
    }

    public static EffectKillData fromCodec(DataCodec codec) {
        int netId = codec.readInt("netId");
        return new EffectKillData(netId);
    }
}
