package com.qwaecd.paramagic.thaumaturgy.spelltree;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.NBTCodec;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SpellNodeData implements IDataSerializable {
    private static final String NODE_ID_KEY = "nodeId";
    private static final String OPERATOR_ID_KEY = "operatorId";
    private static final String CHILDREN_KEY = "children";
    private static final String CHILD_COUNT_KEY = "childCount";
    private static final String CHILD_KEY = "child";

    @Nonnull
    private final String nodeId;
    @Nullable
    private ParaOpId operatorId;
    @Nonnull
    private final List<SpellNodeData> children;

    public SpellNodeData(@Nonnull String nodeId) {
        this(nodeId, null, List.of());
    }

    public SpellNodeData(
            @Nonnull String nodeId,
            @Nullable ParaOpId operatorId,
            @Nonnull List<SpellNodeData> children
    ) {
        this.nodeId = Objects.requireNonNull(nodeId, "nodeId");
        this.operatorId = operatorId;
        this.children = new ArrayList<>(children);
    }

    @Nonnull
    public String getNodeId() {
        return this.nodeId;
    }

    @Nullable
    public ParaOpId getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(@Nullable ParaOpId operatorId) {
        this.operatorId = operatorId;
    }

    @Nonnull
    public List<SpellNodeData> getChildren() {
        return this.children;
    }

    public void addChild(int index, @Nonnull SpellNodeData child) {
        int targetIndex = Math.max(0, Math.min(index, this.children.size()));
        this.children.add(targetIndex, child);
    }

    public boolean removeChild(@Nonnull SpellNodeData child) {
        return this.children.remove(child);
    }

    public void forEachSubtreeNode(@Nonnull Consumer<SpellNodeData> consumer) {
        consumer.accept(this);
        for (SpellNodeData child : this.children) {
            child.forEachSubtreeNode(consumer);
        }
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeString(NODE_ID_KEY, this.nodeId);
        codec.writeObjectNullable(OPERATOR_ID_KEY, this.operatorId);

        if (codec instanceof NBTCodec nbtCodec) {
            ListTag childrenTag = new ListTag();
            for (SpellNodeData child : this.children) {
                CompoundTag childTag = new CompoundTag();
                child.write(new NBTCodec(childTag));
                childrenTag.add(childTag);
            }
            nbtCodec.getTag().put(CHILDREN_KEY, childrenTag);
            return;
        }

        codec.writeInt(CHILD_COUNT_KEY, this.children.size());
        for (SpellNodeData child : this.children) {
            codec.writeObject(CHILD_KEY, child);
        }
    }

    @Nonnull
    public static SpellNodeData fromCodec(DataCodec codec) {
        String nodeId = codec.readString(NODE_ID_KEY);
        ParaOpId operatorId = codec.readObjectNullable(OPERATOR_ID_KEY, ParaOpId::fromCodec);
        List<SpellNodeData> children = new ArrayList<>();

        if (codec instanceof NBTCodec nbtCodec) {
            ListTag childrenTag = nbtCodec.getTag().getList(CHILDREN_KEY, 10);
            for (int i = 0; i < childrenTag.size(); i++) {
                children.add(SpellNodeData.fromCodec(new NBTCodec(childrenTag.getCompound(i))));
            }
            return new SpellNodeData(nodeId, operatorId, children);
        }

        int childCount = codec.readInt(CHILD_COUNT_KEY);
        for (int i = 0; i < childCount; i++) {
            children.add(codec.readObject(CHILD_KEY, SpellNodeData::fromCodec));
        }
        return new SpellNodeData(nodeId, operatorId, children);
    }
}
