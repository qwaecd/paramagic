package com.qwaecd.paramagic.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.elements.MagicCircle;
import com.qwaecd.paramagic.feature.MagicCircleExamples;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

import static com.qwaecd.paramagic.Paramagic.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = MODID)
public class MagicCircleManager {
    private static MagicCircleManager instance;
    private final Map<String, MagicCircle> activeCircles;
    private final Map<String, MagicCircle> circleTemplates;

    private MagicCircleManager() {
        this.activeCircles = new HashMap<>();
        this.circleTemplates = new HashMap<>();
        initializeTemplates();
    }

    public static MagicCircleManager getInstance() {
        if (instance == null) {
            instance = new MagicCircleManager();
        }
        return instance;
    }

    private void initializeTemplates() {
        // Register example templates
        circleTemplates.put("basic", MagicCircleExamples.createBasicCircle(Vec3.ZERO));
        circleTemplates.put("advanced", MagicCircleExamples.createAdvancedCircle(Vec3.ZERO));
        circleTemplates.put("summoning", MagicCircleExamples.createSummoningCircle(Vec3.ZERO));
        circleTemplates.put("healing", MagicCircleExamples.createHealingCircle(Vec3.ZERO));
    }

    public void createCircle(String id, String templateName, Vec3 position) {
        MagicCircle template = circleTemplates.get(templateName);
        if (template != null) {
            // Create a copy of the template at the new position
            MagicCircle newCircle = copyCircle(template, position);
            activeCircles.put(id, newCircle);
        }
    }

    public void createCircle(String id, MagicCircle circle) {
        activeCircles.put(id, circle);
    }

    public void removeCircle(String id) {
        MagicCircle circle = activeCircles.get(id);
        if (circle != null) {
            circle.setActive(false);
            activeCircles.remove(id);
        }
    }

    public MagicCircle getCircle(String id) {
        return activeCircles.get(id);
    }

    public void updateAll(float deltaTime) {
        for (MagicCircle circle : activeCircles.values()) {
            circle.update(deltaTime);
        }
    }

    public void renderAll(PoseStack poseStack, MultiBufferSource buffer, float partialTicks) {
        for (MagicCircle circle : activeCircles.values()) {
            circle.render(poseStack, buffer, partialTicks);
        }
    }

    public void registerTemplate(String name, MagicCircle circle) {
        circleTemplates.put(name, circle);
    }

    public Set<String> getTemplateNames() {
        return circleTemplates.keySet();
    }

    public Set<String> getActiveCircleIds() {
        return activeCircles.keySet();
    }

    private MagicCircle copyCircle(MagicCircle template, Vec3 newPosition) {
        // This is a simplified copy - in a full implementation,
        // you'd want to deep copy all elements and their properties
        MagicCircle copy = new MagicCircle(newPosition);
        // For now, we'll create a new instance from the same template type
        // In a real implementation, you'd implement proper cloning
        return template;
    }
    //==============================================================================
    /*

     */
/**
 * Add a new magic circle to be managed
 *//*

    public static void addCircle(MagicCircle circle) {
        activeCircles.put(circle.getId(), circle);
        synchronized (renderList) {
            renderList.add(circle);
        }
    }

    */
/**
 * Remove a magic circle by ID
 *//*

    public static void removeCircle(UUID id) {
        MagicCircle removed = activeCircles.remove(id);
        if (removed != null) {
            synchronized (renderList) {
                renderList.remove(removed);
            }
        }
    }

    */
/**
 * Get a magic circle by ID
 *//*

    public static MagicCircle getCircle(UUID id) {
        return activeCircles.get(id);
    }

    */
/**
 * Clear all magic circles
 *//*

    public static void clearAll() {
        activeCircles.clear();
        synchronized (renderList) {
            renderList.clear();
        }
    }

    */
/**
 * Update all active magic circles
 *//*

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

    */
/**
 * Render all active magic circles
 *//*

    public static void renderAll(PoseStack poseStack, MultiBufferSource bufferSource) {
        synchronized (renderList) {
            for (MagicCircle circle : renderList) {
                circle.render(poseStack, bufferSource);
            }
        }
    }

    */
/**
 * Get the number of active circles
 *//*

    public static int getActiveCount() {
        return activeCircles.size();
    }

    */
/**
 * Check if a circle with the given ID exists
 *//*

    public static boolean hasCircle(UUID id) {
        return activeCircles.containsKey(id);
    }

    */
/**
 * Get all active circle IDs
 *//*

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

*/
}