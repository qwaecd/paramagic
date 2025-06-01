package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.client.ClientSpellScheduler;
import com.qwaecd.paramagic.client.renderer.MagicCircle;
import com.qwaecd.paramagic.client.renderer.MagicCircleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static com.qwaecd.paramagic.Paramagic.MODID;

public class SpellExecutionPacket {
    private final String magicMapId;
    private final BlockPos center;
    private final Map<String, Object> parameters;

    // Magic circle specific data
    private final UUID circleId;
    private final float yaw;//偏航角
    private final float pitch;//俯仰角
    private final ResourceLocation texture;
    private final float radius;//半径

    public SpellExecutionPacket(String magicMapId, BlockPos center, Map<String, Object> parameters) {
        this.magicMapId = magicMapId;
        this.center = center;
        this.parameters = parameters;
        // Generate magic circle data based on spell
        this.circleId = UUID.randomUUID();
        this.yaw = parameters.containsKey("yaw") ? (Float) parameters.get("yaw") : 0.0f;
        this.pitch = parameters.containsKey("pitch") ? (Float) parameters.get("pitch") : 0.0f;
        this.texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/magic_circle/" +
                (parameters.containsKey("circle_type") ? parameters.get("circle_type") : "default") + ".png");
        this.radius = parameters.containsKey("radius") ? (Float) parameters.get("radius") : 3.0f;
    }

    public SpellExecutionPacket(String magicMapId, BlockPos center, Map<String, Object> parameters,
                                UUID circleId, float yaw, float pitch, ResourceLocation texture, float radius) {
        this.magicMapId = magicMapId;
        this.center = center;
        this.parameters = parameters;
        // Generate magic circle data based on spell
        this.circleId = UUID.randomUUID();
        this.yaw = parameters.containsKey("yaw") ? (Float) parameters.get("yaw") : 0.0f;
        this.pitch = parameters.containsKey("pitch") ? (Float) parameters.get("pitch") : 0.0f;
        this.texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/magic_circle/" +
                (parameters.containsKey("circle_type") ? parameters.get("circle_type") : "default") + ".png");
        this.radius = parameters.containsKey("radius") ? (Float) parameters.get("radius") : 3.0f;
    }

    public static void encode(SpellExecutionPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.magicMapId);
        buf.writeBlockPos(packet.center);
        buf.writeInt(packet.parameters.size());
        packet.parameters.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeUtf(value.toString());
        });

        buf.writeInt(packet.parameters.size());
        for (Map.Entry<String, Object> entry : packet.parameters.entrySet()) {
            buf.writeUtf(entry.getKey());
            // Simplified parameter encoding - extend as needed
            if (entry.getValue() instanceof String) {
                buf.writeByte(0);
                buf.writeUtf((String) entry.getValue());
            } else if (entry.getValue() instanceof Float) {
                buf.writeByte(1);
                buf.writeFloat((Float) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                buf.writeByte(2);
                buf.writeInt((Integer) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                buf.writeByte(3);
                buf.writeBoolean((Boolean) entry.getValue());
            }
        }
        // Write magic circle data
        buf.writeUUID(packet.circleId);
        buf.writeFloat(packet.yaw);
        buf.writeFloat(packet.pitch);
        buf.writeResourceLocation(packet.texture);
        buf.writeFloat(packet.radius);
    }

    public static SpellExecutionPacket decode(FriendlyByteBuf buf) {
        String magicMapId = buf.readUtf();
        BlockPos center = buf.readBlockPos();
        int paramCount = buf.readInt();
        Map<String, Object> parameters = new HashMap<>();
        for (int i = 0; i < paramCount; i++) {
            String key = buf.readUtf();
            byte type = buf.readByte();
            Object value;
            switch (type) {
                case 0: value = buf.readUtf(); break;
                case 1: value = buf.readFloat(); break;
                case 2: value = buf.readInt(); break;
                case 3: value = buf.readBoolean(); break;
                default: value = null;
            }
            if (value != null) {
                parameters.put(key, value);
            }
        }
        // Read magic circle data
        UUID circleId__ = buf.readUUID();
        float yaw = buf.readFloat();
        float pitch = buf.readFloat();
        ResourceLocation texture = buf.readResourceLocation();
        float radius = buf.readFloat();
        return new SpellExecutionPacket(magicMapId, center, parameters, circleId__, yaw, pitch, texture, radius);
    }

    public static void handle(SpellExecutionPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientSpellScheduler.scheduleExecution(packet.magicMapId, packet.center, packet.parameters);
        });
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            // Create and add magic circle on client side
            MagicCircle circle = new MagicCircle(
                    packet.circleId,
                    packet.center,
                    packet.yaw,
                    packet.pitch,
                    packet.texture,
                    packet.radius
            );
            MagicCircleManager.addCircle(circle);

            // Handle other spell effects here
            handleSpellEffect(packet);
        });
        ctx.get().setPacketHandled(true);
    }
    private static void handleSpellEffect(SpellExecutionPacket packet) {
        // Add particle effects, sounds, or other visual/audio feedback
        // This will be called on the client side

        // Example: Play sound based on spell type
        /*
        if (packet.spellId.contains("fire")) {
            // Play fire spell sound
            Minecraft.getInstance().level.playLocalSound(
                packet.position.x, packet.position.y, packet.position.z,
                SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER,
                1.0f, 1.0f, false
            );
        } else if (packet.spellId.contains("lightning")) {
            // Play lightning spell sound
            Minecraft.getInstance().level.playLocalSound(
                packet.position.x, packet.position.y, packet.position.z,
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.MASTER,
                1.0f, 1.0f, false
            );
        }
        */

        System.out.println("Spell executed: " + packet.magicMapId + " at " + packet.center);
    }

    // Getters
    public String getSpellId() { return magicMapId; }
    public Vec3 getPosition() { return center.getCenter(); }
    public Map<String, Object> getParameters() { return parameters; }
    public UUID getCircleId() { return circleId; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public ResourceLocation getTexture() { return texture; }
    public float getRadius() { return radius; }
}
