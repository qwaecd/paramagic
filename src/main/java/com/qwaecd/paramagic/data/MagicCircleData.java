package com.qwaecd.paramagic.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

import static com.qwaecd.paramagic.Paramagic.MODID;

/**
 * Data record for magic circle parameters
 * Used for network synchronization and serialization
 */
@Deprecated
public record MagicCircleData(
        UUID id,
        Vec3 position,
        float yaw,
        float pitch,
        ResourceLocation texture,
        float radius,
        String spellType,
        int duration,
        float intensity
) {

    /**
     * Create a default magic circle data
     */
    public static MagicCircleData createDefault(Vec3 position) {
        return new MagicCircleData(
                UUID.randomUUID(),
                position,
                0.0f,
                0.0f,
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/magic_circle/default.png"),
                3.0f,
                "generic",
                100, // 5 seconds at 20 ticks per second
                1.0f
        );
    }

    /**
     * Create magic circle data for a specific spell type
     */
    public static MagicCircleData createForSpell(Vec3 position, String spellType, float radius) {
        ResourceLocation texture = switch (spellType.toLowerCase()) {
            case "fire" -> ResourceLocation.fromNamespaceAndPath(MODID, "textures/magic_circle/fire.png");
            case "ice" -> ResourceLocation.fromNamespaceAndPath(MODID, "textures/magic_circle/ice.png");
            case "lightning" -> ResourceLocation.fromNamespaceAndPath(MODID, "textures/magic_circle/lightning.png");
            case "healing" -> ResourceLocation.fromNamespaceAndPath(MODID, "textures/magic_circle/healing.png");
            case "protection" -> ResourceLocation.fromNamespaceAndPath(MODID, "textures/magic_circle/protection.png");
            default -> ResourceLocation.fromNamespaceAndPath(MODID, "textures/magic_circle/default.png");
        };

        int duration = switch (spellType.toLowerCase()) {
            case "fire", "ice", "lightning" -> 60; // 3 seconds
            case "healing" -> 80; // 4 seconds
            case "protection" -> 120; // 6 seconds
            default -> 100; // 5 seconds
        };

        float intensity = switch (spellType.toLowerCase()) {
            case "fire", "lightning" -> 1.5f;
            case "ice" -> 1.2f;
            case "healing" -> 0.8f;
            case "protection" -> 1.0f;
            default -> 1.0f;
        };

        return new MagicCircleData(
                UUID.randomUUID(),
                position,
                0.0f,
                0.0f,
                texture,
                radius,
                spellType,
                duration,
                intensity
        );
    }

    /**
     * Create a copy with different position
     */
    public MagicCircleData withPosition(Vec3 newPosition) {
        return new MagicCircleData(
                this.id,
                newPosition,
                this.yaw,
                this.pitch,
                this.texture,
                this.radius,
                this.spellType,
                this.duration,
                this.intensity
        );
    }

    /**
     * Create a copy with different rotation
     */
    public MagicCircleData withRotation(float newYaw, float newPitch) {
        return new MagicCircleData(
                this.id,
                this.position,
                newYaw,
                newPitch,
                this.texture,
                this.radius,
                this.spellType,
                this.duration,
                this.intensity
        );
    }

    /**
     * Create a copy with different scale
     */
    public MagicCircleData withRadius(float newRadius) {
        return new MagicCircleData(
                this.id,
                this.position,
                this.yaw,
                this.pitch,
                this.texture,
                newRadius,
                this.spellType,
                this.duration,
                this.intensity
        );
    }

    /**
     * Validate the data
     */
    public boolean isValid() {
        return id != null &&
                position != null &&
                texture != null &&
                radius > 0 &&
                duration > 0 &&
                intensity > 0 &&
                spellType != null && !spellType.isEmpty();
    }
}
