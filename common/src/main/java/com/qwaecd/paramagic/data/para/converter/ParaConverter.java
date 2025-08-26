package com.qwaecd.paramagic.data.para.converter;

import com.qwaecd.paramagic.data.para.ParaComponentData;
import com.qwaecd.paramagic.feature.MagicNode;

/**
 * Converter interface for ParaComponentData to MagicNode.<br>
 * ParaComponentData到MagicNode的转换器接口。
 *
 * @param <T> The specific subtype of ParaComponentData.<br>
 *            具体的ParaComponentData子类型。
 */
public interface ParaConverter<T extends ParaComponentData> {

    /**
     * Converts the specified ParaComponentData to a MagicNode.<br>
     * 将指定类型的ParaComponentData转换为MagicNode。
     *
     * @param data The data to be converted.<br>
     *             要转换的数据。
     * @return The converted MagicNode. Maybe an empty node containing only transformation if no geometry is needed.<br>
     *         转换后的MagicNode，如果无需几何体则可能为仅包含变换的空节点。
     */
    MagicNode convert(T data);

    /**
     * Returns the ParaComponentData type handled by this converter.<br>
     * 返回此转换器负责处理的ParaComponentData类型。
     *
     * @return The corresponding Class object.<br>
     *         对应的Class对象。
     * @see ParaComponentData
     */
    Class<T> getDataType();
}