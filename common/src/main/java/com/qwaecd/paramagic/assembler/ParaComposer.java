package com.qwaecd.paramagic.assembler;

import com.qwaecd.paramagic.data.animation.AnimationBindingData;
import com.qwaecd.paramagic.data.animation.AnimatorLibraryData;
import com.qwaecd.paramagic.data.animation.converter.AnimationFactory;
import com.qwaecd.paramagic.data.para.ConversionException;
import com.qwaecd.paramagic.data.para.ParaData;
import com.qwaecd.paramagic.data.para.converter.ParaConverters;
import com.qwaecd.paramagic.feature.MagicCircle;
import lombok.Getter;


public class ParaComposer {
    @Getter
    private static final ParaComposer INSTANCE = new ParaComposer();
    private final AnimationFactory animationFactory;

    private ParaComposer() {
        this.animationFactory = new AnimationFactory();
    }

    public MagicCircle assemble(ParaData skeletonData,
                                AnimationBindingData animationBindingData,
                                AnimatorLibraryData animLib)
            throws AssemblyException
    {
        MagicCircle circle = genMagicCircle(skeletonData);

        injectAnimationsToCircle(circle, animationBindingData, animLib);

        return circle;
    }

    private MagicCircle genMagicCircle(ParaData skeletonData) throws AssemblyException {
        MagicCircle circle;
        try {
            circle = ParaConverters.convert(skeletonData);
        } catch (ConversionException e) {
            throw new AssemblyException("Failed to build magic circle skeleton.", e);
        }
        return circle;
    }

    private void injectAnimationsToCircle(MagicCircle circle, AnimationBindingData animationBindingData, AnimatorLibraryData animLib) throws AssemblyException {
        if (animationBindingData != null) {
            try {
                animationFactory.injectAnimations(circle, animationBindingData, animLib);
            } catch (ConversionException e) {
                throw new AssemblyException("Failed to inject animations into magic circle.", e);
            }
        }
    }
}
