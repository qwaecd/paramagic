package com.qwaecd.paramagic.core.particle.memory;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.data.GPUParticle;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL33.*;

public class GPUMemoryManager implements AutoCloseable {
    private final int[] mainVBOs = new int[2];
    private final int totalCapacity;    // 内存池总大小，必须是2的幂
    private final int maxOrder;

    private final List<Integer>[] freeLists;
    private final Map<Integer, ParticleBufferSlice> allocatedSlices;
    /**
     * 创建一个指定大小的GPU内存管理器，大小会被向上调整为最近的2的幂。
     * @param capacityHint 用于计算内存池大小的阶数，最终内存池大小为 2^size。
     */
    @SuppressWarnings("unchecked")
    public GPUMemoryManager(int capacityHint) {
        int normalizedSize = 1;
        int order = 0;
        while (normalizedSize < capacityHint) {
            normalizedSize <<= 1;
            order++;
        }

        this.totalCapacity = normalizedSize;
        this.maxOrder = order;

        this.freeLists = new List[maxOrder + 1];
        for (int i = 0; i < maxOrder + 1; i++) {
            this.freeLists[i] = new ArrayList<>();
        }
        this.freeLists[maxOrder].add(0);

        this.allocatedSlices = new HashMap<>();

        initVBO();
    }

    public int getVBOId(int idx) {
        return mainVBOs[idx];
    }

    public long getPoolByteSize() {
        return (long) totalCapacity * GPUParticle.SIZE_IN_BYTES;
    }

    private void initVBO() {
        long totalBytes = (long) totalCapacity * GPUParticle.SIZE_IN_BYTES;
        glGenBuffers(mainVBOs);

        for (int vbo : mainVBOs) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, totalBytes, GL_DYNAMIC_DRAW);
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        Paramagic.LOG.debug(
                "GPUMemoryManager initialized. Particle capacity: {}, Particle size: {} bytes, Total VBO size: {} MB",
                totalCapacity, GPUParticle.SIZE_IN_BYTES, String.format("%.2f", totalBytes / (1024.0 * 1024.0)));
    }

    /**
     * 从给定需要的粒子数量分配一个ParticleBufferSlice。
     * @param requestedCount 粒子数量。
     * @return 分配的ParticleBufferSlice，如果分配失败则返回null。
     */
    public @Nullable ParticleBufferSlice allocate(int requestedCount) {
        if (requestedCount <= 0 || requestedCount > totalCapacity) {
            Paramagic.LOG.error("Invalid allocation size: {}, or it bigger than totalCapacity: {}.", requestedCount, totalCapacity);
            return null;
        }

        // 从给定需要数量获取最小的2的幂次方块
        int requestedOrder = 0;
        int blockSize = 1;
        while (blockSize < requestedCount) {
            blockSize <<= 1;
            requestedOrder++;
        }

        // 查找是否有空闲的块
        int currentOrder = requestedOrder;
        while (currentOrder <= maxOrder && freeLists[currentOrder].isEmpty()) {
            currentOrder++;
        }

        if (currentOrder > maxOrder) {
            Paramagic.LOG.error("Out of GPU memory: requested {}, total capacity {}.", requestedCount, totalCapacity);
            return null;
        }

        // 取出找到的块，并进行必要的拆分
        int blockAddress = freeLists[currentOrder].remove(0);
        while (currentOrder < requestedOrder) {
            currentOrder--;
            int buddyAddress = blockAddress + (1 << currentOrder);  // 计算需要添加到空闲内存的伙伴地址。当前块的地址+分裂后当前块的大小 就是分裂后剩下的那个空闲内存地址。
            freeLists[currentOrder].add(buddyAddress);
        }

        ParticleBufferSlice newSlice = new ParticleBufferSlice(blockAddress, requestedCount, blockSize);
        this.allocatedSlices.put(blockAddress, newSlice);

        return newSlice;
    }

    public void free(ParticleBufferSlice slice) {
        if (slice == null) return;
        if (!allocatedSlices.containsKey(slice.getOffset())) {
            Paramagic.LOG.error("Attempted to free a slice that was not allocated: {}.", slice);
            return;
        }
        allocatedSlices.remove(slice.getOffset());

        // 2. 获取要释放块的物理信息：地址和大小
        int address = slice.getOffset();
        int blockSize = slice.getAllocatedBlockSize();
        int order = (int) (Math.log(blockSize) / Math.log(2));


        // 3. 循环向上尝试与伙伴合并
        while (order < maxOrder) {
            // what the fuck???
            // 位运算魔法???
            int buddyAddress = address ^ blockSize;

            // 3.2 检查伙伴是否空闲
            // "空闲"意味着它必须存在于对应阶数的空闲列表中
            // TODO: 如果查找出现瓶颈，考虑换成HashSet
            List<Integer> buddyFreeList = freeLists[order];
            if (buddyFreeList.contains(buddyAddress)) {
                // 3.3 伙伴是空闲的，执行合并！
                // a. 从空闲列表中移除伙伴（因为它不再是独立的空闲块了）
                buddyFreeList.remove(Integer.valueOf(buddyAddress));

                // b. 更新当前块的地址为合并后大块的起始地址（总是较小的那个）
                address = Math.min(address, buddyAddress);

                // c. 提升当前块的等级和大小，准备进行下一轮更高阶的合并检查
                order++;
                blockSize <<= 1; // blockSize *= 2
            } else {
                // 3.4 伙伴正在被使用或不存在，无法合并，终止循环
                break;
            }
        }

        // 4. 将最终（可能已经合并过的）块加入其对应阶数的空闲列表
        freeLists[order].add(address);
    }

    @Override
    public void close() throws Exception {
        for (int vbo : mainVBOs) {
            glDeleteBuffers(vbo);
        }
    }
}
