package com.qwaecd.paramagic.core.particle.simulation.emitter;

import java.nio.ByteBuffer;

public interface Emitter {
    /**
     * Initializes the particle data.
     * <p>
     * 初始化粒子数据。
     * @param buffer The ByteBuffer for storing particle data. No need to call flip() after filling. <br> 用于存储粒子数据的ByteBuffer，在填充完数据之后无需flip()。
     * @param particleCount The total number of particles for the entire effect. <br> 整个effect的粒子数量。
     */
    void initialize(final ByteBuffer buffer, final int particleCount);
    void update(float deltaTime);
    boolean isFinished();

    float getTimeSinceFinished();
}
