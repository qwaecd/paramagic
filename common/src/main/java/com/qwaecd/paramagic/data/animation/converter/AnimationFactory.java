package com.qwaecd.paramagic.data.animation.converter;

import com.qwaecd.paramagic.client.animation.AnimationTrack;
import com.qwaecd.paramagic.client.animation.Animator;
import com.qwaecd.paramagic.core.para.material.ParaMaterial;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.data.animation.converter.impl.KeyframeTrackConverter;
import com.qwaecd.paramagic.data.animation.struct.AnimationBinding;
import com.qwaecd.paramagic.data.animation.struct.AnimationBindingConfig;
import com.qwaecd.paramagic.data.animation.struct.AnimatorData;
import com.qwaecd.paramagic.data.animation.struct.AnimatorLibraryData;
import com.qwaecd.paramagic.data.animation.struct.track.KeyframeTrackData;
import com.qwaecd.paramagic.data.animation.struct.track.TrackData;
import com.qwaecd.paramagic.data.para.converter.ConversionException;
import com.qwaecd.paramagic.feature.MagicCircle;
import com.qwaecd.paramagic.feature.MagicNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AnimationFactory {
    private final Map<Class<? extends TrackData<?>>, TrackConverter<?>> converterRegistry;

    public AnimationFactory() {
        this.converterRegistry = new HashMap<>();
        registerDefaultConverters();
    }

    private void registerDefaultConverters() {
        register(KeyframeTrackData.class, new KeyframeTrackConverter());
    }
    private <T extends TrackData<?>> void register(Class<T> dataClass, TrackConverter<?> converter) {
        converterRegistry.put(dataClass, converter);
    }

    public void injectAnimations(MagicCircle circle,
                                 @Nonnull AnimationBindingConfig bindingData,
                                 @Nullable AnimatorLibraryData animLib)
            throws ConversionException
    {
        validateInputs(circle);
        for (AnimationBinding datum : bindingData.getBindings()) {
            processBindingData(circle, datum, animLib);
        }
    }

    private void processBindingData(MagicCircle circle, AnimationBinding animationBinding, @Nullable AnimatorLibraryData animLib) throws ConversionException {
        AnimatorData animatorData = resolveAnimatorData(animationBinding, animLib);
        MagicNode targetNode = findTargetNode(circle, animationBinding.getTargetNodeNameOrComponentId());
        Animator animatorInstance = getOrCreateAnimator(targetNode);
        attachTracksToAnimator(animatorData, targetNode, animatorInstance);
    }

    private AnimatorData resolveAnimatorData(AnimationBinding animationBinding, @Nullable AnimatorLibraryData animLib) throws ConversionException {
        AnimatorData animatorData = animationBinding.getAnimatorData();

        // 如果不是内联定义的，则从库中查找
        if (animatorData == null) {
            return loadAnimatorDataFromLibrary(animationBinding, animLib);
        }

        return animatorData;
    }

    private AnimatorData loadAnimatorDataFromLibrary(AnimationBinding animationBinding, @Nullable AnimatorLibraryData animLib) throws ConversionException {
        if (animLib == null) {
            throw new ConversionException("AnimatorLibraryData is required when using external animator templates, but it is null.");
        }

        try {
            String animatorTemplateName = animationBinding.getAnimatorTemplateName();
            return animLib.getTemplateByName(animatorTemplateName).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new ConversionException("Animator template '" + animationBinding.getAnimatorTemplateName() + "' not found in library.");
        }
    }

    private MagicNode findTargetNode(MagicCircle circle, String targetNodeNameOrComponentId) throws ConversionException {
        Optional<MagicNode> nodeById = circle.findNode(targetNodeNameOrComponentId);
        if (nodeById.isEmpty()) {
            throw new ConversionException("Target component ID '" + targetNodeNameOrComponentId + "' not found in MagicCircle.");
        }
        return nodeById.get();
    }

    private Animator getOrCreateAnimator(MagicNode targetNode) {
        Animator animatorInstance = targetNode.getAnimator();

        if (animatorInstance == null) {
            animatorInstance = new Animator();
            targetNode.setAnimator(animatorInstance);
        }

        return animatorInstance;
    }

    private void attachTracksToAnimator(AnimatorData animatorData, MagicNode targetNode, Animator animatorInstance) throws ConversionException {
        for (TrackData<?> track : animatorData.getTracks()) {
            ParaMaterial material = (ParaMaterial) targetNode.getMaterial();
            if (track.isColorTrack) {
                if (material == null){
                    throw new ConversionException("Material is required, you may be animating a VoidPara.");
                }
                material.setHasColorAnimation(true);
            }
            convertAndAddTrack(track, targetNode.getTransform(), material, animatorInstance);
        }
    }

    private void validateInputs(MagicCircle circle) throws ConversionException {
        if (circle == null) {
            throw new ConversionException("MagicCircle cannot be null");
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends TrackData<?>> void convertAndAddTrack(T trackData, Transform targetTransform, ParaMaterial material, Animator animator)
            throws ConversionException
    {
        TrackConverter<T> converter = (TrackConverter<T>) converterRegistry.get(trackData.getClass());

        if (converter != null) {
            AnimationTrack runtimeTrack = converter.convert(trackData, targetTransform, material);
            animator.addTrack(runtimeTrack);
        } else {
            throw new ConversionException("No converter found for " + trackData.getClass().getSimpleName());
        }
    }
}
