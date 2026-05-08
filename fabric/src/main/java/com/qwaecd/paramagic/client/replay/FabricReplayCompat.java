package com.qwaecd.paramagic.client.replay;

import com.qwaecd.paramagic.mixin.accessor.MinecraftMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 这 tm 什么黑魔法<br>
 * 纯 vibe 打造，没有人工痕迹，太 tm 黑了
 * <br>
 * 目标 replay 版本 1.20.1-2.6.23。
 */
public final class FabricReplayCompat {
    private static final String ENABLED_PROPERTY = "paramagic.compat.replay";
    private static final String REPLAY_TIMER_CLASS_NAME = "com.replaymod.pathing.player.ReplayTimer";
    private static final float DEFAULT_REPLAY_DELTA_SECONDS = 1.0f / 20.0f;
    private static final float MAX_REPLAY_DELTA_SECONDS = 0.25f;
    private static final boolean ENABLED = Boolean.getBoolean(ENABLED_PROPERTY);
    private static volatile boolean pauseCompatDisabled;
    private static volatile boolean deltaCompatDisabled;
    private static volatile Object currentVideoRenderer;
    private static ReplayPathHandles replayPathHandles;
    private static VideoRendererHandles videoRendererHandles;
    private static boolean renderCallbacksInitialized;
    private static boolean renderCallbacksDisabled;
    private static long lastPathTimeMs = Long.MIN_VALUE;
    private static long lastVideoTimeMs = Long.MIN_VALUE;
    private static Object lastPathPlayer;
    private static Object lastVideoRenderer;

    private FabricReplayCompat() {
    }

    public static void init() {
        if (!ENABLED) {
            return;
        }
        tryRegisterRenderCallbacks();
    }

    public static boolean shouldPauseVisuals(Minecraft minecraft, boolean vanillaPaused) {
        if (!ENABLED) {
            return vanillaPaused;
        }
        if (!vanillaPaused || pauseCompatDisabled) {
            return vanillaPaused;
        }
        try {
            return !isReplayTimerActive(minecraft);
        } catch (Throwable ignored) {
            pauseCompatDisabled = true;
            return true;
        }
    }

    public static float getVisualDeltaTime(Minecraft minecraft, float vanillaDeltaTime) {
        if (!ENABLED) {
            return vanillaDeltaTime;
        }
        if (!isReplayDeltaNeeded(minecraft, vanillaDeltaTime)) {
            resetDeltaTracking();
            return vanillaDeltaTime;
        }
        if (deltaCompatDisabled) {
            return vanillaDeltaTime;
        }
        try {
            Float replayDelta = tryGetReplayDeltaSeconds();
            if (replayDelta != null) {
                return replayDelta;
            }
        } catch (Throwable ignored) {
            deltaCompatDisabled = true;
            resetDeltaTracking();
        }
        return vanillaDeltaTime;
    }

    private static boolean isReplayTimerActive(Minecraft minecraft) {
        Timer timer = ((MinecraftMixin) minecraft).getTimer();
        return timer != null && REPLAY_TIMER_CLASS_NAME.equals(timer.getClass().getName());
    }

    private static boolean isReplayDeltaNeeded(Minecraft minecraft, float vanillaDeltaTime) {
        if (Float.isFinite(vanillaDeltaTime) && vanillaDeltaTime > 0.0f) {
            return false;
        }
        try {
            return isReplayTimerActive(minecraft);
        } catch (Throwable ignored) {
            deltaCompatDisabled = true;
            return false;
        }
    }

    private static Float tryGetReplayDeltaSeconds() throws Throwable {
        Float videoDelta = tryGetVideoDeltaSeconds();
        if (videoDelta != null) {
            return videoDelta;
        }
        Float pathDelta = tryGetPathDeltaSeconds();
        if (pathDelta != null) {
            return pathDelta;
        }
        resetDeltaTracking();
        return null;
    }

    private static Float tryGetVideoDeltaSeconds() throws Throwable {
        Object renderer = currentVideoRenderer;
        if (renderer == null) {
            lastVideoRenderer = null;
            lastVideoTimeMs = Long.MIN_VALUE;
            return null;
        }
        VideoRendererHandles handles = getVideoRendererHandles(renderer.getClass());
        if ((boolean) handles.isPaused.invoke(renderer)) {
            lastVideoRenderer = renderer;
            lastVideoTimeMs = Long.MIN_VALUE;
            return 0.0f;
        }
        long videoTimeMs = (int) handles.getVideoTime.invoke(renderer);
        int fps = getVideoFps(renderer, handles);
        if (renderer != lastVideoRenderer) {
            lastVideoRenderer = renderer;
            lastVideoTimeMs = videoTimeMs;
            return fpsDeltaSeconds(fps);
        }
        long previousTimeMs = lastVideoTimeMs;
        lastVideoTimeMs = videoTimeMs;
        if (previousTimeMs == Long.MIN_VALUE) {
            return fpsDeltaSeconds(fps);
        }
        long deltaMs = videoTimeMs - previousTimeMs;
        if (deltaMs <= 0) {
            return fpsDeltaSeconds(fps);
        }
        return sanitizeReplayDelta(deltaMs / 1000.0f);
    }

    private static Float tryGetPathDeltaSeconds() throws Throwable {
        ReplayPathHandles handles = getReplayPathHandles();
        Object instance = handles.instanceField.get(null);
        if (instance == null) {
            resetPathTracking();
            return null;
        }
        Object guiPathing = handles.getGuiPathing.invoke(instance);
        if (guiPathing == null) {
            resetPathTracking();
            return null;
        }
        Object player = handles.playerField.get(guiPathing);
        if (player == null || !(boolean) handles.isActive.invoke(player)) {
            resetPathTracking();
            return null;
        }
        long pathTimeMs = (long) handles.getTimePassed.invoke(player);
        if (player != lastPathPlayer) {
            lastPathPlayer = player;
            lastPathTimeMs = pathTimeMs;
            return DEFAULT_REPLAY_DELTA_SECONDS;
        }
        long previousTimeMs = lastPathTimeMs;
        lastPathTimeMs = pathTimeMs;
        if (previousTimeMs == Long.MIN_VALUE) {
            return DEFAULT_REPLAY_DELTA_SECONDS;
        }
        long deltaMs = pathTimeMs - previousTimeMs;
        if (deltaMs <= 0) {
            return DEFAULT_REPLAY_DELTA_SECONDS;
        }
        return sanitizeReplayDelta(deltaMs / 1000.0f);
    }

    private static ReplayPathHandles getReplayPathHandles() throws ReflectiveOperationException {
        ReplayPathHandles handles = replayPathHandles;
        if (handles != null) {
            return handles;
        }
        Class<?> simplePathingClass = Class.forName("com.replaymod.simplepathing.ReplayModSimplePathing");
        Field instanceField = simplePathingClass.getField("instance");
        Method getGuiPathing = simplePathingClass.getMethod("getGuiPathing");
        Class<?> guiPathingClass = Class.forName("com.replaymod.simplepathing.gui.GuiPathing");
        Field playerField = guiPathingClass.getDeclaredField("player");
        playerField.setAccessible(true);
        Class<?> playerClass = Class.forName("com.replaymod.pathing.player.AbstractTimelinePlayer");
        Method isActive = playerClass.getMethod("isActive");
        Method getTimePassed = playerClass.getMethod("getTimePassed");
        MethodHandles.Lookup lookup = MethodHandles.publicLookup();
        handles = new ReplayPathHandles(
                instanceField,
                lookup.unreflect(getGuiPathing),
                playerField,
                lookup.unreflect(isActive),
                lookup.unreflect(getTimePassed)
        );
        replayPathHandles = handles;
        return handles;
    }

    private static VideoRendererHandles getVideoRendererHandles(Class<?> rendererClass) throws ReflectiveOperationException {
        VideoRendererHandles handles = videoRendererHandles;
        if (handles != null && handles.rendererClass == rendererClass) {
            return handles;
        }
        MethodHandles.Lookup lookup = MethodHandles.publicLookup();
        Method getVideoTime = rendererClass.getMethod("getVideoTime");
        Method isPaused = rendererClass.getMethod("isPaused");
        Method getRenderSettings = rendererClass.getMethod("getRenderSettings");
        Object settings = getRenderSettings.invoke(currentVideoRenderer);
        Class<?> settingsClass = settings.getClass();
        Method getFramesPerSecond = settingsClass.getMethod("getFramesPerSecond");
        handles = new VideoRendererHandles(
                rendererClass,
                lookup.unreflect(getVideoTime),
                lookup.unreflect(isPaused),
                lookup.unreflect(getRenderSettings),
                lookup.unreflect(getFramesPerSecond)
        );
        videoRendererHandles = handles;
        return handles;
    }

    private static int getVideoFps(Object renderer, VideoRendererHandles handles) throws Throwable {
        Object settings = handles.getRenderSettings.invoke(renderer);
        int fps = (int) handles.getFramesPerSecond.invoke(settings);
        return Math.max(fps, 1);
    }

    private static float fpsDeltaSeconds(int fps) {
        return sanitizeReplayDelta(1.0f / Math.max(fps, 1));
    }

    private static float sanitizeReplayDelta(float deltaSeconds) {
        if (!Float.isFinite(deltaSeconds) || deltaSeconds <= 0.0f) {
            return DEFAULT_REPLAY_DELTA_SECONDS;
        }
        return Math.min(deltaSeconds, MAX_REPLAY_DELTA_SECONDS);
    }

    private static void resetDeltaTracking() {
        resetPathTracking();
        lastVideoRenderer = null;
        lastVideoTimeMs = Long.MIN_VALUE;
    }

    private static void resetPathTracking() {
        lastPathPlayer = null;
        lastPathTimeMs = Long.MIN_VALUE;
    }

    private static void tryRegisterRenderCallbacks() {
        if (renderCallbacksInitialized || renderCallbacksDisabled) {
            return;
        }
        try {
            Class<?> preClass = Class.forName("com.replaymod.render.events.ReplayRenderCallback$Pre");
            Class<?> postClass = Class.forName("com.replaymod.render.events.ReplayRenderCallback$Post");
            registerCallback(preClass, "beforeRendering", args -> {
                currentVideoRenderer = args[0];
                lastVideoRenderer = null;
                lastVideoTimeMs = Long.MIN_VALUE;
            });
            registerCallback(postClass, "afterRendering", args -> {
                currentVideoRenderer = null;
                lastVideoRenderer = null;
                lastVideoTimeMs = Long.MIN_VALUE;
            });
            renderCallbacksInitialized = true;
        } catch (Throwable ignored) {
            renderCallbacksDisabled = true;
            currentVideoRenderer = null;
        }
    }

    private static void registerCallback(Class<?> callbackClass, String callbackMethodName, CallbackAction action) throws ReflectiveOperationException {
        Field eventField = callbackClass.getField("EVENT");
        Object event = eventField.get(null);
        Method register = event.getClass().getDeclaredMethod("register", Object.class);
        register.setAccessible(true);
        Object listener = Proxy.newProxyInstance(
                callbackClass.getClassLoader(),
                new Class<?>[]{callbackClass},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "equals" -> proxy == args[0];
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "toString" -> "ParamagicReplayRenderCallbackProxy";
                            default -> null;
                        };
                    }
                    if (callbackMethodName.equals(method.getName()) && args != null) {
                        action.invoke(args);
                    }
                    return null;
                }
        );
        register.invoke(event, listener);
    }

    private interface CallbackAction {
        void invoke(Object[] args);
    }

    private record ReplayPathHandles(
            Field instanceField,
            MethodHandle getGuiPathing,
            Field playerField,
            MethodHandle isActive,
            MethodHandle getTimePassed
    ) {
    }

    private record VideoRendererHandles(
            Class<?> rendererClass,
            MethodHandle getVideoTime,
            MethodHandle isPaused,
            MethodHandle getRenderSettings,
            MethodHandle getFramesPerSecond
    ) {
    }
}
