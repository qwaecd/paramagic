package com.qwaecd.paramagic.data.para.converter.api;

import com.qwaecd.paramagic.data.para.converter.ConversionException;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.feature.MagicCircle;

public interface ParaDataConverter {
    /**
     * Converts a complete ParaData to a renderable MagicCircle.<br>
     * 将完整的ParaData转换为可渲染的MagicCircle。
     *
     * @param paraData The magic circle data to convert.<br> 要转换的魔法阵数据。
     * @return The renderable magic circle.<br> 可渲染的魔法阵。
     * @throws ConversionException if conversion fails.<br> 如果转换失败则抛出转换异常。
     */
    MagicCircle convert(ParaData paraData) throws ConversionException;

    /**
     * Checks if all components in the ParaData can be converted.<br>
     * 检查ParaData中的所有组件是否都可以被转换。
     *
     * @param paraData The data to validate.<br> 要验证的数据。
     * @return true if all components are supported.<br> 如果所有组件都被支持则返回true。
     */
    boolean canConvert(ParaData paraData);
}
