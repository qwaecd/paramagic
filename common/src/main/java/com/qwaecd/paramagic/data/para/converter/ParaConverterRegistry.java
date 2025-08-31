package com.qwaecd.paramagic.data.para.converter;

import com.qwaecd.paramagic.data.para.ParaComponentData;
import com.qwaecd.paramagic.data.para.converter.impl.RingParaConverter;
import com.qwaecd.paramagic.data.para.converter.impl.VoidParaConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for ParaConverter instances, managing the mapping between ParaComponentData types and their converters.<br>
 * ParaConverter注册表，管理ParaComponentData类型与其转换器之间的映射关系。
 */
public class ParaConverterRegistry {
    private final Map<Class<? extends ParaComponentData>, ParaComponentConverter<?>> converterRegistry;

    public ParaConverterRegistry() {
        this.converterRegistry = new HashMap<>();
        registerDefaultConversions();
    }

    private void registerDefaultConversions() {
         register(new VoidParaConverter());
         register(new RingParaConverter());
    }

    /**
     * Registers a ParaConverter for a specific ParaComponentData type.<br>
     * 为特定的ParaComponentData类型注册一个ParaConverter。
     *
     * @param converter The converter to register.<br> 要注册的转换器。
     * @param <T> The type of ParaComponentData handled by the converter.<br> 该转换器处理的ParaComponentData类型。
     * @see ParaComponentData
     */
    public <T extends ParaComponentData> void register(ParaComponentConverter<T> converter) {
        this.converterRegistry.put(converter.getDataType(), converter);
    }

    /**
     * Gets the ParaConverter for the specified ParaComponentData type.<br>
     * 获取指定ParaComponentData类型的ParaConverter。
     *
     * @param dataType The data type.<br> 数据类型。
     * @return The corresponding converter, or Optional.empty() if not found.<br> 对应的转换器，如果未找到则返回Optional.empty()。
     */
    @SuppressWarnings("unchecked")
    public <T extends ParaComponentData> Optional<ParaComponentConverter<T>> getConverter(Class<T> dataType) {
        ParaComponentConverter<?> converter = this.converterRegistry.get(dataType);
        return Optional.ofNullable((ParaComponentConverter<T>) converter);
    }

    /**
     * Gets the ParaConverter for the specified ParaComponentData instance.<br>
     * 获取指定ParaComponentData实例的ParaConverter。
     *
     * @param data The ParaComponentData instance.<br> ParaComponentData实例。
     * @return The corresponding converter, or Optional.empty() if not found.<br> 对应的转换器，如果未找到则返回Optional.empty()。
     */
    public Optional<ParaComponentConverter<? extends ParaComponentData>> getConverter(ParaComponentData data) {
        return Optional.ofNullable(this.converterRegistry.get(data.getClass()));
    }

    /**
     * Checks if a specific ParaComponentData type is supported.<br>
     * 检查是否支持某种ParaComponentData类型。
     *
     * @param dataType The data type to check.<br> 要检查的数据类型。
     * @return true if supported, false otherwise.<br> 如果支持则返回true，否则返回false。
     */
    public boolean isSupported(Class<? extends ParaComponentData> dataType) {
        return converterRegistry.containsKey(dataType);
    }

    /**
     * Checks if the type of a specific ParaComponentData instance is supported.<br>
     * 检查是否支持某个ParaComponentData实例的类型。
     *
     * @param data The ParaComponentData instance.<br> ParaComponentData实例。
     * @return true if supported, false otherwise.<br> 如果支持则返回true，否则返回false。
     */
    public boolean isSupported(ParaComponentData data) {
        return converterRegistry.containsKey(data.getClass());
    }
}
