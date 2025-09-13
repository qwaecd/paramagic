package com.qwaecd.paramagic.data.animation.converter;

import com.qwaecd.paramagic.client.animation.AnimationTrack;
import com.qwaecd.paramagic.client.animation.Animator;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.data.animation.AnimatorData;
import com.qwaecd.paramagic.data.animation.AnimationBindingData;
import com.qwaecd.paramagic.data.animation.AnimatorLibraryData;
import com.qwaecd.paramagic.data.animation.BindingData;
import com.qwaecd.paramagic.data.animation.converter.impl.KeyframeTrackConverter;
import com.qwaecd.paramagic.data.animation.track.KeyframeTrackData;
import com.qwaecd.paramagic.data.animation.track.TrackData;
import com.qwaecd.paramagic.data.para.ConversionException;
import com.qwaecd.paramagic.feature.MagicCircle;
import com.qwaecd.paramagic.feature.MagicNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AnimationFactory {
    private final Map<Class<? extends TrackData>, TrackConverter<?>> converterRegistry;

    public AnimationFactory() {
        this.converterRegistry = new HashMap<>();
        registerDefaultConverters();
    }

    private void registerDefaultConverters() {
        register(KeyframeTrackData.class, new KeyframeTrackConverter());
    }
    private void register(Class<? extends TrackData> dataClass, TrackConverter<?> converter) {
        converterRegistry.put(dataClass, converter);
    }

    public void injectAnimations(MagicCircle circle,
                                 @Nonnull AnimationBindingData bindingData,
                                 @Nullable AnimatorLibraryData animLib)
            throws ConversionException
    {
        validateInputs(circle);
        for (BindingData datum : bindingData.getBindingData()) {
            processBindingData(circle, datum, animLib);
        }
    }

    private void processBindingData(MagicCircle circle, BindingData bindingData, @Nullable AnimatorLibraryData animLib) throws ConversionException {
        AnimatorData animatorData = resolveAnimatorData(bindingData, animLib);
        MagicNode targetNode = findTargetNode(circle, bindingData.getTargetComponentId());
        Animator animatorInstance = getOrCreateAnimator(targetNode);
        attachTracksToAnimator(animatorData, targetNode, animatorInstance);
    }

    private AnimatorData resolveAnimatorData(BindingData bindingData, @Nullable AnimatorLibraryData animLib) throws ConversionException {
        AnimatorData animatorData = bindingData.getAnimatorData();

        // 如果不是内联定义的，则从库中查找
        if (animatorData == null) {
            return loadAnimatorDataFromLibrary(bindingData, animLib);
        }

        return animatorData;
    }

    private AnimatorData loadAnimatorDataFromLibrary(BindingData bindingData, @Nullable AnimatorLibraryData animLib) throws ConversionException {
        if (animLib == null) {
            throw new ConversionException("AnimatorLibraryData is required when using external animator templates, but it is null.");
        }

        try {
            String animatorTemplateName = bindingData.getAnimatorTemplateName();
            return animLib.getTemplateByName(animatorTemplateName).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new ConversionException("Animator template '" + bindingData.getAnimatorTemplateName() + "' not found in library.");
        }
    }

    private MagicNode findTargetNode(MagicCircle circle, String targetComponentId) throws ConversionException {
        Optional<MagicNode> nodeById = circle.findNodeById(targetComponentId);
        if (nodeById.isEmpty()) {
            throw new ConversionException("Target component ID '" + targetComponentId + "' not found in MagicCircle.");
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
        for (TrackData track : animatorData.getTracks()) {
            attachAnimatorTrack(track, targetNode.getTransform(), animatorInstance);
        }
    }

    private void validateInputs(MagicCircle circle) throws ConversionException {
        if (circle == null) {
            throw new ConversionException("MagicCircle cannot be null");
        }
    }

    private void attachAnimatorTrack(TrackData trackData, Transform transform, Animator animator) throws ConversionException {
        convertAndAddTrack(trackData, transform, animator);
    }

    @SuppressWarnings("unchecked")
    private <T extends TrackData> void convertAndAddTrack(T trackData, Transform targetTransform, Animator animator)
            throws ConversionException
    {
        TrackConverter<T> converter = (TrackConverter<T>) converterRegistry.get(trackData.getClass());

        if (converter != null) {
            AnimationTrack runtimeTrack = converter.convert(trackData, targetTransform);
            animator.addTrack(runtimeTrack);
        } else {
            throw new ConversionException("No converter found for " + trackData.getClass().getSimpleName());
        }
    }
}
