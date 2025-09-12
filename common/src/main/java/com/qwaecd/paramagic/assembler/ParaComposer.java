package com.qwaecd.paramagic.assembler;

import com.qwaecd.paramagic.data.animation.AnimationData;
import com.qwaecd.paramagic.data.animation.converter.AnimationFactory;
import com.qwaecd.paramagic.data.para.ConversionException;
import com.qwaecd.paramagic.data.para.ParaData;
import com.qwaecd.paramagic.data.para.converter.ParaConverters;
import com.qwaecd.paramagic.feature.MagicCircle;


public class ParaComposer {
    private final AnimationFactory animationFactory;

    private ParaComposer() {
        this.animationFactory = new AnimationFactory();
    }

    public MagicCircle assemble(ParaData skeletonData, AnimationData animationData) throws AssemblyException {
        MagicCircle circle;
        try {
            circle = ParaConverters.convert(skeletonData);
        } catch (ConversionException e) {
            throw new AssemblyException("Failed to build magic circle skeleton.", e);
        }

        if (animationData != null) {
            try {
                animationFactory.injectAnimations(circle, animationData);
            } catch (ConversionException e) {
                throw new AssemblyException("Failed to inject animations into magic circle.", e);
            }
        }

        return circle;
    }
}
