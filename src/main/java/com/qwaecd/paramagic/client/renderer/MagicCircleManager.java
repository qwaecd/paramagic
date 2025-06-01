package com.qwaecd.paramagic.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.qwaecd.paramagic.Paramagic.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = MODID)
public class MagicCircleManager {
    private static final ConcurrentMap<UUID, MagicCircle> activeCircles = new ConcurrentHashMap<>();
    private static final List<MagicCircle> renderList = new ArrayList<>();

    /**
     * Add a new magic circle to be managed
     */
    public static void addCircle(MagicCircle circle) {
        activeCircles.put(circle.getId(), circle);
        synchronized (renderList) {
            renderList.add(circle);
        }
    }

    /**
     * Remove a magic circle by ID
     */
    public static void removeCircle(UUID id) {
        MagicCircle removed = activeCircles.remove(id);
        if (removed != null) {
            synchronized (renderList) {
                renderList.remove(removed);
            }
        }
    }

    /**
     * Get a magic circle by ID
     */
    public static MagicCircle getCircle(UUID id) {
        return activeCircles.get(id);
    }

    /**
     * Clear all magic circles
     */
    public static void clearAll() {
        activeCircles.clear();
        synchronized (renderList) {
            renderList.clear();
        }
    }

    /**
     * Update all active magic circles
     */
    public static void updateAll() {
        synchronized (renderList) {
            Iterator<MagicCircle> iterator = renderList.iterator();
            while (iterator.hasNext()) {
                MagicCircle circle = iterator.next();
                circle.tick();

                // Remove finished circles
                if (circle.isFinished()) {
                    iterator.remove();
                    activeCircles.remove(circle.getId());
                }
            }
        }
    }

    /**
     * Render all active magic circles
     */
    public static void renderAll(PoseStack poseStack, MultiBufferSource bufferSource) {
        synchronized (renderList) {
            for (MagicCircle circle : renderList) {
                circle.render(poseStack, bufferSource);
            }
        }
    }

    /**
     * Get the number of active circles
     */
    public static int getActiveCount() {
        return activeCircles.size();
    }

    /**
     * Check if a circle with the given ID exists
     */
    public static boolean hasCircle(UUID id) {
        return activeCircles.containsKey(id);
    }

    /**
     * Get all active circle IDs
     */
    public static List<UUID> getActiveIds() {
        return new ArrayList<>(activeCircles.keySet());
    }

    // Event handle
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            updateAll();
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        // Clear all circles when leaving a world to prevent memory leaks
        clearAll();
    }
}