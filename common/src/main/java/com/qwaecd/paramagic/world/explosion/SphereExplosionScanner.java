package com.qwaecd.paramagic.world.explosion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class SphereExplosionScanner {
    private static final Map<Integer, List<BlockOffset>> OFFSET_CACHE = new ConcurrentHashMap<>();

    private SphereExplosionScanner() {
    }

    static List<BlockOffset> offsetsFor(float radius) {
        int cacheRadius = Math.max(1, (int) Math.ceil(radius));
        return OFFSET_CACHE.computeIfAbsent(cacheRadius, SphereExplosionScanner::buildOffsets);
    }

    private static List<BlockOffset> buildOffsets(int radius) {
        int radiusSq = radius * radius;
        List<List<BlockOffset>> buckets = new ArrayList<>(radius + 1);
        for (int i = 0; i <= radius; i++) {
            buckets.add(new ArrayList<>());
        }

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int distanceSq = dx * dx + dy * dy + dz * dz;
                    if (distanceSq > radiusSq) {
                        continue;
                    }
                    float distance = (float) Math.sqrt(distanceSq);
                    int bucketIndex = Math.min(radius, (int) distance);
                    buckets.get(bucketIndex).add(new BlockOffset(dx, dy, dz, distance));
                }
            }
        }

        List<BlockOffset> offsets = new ArrayList<>();
        for (List<BlockOffset> bucket : buckets) {
            offsets.addAll(bucket);
        }
        return Collections.unmodifiableList(offsets);
    }

    static final class BlockOffset {
        final int dx;
        final int dy;
        final int dz;
        final float distance;

        BlockOffset(int dx, int dy, int dz, float distance) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.distance = distance;
        }
    }
}
