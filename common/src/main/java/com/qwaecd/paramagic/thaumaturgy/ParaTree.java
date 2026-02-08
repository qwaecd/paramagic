package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.data.para.struct.ParaData;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ParaTree {
    private final Map<String, ParaNode> nodes = new HashMap<>();
    @Nonnull
    private final ParaData paraData;

    public ParaTree(@Nonnull ParaData paraData) {
        this.paraData = paraData;
    }
}
