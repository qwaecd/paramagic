package com.qwaecd.paramagic.core.render.shader;

/**
 * 内部使用的简单 String->int 映射（开放寻址，线性探测）。
 * 目标：零装箱、较低分配、快速查找。非线程安全。
 */
public final class StringIntMap {
    // 用于表示“没有找到”
    public static final int MISSING = Integer.MIN_VALUE;

    private String[] keys;
    private int[] values;
    private int size;
    private int mask;
    private int threshold;

    public StringIntMap(int initialCapacity) {
        int cap = 1;
        while (cap < initialCapacity) cap <<= 1;
        keys = new String[cap];
        values = new int[cap];
        mask = cap - 1;
        threshold = (int) (cap * 0.6f);
        size = 0;
    }

    public int get(String key) {
        int idx = indexFor(key);
        while (true) {
            String k = keys[idx];
            if (k == null) return MISSING;
            if (k.equals(key)) return values[idx];
            idx = (idx + 1) & mask;
        }
    }

    public void put(String key, int val) {
        if (size >= threshold) resize();
        int idx = indexFor(key);
        while (true) {
            String k = keys[idx];
            if (k == null) {
                keys[idx] = key;
                values[idx] = val;
                size++;
                return;
            }
            if (k.equals(key)) {
                values[idx] = val; // 更新
                return;
            }
            idx = (idx + 1) & mask;
        }
    }

    private int indexFor(String key) {
        int h = key.hashCode();
        // 处理负数并混合几位 (简单做法)
        h ^= (h >>> 16);
        return h & mask;
    }

    private void resize() {
        String[] oldKeys = keys;
        int[] oldVals = values;
        int newCap = keys.length << 1;
        keys = new String[newCap];
        values = new int[newCap];
        mask = newCap - 1;
        threshold = (int) (newCap * 0.6f);
        size = 0;
        for (int i = 0; i < oldKeys.length; i++) {
            String k = oldKeys[i];
            if (k != null) {
                put(k, oldVals[i]);
            }
        }
    }
}
