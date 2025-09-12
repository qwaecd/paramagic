package com.qwaecd.paramagic.data.para.converter;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.data.para.ConversionException;
import com.qwaecd.paramagic.data.para.ParaComponentData;
import com.qwaecd.paramagic.data.para.ParaData;
import com.qwaecd.paramagic.data.para.converter.impl.PolygonConverter;
import com.qwaecd.paramagic.data.para.converter.impl.RingParaConverter;
import com.qwaecd.paramagic.data.para.converter.impl.VoidParaConverter;
import com.qwaecd.paramagic.data.para.converter.parafactory.ParaAssetFactory;
import com.qwaecd.paramagic.feature.MagicCircle;
import com.qwaecd.paramagic.feature.MagicNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainParaDataConverter implements ParaDataConverter {
    @Getter
    private final ParaConverterRegistry converterRegistry;
    private final ParaAssetFactory assetFactory;

    public MainParaDataConverter() {
        this.converterRegistry = new ParaConverterRegistry();
        this.assetFactory = new ParaAssetFactory();
        registerAllConversions();
    }

    /**
     * Constructor with custom registry (for testing or customization).<br>
     * 使用自定义注册表的构造函数（用于测试或定制）。
     */
    public MainParaDataConverter(ParaConverterRegistry registry) {
        this.converterRegistry = registry;
        this.assetFactory = new ParaAssetFactory();
        registerAllConversions();
    }

    private void registerAllConversions() {
        this.converterRegistry.register(new VoidParaConverter(this.assetFactory));
        this.converterRegistry.register(new RingParaConverter(this.assetFactory));
        this.converterRegistry.register(new PolygonConverter(this.assetFactory));
    }
    @Override
    public MagicCircle convert(ParaData paraData) throws ConversionException {
        if (paraData == null) {
            throw new ConversionException("ParaData cannot be null");
        }

        Paramagic.LOG.debug("Converting ParaData with UUID: {}", paraData.circleUUID);

        MagicCircle magicCircle = new MagicCircle();

        if (paraData.rootComponent != null) {
            try {
                MagicNode rootNode = convertComponentRecursive(paraData.rootComponent, magicCircle);

                if (rootNode != null) {
                    magicCircle.addChild(rootNode);
                    Paramagic.LOG.debug("Successfully converted ParaData with {} registered nodes",
                            magicCircle.getRegisteredComponentIds().size());
                } else {
                    Paramagic.LOG.warn("Root component conversion resulted in null node");
                }

            } catch (Exception e) {
                throw new ConversionException("Failed to convert root component", e);
            }
        } else {
            Paramagic.LOG.warn("ParaData has null root component, creating empty MagicCircle");
        }

        return magicCircle;
    }
    @Override
    public boolean canConvert(ParaData paraData) {
        if (paraData == null || paraData.rootComponent == null) {
            return false;
        }

        List<String> unsupportedTypes = new ArrayList<>();
        boolean canConvert = validateComponentRecursive(paraData.rootComponent, unsupportedTypes);

        if (!canConvert) {
            Paramagic.LOG.warn("Cannot convert ParaData - unsupported types: {}", unsupportedTypes);
        }

        return canConvert;
    }

    /**
     * Recursively validates if all components can be converted.<br>
     * 递归验证是否所有组件都可以被转换。
     */
    private boolean validateComponentRecursive(ParaComponentData data, List<String> unsupportedTypes) {
        if (!converterRegistry.isSupported(data)) {
            unsupportedTypes.add(data.getClass().getSimpleName());
            return false;
        }

        for (ParaComponentData child : data.children) {
            if (!validateComponentRecursive(child, unsupportedTypes)) {
                return false;
            }
        }

        return true;
    }
    /**
     * Recursively converts ParaComponentData to MagicNode.<br>
     * 递归将ParaComponentData转换为MagicNode。
     */
    private MagicNode convertComponentRecursive(ParaComponentData data, MagicCircle parentCircle)
            throws ConversionException
    {

        Paramagic.LOG.trace("Converting component: {} (type: {})", data.getComponentId(), data.getClass().getSimpleName());

        Optional<ParaComponentConverter<? extends ParaComponentData>> converterOpt =
                converterRegistry.getConverter(data);

        if (converterOpt.isEmpty()) {
            throw new ConversionException(data.getComponentId(), data.getClass());
        }

        MagicNode node;
        try {
            node = convertUnchecked(converterOpt.get(), data);
        } catch (Exception e) {
            throw new ConversionException(data.getComponentId(), data.getClass(), e);
        }

        if (node == null) {
            Paramagic.LOG.warn("Converter returned null for component: {} (type: {})",
                    data.getComponentId(), data.getClass().getSimpleName());
            return null;
        }

        // Register the node if it has an ID
        if (parentCircle != null && data.getComponentId() != null && !data.getComponentId().isEmpty()) {
            parentCircle.registerNode(data.getComponentId(), node);
            Paramagic.LOG.trace("Registered node with ID: {}", data.getComponentId());
        }
        // Recursively convert and add children
        for (ParaComponentData childData : data.children) {
            MagicNode childNode = convertComponentRecursive(childData, parentCircle);
            if (childNode != null) {
                node.addChild(childNode);
            }
        }

        return node;
    }

    @SuppressWarnings("unchecked")
    private <T extends ParaComponentData> MagicNode convertUnchecked(
            ParaComponentConverter<? extends ParaComponentData> converter, T data
    ) {
        // 执行未检查转换的辅助方法。
        return ((ParaComponentConverter<T>) converter).convert(data);
    }

    /**
     * Allows external registration of custom converters.<br>
     * 允许外部注册自定义转换器。
     */
    public <T extends ParaComponentData> void registerConverter(ParaComponentConverter<T> converter) {
        converterRegistry.register(converter);
    }
}
