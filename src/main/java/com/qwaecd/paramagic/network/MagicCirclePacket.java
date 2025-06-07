package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.client.ClientSpellScheduler;
import com.qwaecd.paramagic.client.renderer.MagicCircleManager;
import com.qwaecd.paramagic.elements.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector2f;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class MagicCirclePacket {
    private final Vec3 position;
    private final float totalTime;
    private final List<Element> elements;


    public MagicCirclePacket(Vec3 position, float totalTime, List<Element> elements){
        this.position = position;
        this.totalTime = totalTime;
        this.elements = elements;
    }

    public static void encode(MagicCirclePacket packet, FriendlyByteBuf buf){
        buf.writeDouble(packet.position.x);
        buf.writeDouble(packet.position.y);
        buf.writeDouble(packet.position.z);

        buf.writeFloat(packet.totalTime);

        buf.writeInt(packet.elements.size());

        for (Element e : packet.elements){
            writeElement(buf, e);
        }
    }

    public static MagicCirclePacket decode(FriendlyByteBuf buf){
        MagicCirclePacket magicCirclePacket;
        Vec3 position = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        float totalTime = buf.readFloat();
        int size = buf.readInt();
        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < size; i++){
            elements.add(readElement(buf));
        }
        magicCirclePacket = new MagicCirclePacket(position, totalTime, elements);
        return magicCirclePacket;
    }

    public static void handle(MagicCirclePacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            MagicCircle magicCircle = new MagicCircle(packet.position, packet.elements);
            MagicCircleManager.getInstance().createCircle("custom", magicCircle);
        });
        ctx.get().setPacketHandled(true);
    }

    public Vec3 getPosition() {
        return position;
    }

    private static void writeElement(FriendlyByteBuf buf, Element element){
        switch (element.getElementType()){
            case CIRCLE -> {
                BufHelper.writeCircle(buf, element);}
            case GROUP -> {
                BufHelper.writeGroup(buf, element);}
            case LINE -> {
                BufHelper.writeLine(buf, element);}
            case PARTICLE -> {
                BufHelper.writeParticle(buf, element);}
            case RUNE -> {
                BufHelper.writeRune(buf, element);}
            case TEXT -> {
                BufHelper.writeText(buf, element);}
        }
    }

    private static Element readElement(FriendlyByteBuf buf){
        int type = buf.readInt();
        switch (type){
            case 0 -> {
                return BufHelper.readCircle(buf);}
            case 1 -> {
                return BufHelper.readGroup(buf);}
            case 2 -> {
                return BufHelper.readLine(buf);}
            case 3 -> {
                return BufHelper.readParticle(buf);}
            case 4 -> {
                return BufHelper.readRune(buf);}
            case 5 -> {
                return BufHelper.readText(buf);}
            default -> {
                throw new IllegalArgumentException("Invalid element type: " + type);}
        }
    }


    private static class BufHelper {
        static CircleElement readCircle(FriendlyByteBuf buf){
            return new CircleElement(buf.readFloat(), buf.readFloat());
        }
        static void writeCircle(FriendlyByteBuf buf, Element element){
            buf.writeInt(0);
            CircleElement circleElement = (CircleElement) element;
            buf.writeFloat(circleElement.getRadius());
            buf.writeFloat(circleElement.getThickness());
        }
        static GroupElement readGroup(FriendlyByteBuf buf) {
            return new GroupElement();
        }
        static void writeGroup(FriendlyByteBuf buf, Element element){
            buf.writeInt(1);
            // TODO: 检查会不会栈溢出
//            writeElement(buf, element);
        }

        static LineElement readLine(FriendlyByteBuf buf){
            return new LineElement(
                    new Vector2f(buf.readFloat(), buf.readFloat()),
                    new Vector2f(buf.readFloat(), buf.readFloat()),
                    buf.readFloat());
        }
        static void writeLine(FriendlyByteBuf buf, Element element){
            buf.writeInt(2);
            LineElement lineElement = (LineElement) element;
            buf.writeFloat(lineElement.getStart().x());
            buf.writeFloat(lineElement.getStart().y());
            buf.writeFloat(lineElement.getEnd().x());
            buf.writeFloat(lineElement.getEnd().y());
            buf.writeFloat(lineElement.getThickness());
        }

        static ParticleElement readParticle(FriendlyByteBuf buf){
            return new ParticleElement(
                    // TODO: 这里需要编码具体粒子类型
                    ParticleTypes.AMBIENT_ENTITY_EFFECT,
                    buf.readInt(),
                    buf.readFloat(),
                    buf.readInt(),
                    buf.readBoolean());
        }
        static void writeParticle(FriendlyByteBuf buf, Element element){
            buf.writeInt(3);
            ParticleElement particleElement = (ParticleElement) element;
            buf.writeInt(particleElement.getCount());
            buf.writeFloat(particleElement.getSpeed());
            buf.writeInt(particleElement.getLifetime());
            buf.writeBoolean(particleElement.isLoop());
        }
        static RuneElement readRune(FriendlyByteBuf buf){
            return new RuneElement(
                    buf.readResourceLocation(),
                    buf.readFloat());
        }
        static void writeRune(FriendlyByteBuf buf, Element element){
            buf.writeInt(4);
            RuneElement runeElement = (RuneElement) element;
            buf.writeResourceLocation(runeElement.getResourceLocation());
            buf.writeFloat(runeElement.getSize());
        }
        static TextElement readText(FriendlyByteBuf buf){
            return new TextElement(
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readInt());
        }
        static void writeText(FriendlyByteBuf buf, Element element){
            buf.writeInt(5);
            TextElement textElement = (TextElement) element;
            buf.writeUtf(textElement.getText());
            buf.writeUtf(textElement.getFontName());
            buf.writeInt(textElement.getFontSize());
        }
    }
}
