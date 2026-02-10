package com.qwaecd.paramagic.data.para.struct;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.data.para.ParaStructureFrozenException;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.platform.Services;
import lombok.Getter;
import lombok.Setter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

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
    /**
     * 子组件列表, 该列表在构造完成后 (分配组件 id 时) 会被冻结, 防止后续修改.<br>
     * List of child components, which will be frozen after construction (when component ids are assigned) to prevent subsequent modification.
     */
    private List<ParaComponentData> children;
    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale;

    public Vector4f color;
    @Getter
    @Setter
    protected float intensity;

    private boolean frozenStructure = false;

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
    public final void addChild(ParaComponentData child) {
        if (child == null) {
            throw new NullPointerException("Cannot add null child to ParaComponentData");
        }

        if (this.frozenStructure) {
            throw new IllegalStateException("Cannot add child to ParaComponentData after structure is frozen.");
        }
        this.children.add(child);
    }

    /**
     * 冻结结构, 防止后续修改子组件列表. 该函数在 ParaData 分配子类 id 时被调用, 保证 ParaData 构造完成后结构已被冻结.<br>
     * Freeze the structure to prevent subsequent modification of the child component list. This function is called when ParaData assigns subclass ids to ensure that ParaData is frozen after construction.
     * @see com.qwaecd.paramagic.data.para.struct.ParaData
     */
    final void freeze() {
        if (!this.frozenStructure) {
            this.children = List.copyOf(this.children);
            this.frozenStructure = true;
        }
    }

    public final boolean isStructureFrozen() {
        return this.frozenStructure;
    }

    /**
     * 获取不可变的子组件列表, 如果结构未被冻结则抛出异常.<br>
     * Get an immutable list of child components. If the structure is not frozen, an exception is thrown.
     * @return 不可变的子组件列表 <br> Immutable list of child components
     * @throws ParaStructureFrozenException 如果结构未被冻结 / if the structure is not frozen
     */
    public final List<ParaComponentData> getChildren() throws ParaStructureFrozenException {
        if (!this.frozenStructure) {
            throw new ParaStructureFrozenException();
        }

        return this.children;
    }

    /**
     * 获取当前子组件列表的快照, 如果结构已被冻结则直接返回不可变列表, 否则返回子组件列表的一个不可变拷贝.<br>
     * Get a snapshot of the current child component list. If the structure is frozen, return an immutable list directly; otherwise, return an immutable copy of the child component list.
     */
    public final List<ParaComponentData> getCurrentChildren() {
        if (this.frozenStructure) {
            return this.children;
        } else {
            return List.copyOf(this.children);
        }
    }

    public abstract int getComponentType();

    /**
     * 写入基础属性.<br>
     * 写入 type 的数据在子类的 write 方法的开头完成.
     */
    protected void writeBase(DataCodec codec) {
        // 写入 type 的数据在子类的 write 方法的开头完成
//        codec.writeInt("type", this.getComponentType());

        codec.writeStringNullable("name", this.name);
        codec.writeFloatArray("pos",   new float[]{this.position.x, this.position.y, this.position.z});
        codec.writeFloatArray("rot",   new float[]{this.rotation.x, this.rotation.y, this.rotation.z, this.rotation.w});
        codec.writeFloatArray("scale", new float[]{this.scale.x, this.scale.y, this.scale.z});
        codec.writeFloatArray("color", new float[]{this.color.x, this.color.y, this.color.z, this.color.w});
        codec.writeFloat("intensity", this.intensity);

//        final int childCount = this.children.size();
//        codec.writeInt("child", childCount);
//        for (int i = 0; i < childCount; i++) {
//            ParaComponentData child = this.children.get(i);
//            codec.writeObject("ch_" + i, child);
//        }
        ParaComponentData[] childrenArray = this.children.toArray(new ParaComponentData[0]);
        codec.writeObjectArray("children", childrenArray);
    }

    protected void readBase(DataCodec codec) {
        this.name = codec.readStringNullable("name");
        float[] pos = codec.readFloatArray("pos");
        this.position.set(pos[0], pos[1], pos[2]);
        float[] rot = codec.readFloatArray("rot");
        this.rotation.set(rot[0], rot[1], rot[2], rot[3]);
        float[] scale = codec.readFloatArray("scale");
        this.scale.set(scale[0], scale[1], scale[2]);
        float[] color = codec.readFloatArray("color");
        this.color.set(color[0], color[1], color[2], color[3]);
        this.intensity = codec.readFloat("intensity");

//        final int childCount = codec.readInt("child");
//        for (int i = 0; i < childCount; i++) {
//            ParaComponentData child = codec.readObject("ch_" + i, ParaComponentData::fromCodecBase);
//            this.children.add(child);
//        }
        IDataSerializable[] childrenArr = codec.readObjectArray("children", ParaComponentData::fromCodecBase);
        for (IDataSerializable child : childrenArr) {
            this.children.add((ParaComponentData) child);
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
