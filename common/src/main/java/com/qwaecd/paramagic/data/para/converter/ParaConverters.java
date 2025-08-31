package com.qwaecd.paramagic.data.para.converter;

import com.qwaecd.paramagic.data.para.ParaData;
import com.qwaecd.paramagic.feature.MagicCircle;

public class ParaConverters {
    private static final BasedParaDataConverter DEFAULT_CONVERTER = new BasedParaDataConverter();
    public static ParaConverterRegistry getRegistry() {
        return DEFAULT_CONVERTER.getConverterRegistry();
    }

    public static MagicCircle convert(ParaData paraData) throws ConversionException {
        return DEFAULT_CONVERTER.convert(paraData);
    }

    public static boolean canConvert(ParaData paraData) {
        return DEFAULT_CONVERTER.canConvert(paraData);
    }
}
