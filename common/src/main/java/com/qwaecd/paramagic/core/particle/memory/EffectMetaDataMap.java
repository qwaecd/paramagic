package com.qwaecd.paramagic.core.particle.memory;

import com.qwaecd.paramagic.core.particle.effect.EffectFlags;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL44.*;

/**
 * <pre>
 * struct EffectMetaData {
 *     uint maxParticles;
 *     uint currentCount;
 *     uint flags;
 *     uint _padding2;
 * };
 * </pre>
 */
public final class EffectMetaDataMap implements AutoCloseable {
    private final int effectMetaDataSSBO;
    private final int maxEffectCount;

    // field offsets in bytes within the struct
    private static final int OFFSET_MAX_PARTICLES = 0;
    private static final int OFFSET_CURRENT_COUNT = 4;
    private static final int OFFSET_FLAGS         = 8;
    private static final int OFFSET_PADDING       = 12;

    private ByteBuffer mappedBuffer;
    public static final int SIZE_IN_BYTES = 4 * Integer.BYTES;


    EffectMetaDataMap(int maxEffectCount, int effectMetaData) {
        this.maxEffectCount = maxEffectCount;
        this.effectMetaDataSSBO = effectMetaData;
    }

    void init() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.effectMetaDataSSBO);
        int storageFlags = GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT;

        long bufferSizeBytes = (long) this.maxEffectCount * SIZE_IN_BYTES;
        glBufferStorage(GL_SHADER_STORAGE_BUFFER, bufferSizeBytes, storageFlags);

        this.mappedBuffer = glMapBufferRange(
                GL_SHADER_STORAGE_BUFFER, 0, bufferSizeBytes, storageFlags
        );

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        if (this.mappedBuffer != null) {
            this.mappedBuffer.order(ByteOrder.nativeOrder());
        }
    }

    /**
     * 更新一个 Effect 的完整元数据。
     * 在 spawnEffect 时调用。
     * @param effectId 要更新的 Effect ID。
     * @param maxParticles 该 Effect 的最大粒子数。
     * @param flags 该 Effect 的初始标志位 (e.g., EFFECT_FLAG_IS_ALIVE)。
     */
    void updateEffect(int effectId, int maxParticles, int flags) {
        if (mappedBuffer == null || effectId < 0 || effectId >= maxEffectCount) {
            return;
        }
        // 1. 定位到指定 effectId 的数据块起始位置
        int baseOffset = effectId * SIZE_IN_BYTES;
        mappedBuffer.position(baseOffset);

        // 2. 按顺序写入结构体的所有字段
        mappedBuffer.putInt(maxParticles); // 写入 maxParticles
        mappedBuffer.putInt(0);      // 写入 currentCount (初始为0)
        mappedBuffer.putInt(flags);        // 写入 flags
        mappedBuffer.putInt(0);
    }

    /**
     * 更新一个 Effect 的标志位。
     * 在需要中断、冻结或恢复 Effect 时调用。
     * @param effectId 要更新的 Effect ID。
     * @param flags 新的标志位值。
     */
    void updateFlags(int effectId, int flags) {
        if (mappedBuffer == null || effectId < 0 || effectId >= maxEffectCount) {
            return;
        }
        // 1. 计算 flags 字段的精确字节偏移量
        int flagOffset = effectId * SIZE_IN_BYTES + OFFSET_FLAGS;

        // 2. 直接在那个位置写入新的 flags 值
        mappedBuffer.putInt(flagOffset, flags);
    }

    /**
     * 清理一个 Effect 的元数据。
     * 在 removeEffect 时调用，将其标记为死亡。
     * @param effectId 要清理的 Effect ID。
     */
    void clearEffect(int effectId) {
        if (mappedBuffer == null || effectId < 0 || effectId >= maxEffectCount) {
            return;
        }
        // 1. 定位到指定 effectId 的数据块起始位置
        int baseOffset = effectId * SIZE_IN_BYTES;
        mappedBuffer.position(baseOffset);

        // 2. 写入 16 字节的零
        mappedBuffer.putInt(0); // maxParticles = 0
        mappedBuffer.putInt(0); // currentCount = 0
        mappedBuffer.putInt(EffectFlags.KILL_ALL.get());
        mappedBuffer.putInt(0); // padding
    }

    @Override
    public void close() {
        if (mappedBuffer != null) {
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, effectMetaDataSSBO);
            glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
            mappedBuffer = null;
        }
    }
}
