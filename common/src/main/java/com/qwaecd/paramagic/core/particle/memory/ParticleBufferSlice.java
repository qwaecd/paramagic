package com.qwaecd.paramagic.core.particle.memory;


import lombok.Getter;

public final class ParticleBufferSlice {
    /**
     * 切片在主VBO中的起始偏移量（以粒子为单位）。
     */
    @Getter
    private final int offset;

    /**
     * 用户请求的粒子数量。
     */
    private final int requestedCount;

    /**
     * 实际分配的块大小（总是2的幂）。
     */
    private final int allocatedBlockSize;

    ParticleBufferSlice(int offset, int requestedCount, int allocatedBlockSize) {
        this.offset = offset;
        this.requestedCount = requestedCount;
        this.allocatedBlockSize = allocatedBlockSize;
    }

    public int getParticleCount() {
        return requestedCount;
    }

    int getAllocatedBlockSize() {
        return allocatedBlockSize;
    }
}
