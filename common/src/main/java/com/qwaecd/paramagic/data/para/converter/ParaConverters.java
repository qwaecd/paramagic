package com.qwaecd.paramagic.data.para.converter;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.data.para.converter.api.ParaConverterRegistry;
import com.qwaecd.paramagic.data.para.converter.internal.MainParaDataConverter;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.feature.circle.MagicCircle;

public class ParaConverters {
    private static MainParaDataConverter DEFAULT_CONVERTER;

    private ParaConverters() {}
    public static void init() {
        if (DEFAULT_CONVERTER != null) {
            Paramagic.LOG.warn("ParaConverters is already initialized.");
            return;
        }
        DEFAULT_CONVERTER = new MainParaDataConverter();
    }
    private static MainParaDataConverter getConverter() {
        if (DEFAULT_CONVERTER == null) {
            throw new IllegalStateException("ParaConverters has not been initialized. Please call init() first.");
        }
        return DEFAULT_CONVERTER;
    }
    public static ParaConverterRegistry getRegistry() {
        return getConverter().getConverterRegistry();
    }

    public static MagicCircle convert(ParaData paraData) throws ConversionException {
        return getConverter().convert(paraData);
    }

    public static boolean canConvert(ParaData paraData) {
        return getConverter().canConvert(paraData);
    }
}
