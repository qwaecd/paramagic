package com.qwaecd.paramagic.data.animation.converter;

import com.qwaecd.paramagic.client.animation.AnimationTrack;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.data.animation.track.TrackData;
import com.qwaecd.paramagic.data.para.ConversionException;

/**
 * Converts a specific type of TrackData instance into a runtime AnimationTrack.<br>
 * 将一个特定类型的 TrackData 实例转换为运行时的 AnimationTrack。
 *
 * @param <T> The concrete subtype of TrackData this converter can handle.<br>
 *           此转换器能够处理的 TrackData 的具体子类型。
 */
public interface TrackConverter<T extends TrackData> {
    /**
     * Performs the conversion.
     * 执行转换。
     *
     * @param trackData The animation track data to convert.<br>
     *                  要转换的动画轨道数据。
     * @param targetTransform The target Transform object to which the animation will be applied.<br>
     *                       动画将要应用到的目标 Transform 对象。
     * @return A configured AnimationTrack instance ready for runtime playback.<br>
     *         一个配置好的、准备在运行时播放的 AnimationTrack 实例。
     */
    AnimationTrack convert(T trackData, Transform targetTransform) throws ConversionException;
}