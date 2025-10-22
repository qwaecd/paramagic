package com.qwaecd.paramagic.assembler;

import com.qwaecd.paramagic.data.animation.converter.AnimationFactory;
import com.qwaecd.paramagic.data.animation.struct.AnimationBindingConfig;
import com.qwaecd.paramagic.data.animation.struct.AnimatorLibraryData;
import com.qwaecd.paramagic.data.para.converter.ConversionException;
import com.qwaecd.paramagic.data.para.converter.ParaConverters;
import com.qwaecd.paramagic.data.para.struct.ParaData;
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
                                AnimationBindingConfig animationBindingConfig,
                                AnimatorLibraryData animLib)
            throws AssemblyException
    {
        MagicCircle circle = genMagicCircle(skeletonData);

        injectAnimationsToCircle(circle, animationBindingConfig, animLib);

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

    private void injectAnimationsToCircle(MagicCircle circle, AnimationBindingConfig animationBindingConfig, AnimatorLibraryData animLib) throws AssemblyException {
        if (animationBindingConfig != null) {
            try {
                animationFactory.injectAnimations(circle, animationBindingConfig, animLib);
            } catch (ConversionException e) {
                throw new AssemblyException("Failed to inject animations into magic circle.", e);
            }
        }
    }
}
