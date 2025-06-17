package com.qwaecd.paramagic.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.qwaecd.paramagic.elements.MagicCircle;
import com.qwaecd.paramagic.feature.MagicCircleExamples;
import com.qwaecd.paramagic.feature.dynamic.texture.DynamicTestTexture;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

import static com.qwaecd.paramagic.Paramagic.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = MODID)
public class MagicCircleManager {
    private static MagicCircleManager instance;
    private final Map<String, MagicCircle> activeCircles;
    private final Map<String, MagicCircle> circleTemplates;
    private final List<DynamicTestTexture> dynamicTextures;

    private MagicCircleManager() {
        this.activeCircles = new HashMap<>();
        this.circleTemplates = new HashMap<>();
        this.dynamicTextures = new ArrayList<>();
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
    private Quaternionf createBillboardRotation(Vector3f look, Vector3f up, Vector3f right) {
        // 创建正交旋转矩阵（3x3）
        Matrix3f rotationMatrix = new Matrix3f();

        // 设置矩阵列向量（Minecraft 使用列主序）
        rotationMatrix.m00 = right.x;  // 右向量 = 第一列
        rotationMatrix.m01 = right.y;
        rotationMatrix.m02 = right.z;

        rotationMatrix.m10 = up.x;    // 上向量 = 第二列
        rotationMatrix.m11 = up.y;
        rotationMatrix.m12 = up.z;

        rotationMatrix.m20 = look.x;  // 观察向量 = 第三列
        rotationMatrix.m21 = look.y;
        rotationMatrix.m22 = look.z;

        // 将矩阵转换为四元数
        Quaternionf quat = new Quaternionf();
        rotationMatrix.getNormalizedRotation(quat);

        return quat;
    }

    public void renderAll(PoseStack poseStack, MultiBufferSource buffer, float partialTicks) {
        for (MagicCircle circle : activeCircles.values()) {
            circle.render(poseStack, buffer, partialTicks);
        }
        for (DynamicTestTexture texture : dynamicTextures) {
            if (texture != null) {
                poseStack.pushPose();
                // Set up the pose stack for rendering
                Camera mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
                Vector3f lookVector = mainCamera.getLookVector();
                Vector3f upVector = mainCamera.getUpVector();
                Vector3f leftVector = mainCamera.getLeftVector();

                float scale = org.joml.Math.sin(System.currentTimeMillis() % 3000L / 3000f * (float)Math.PI) * 0.5f + 0.5f;
                scale = 0.03f;
                poseStack.translate(lookVector.x, lookVector.y, lookVector.z);
                poseStack.scale(scale, scale, scale);
//                poseStack.mulPose(mainCamera.rotation());
                poseStack.mulPose(createBillboardRotation(lookVector, upVector, leftVector));
                // Bind the texture for rendering
                ResourceLocation magicCircleRL = texture.getMagicCircleRL();
                Minecraft.getInstance().getTextureManager().bindForSetup(magicCircleRL);

                // Bind the texture for rendering
                RenderSystem.setShaderTexture(0, magicCircleRL);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.depthMask(Minecraft.useShaderTransparency());

                Matrix4f matrix = poseStack.last().pose();
                Matrix3f normalMatrix = poseStack.last().normal();
                // Draw a square for the magic circle
                BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                // 四个顶点：位置 + 纹理 UV
//                bufferbuilder.vertex(matrix, 0, 0, 0).uv(0f, 0f).endVertex();
//                bufferbuilder.vertex(matrix, 0, 16, 0).uv(1f, 0f).endVertex();
//                bufferbuilder.vertex(matrix, 16, 16, 0).uv(1f, 1f).endVertex();
//                bufferbuilder.vertex(matrix, 16, 0, 0).uv(0f, 1f).endVertex();

                bufferbuilder.vertex(matrix, -8, 8, -8).uv(0f, 0f).endVertex();
                bufferbuilder.vertex(matrix, 8, 8, -8).uv(1f, 0f).endVertex();
                bufferbuilder.vertex(matrix, 8, -8, -8).uv(1f, 1f).endVertex();
                bufferbuilder.vertex(matrix, -8, -8, -8).uv(0f, 1f).endVertex();

//                bufferbuilder.vertex(matrix, 0, 0, 0).uv(0f, 0f).endVertex();
//                bufferbuilder.vertex(matrix, 0, -16, 0).uv(1f, 0f).endVertex();
//                bufferbuilder.vertex(matrix, -16, -16, 0).uv(1f, 1f).endVertex();
//                bufferbuilder.vertex(matrix, -16, 0, 0).uv(0f, 1f).endVertex();
                BufferUploader.drawWithShader(bufferbuilder.end());

                // Render circle outline using lines
                VertexConsumer consumer = buffer.getBuffer(RenderType.lines());
                int segments = 32;
                float angleStep = 2 * (float)Math.PI / segments;
                float radius = 8f;
                for (int i = 0; i < segments; i++) {
                    float angle1 = i * angleStep;
                    float angle2 = (i + 1) * angleStep;

                    float x1 = (float) Math.cos(angle1) * radius;
                    float y1 = (float) Math.sin(angle1) * radius;
                    float x2 = (float) Math.cos(angle2) * radius;
                    float y2 = (float) Math.sin(angle2) * radius;
                    // Add normal vectors for lines
                    consumer.vertex(matrix, x1, y1, 0).color(255, 255, 255, 255).normal(normalMatrix, 0, 0, 1).endVertex();
                    consumer.vertex(matrix, x2, y2, 0).color(255, 255, 255, 255).normal(normalMatrix, 0, 0, 1).endVertex();
                }
                poseStack.popPose();
                RenderSystem.depthMask(true);
            }

        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        // Clear all circles when leaving a world to prevent memory leaks
        synchronized (instance.activeCircles) {
            if (instance != null) {
                instance.activeCircles.clear();
                for (DynamicTestTexture dynamicTexture : instance.dynamicTextures) {
                    if (dynamicTexture != null && dynamicTexture.getMagicCircleTexture() != null) {
                        Minecraft.getInstance().getTextureManager().release(dynamicTexture.getMagicCircleRL());
                    }
                }

            }
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

    public List<DynamicTestTexture> getDynamicTextures() {
        return dynamicTextures;
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