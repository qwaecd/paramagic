package com.qwaecd.paramagic.data.para.struct;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.platform.Services;
import lombok.Getter;
import lombok.Setter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Also can be called MagicCircleComponentData. Base class for all magic circle element components.
 * <p>
 * 也可以叫 MagicCircleComponentData，是所有法阵元素组件的基类.
 */
public abstract class ParaComponentData implements IDataSerializable {
    private static final Map<Integer, Function<DataCodec, ? extends ParaComponentData>> REGISTRY = new HashMap<>();
    /**
     * 单个ParaData下的树路径，也可作为唯一ID, 该值非 null.
     */
    @Getter
    protected String componentId;
    /**
     * 可以任意指定的组件名称，需要手动确保唯一性。
     */
    @Getter
    @Setter
    @Nullable
    protected String name;
    public final List<ParaComponentData> children;
    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale;

    public Vector4f color;
    @Getter
    @Setter
    protected float intensity;

    protected ParaComponentData() {
        this.children = new ArrayList<>();

        this.position = new Vector3f(0.0f, 0.0f, 0.0f);
        this.rotation = new Quaternionf().identity();
        this.scale = new Vector3f(1.0f, 1.0f, 1.0f);

        this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.intensity = 0.0f;
    }

    public static void register(int componentType, Function<DataCodec, ? extends ParaComponentData> constructor) {
        if (REGISTRY.containsKey(componentType)) {
            String message = "ParaComponentData type " + componentType + " is already registered.";
            if (Services.PLATFORM.isDevelopmentEnvironment()) {
                throw new IllegalStateException(message);
            }
            Paramagic.LOG.error(message);
            return;
        }
        REGISTRY.put(componentType, constructor);
    }
    public void addChild(ParaComponentData child) {
        if (child == null) {
            throw new NullPointerException("Cannot add null child to ParaComponentData");
        }
        this.children.add(child);
    }

    public abstract int getComponentType();

    /**
     * 写入基础属性.<br>
     * 写入 type 的数据在子类的 write 方法的开头完成.
     */
    protected void writeBase(DataCodec codec) {
        // 写入 type 的数据在子类的 write 方法的开头完成
//        codec.writeInt("type", this.getComponentType());

        if (this.name == null || this.name.isEmpty()) {
            codec.writeBoolean("hasName", false);
        } else {
            codec.writeBoolean("hasName", true);
            codec.writeString("name", this.name);
        }
        codec.writeFloatArray("pos",   new float[]{this.position.x, this.position.y, this.position.z});
        codec.writeFloatArray("rot",   new float[]{this.rotation.x, this.rotation.y, this.rotation.z, this.rotation.w});
        codec.writeFloatArray("scale", new float[]{this.scale.x, this.scale.y, this.scale.z});
        codec.writeFloatArray("color", new float[]{this.color.x, this.color.y, this.color.z, this.color.w});
        codec.writeFloat("intensity", this.intensity);

        final int childCount = this.children.size();
        codec.writeInt("child", childCount);
        for (int i = 0; i < childCount; i++) {
            ParaComponentData child = this.children.get(i);
            codec.writeObject("ch_" + i, child);
        }
    }

    protected void readBase(DataCodec codec) {
        if (codec.readBoolean("hasName")) {
            this.name = codec.readString("name");
        } else {
            this.name = null;
        }
        float[] pos = codec.readFloatArray("pos");
        this.position.set(pos[0], pos[1], pos[2]);
        float[] rot = codec.readFloatArray("rot");
        this.rotation.set(rot[0], rot[1], rot[2], rot[3]);
        float[] scale = codec.readFloatArray("scale");
        this.scale.set(scale[0], scale[1], scale[2]);
        float[] color = codec.readFloatArray("color");
        this.color.set(color[0], color[1], color[2], color[3]);
        this.intensity = codec.readFloat("intensity");

        final int childCount = codec.readInt("child");
        for (int i = 0; i < childCount; i++) {
            ParaComponentData child = codec.readObject("ch_" + i, ParaComponentData::fromCodecBase);
            this.children.add(child);
        }
    }

    public static ParaComponentData fromCodecBase(DataCodec codec) {
        final int componentType = codec.readInt("type");
        Function<DataCodec, ? extends ParaComponentData> dataCodecFunction = REGISTRY.get(componentType);
        if (dataCodecFunction != null) {
            return dataCodecFunction.apply(codec);
        }
        throw new UnsupportedOperationException("Unsupported ParaComponentData type: " + componentType);
    }
}
