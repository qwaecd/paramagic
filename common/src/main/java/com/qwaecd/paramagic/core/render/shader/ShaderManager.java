package com.qwaecd.paramagic.core.render.shader;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import lombok.Getter;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.qwaecd.paramagic.core.exception.ShaderException;

public class ShaderManager {
    private static ShaderManager INSTANCE;

    private static final Map<String, ShaderInfo> SHADER_DEFINITIONS = new ConcurrentHashMap<>();

    private final Map<String, Shader> SHADER_REGISTRY;
    @Getter
    private Shader positionColorShader;
    @Getter
    private Shader baseBallInShader;
    @Getter
    private Shader baseBallOutShader;
    @Getter
    private Shader debugMagicCircleShader;
    @Getter
    private Shader compositeShader;

    private ShaderManager() {
        this.SHADER_REGISTRY = new HashMap<>();
    }
    public static void init() {
        if (INSTANCE != null) {
            Paramagic.LOG.warn("ShaderManager is already initialized.");
            return;
        }
        INSTANCE = new ShaderManager();
        INSTANCE.loadRegisteredShaders();
        INSTANCE.initInstanceShaders();
    }

    /**
     * Gets the singleton instance of the ShaderManager.
     * <p>
     * 获取 ShaderManager 的单例实例。
     *
     * @return The singleton instance of ShaderManager. / ShaderManager 的单例实例。
     * @throws IllegalStateException if the ShaderManager has not been initialized via {@link #init()}. / 如果 ShaderManager 未通过 {@link #init()} 初始化。
     */
    public static ShaderManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ShaderManager has not been initialized. Please call init() first.");
        }
        return INSTANCE;
    }

    private void initInstanceShaders() {
        positionColorShader = getShaderThrowIfNotFound("position_color");
        baseBallInShader    = getShaderThrowIfNotFound("base_ball_in");
        baseBallOutShader   = getShaderThrowIfNotFound("base_ball_out");
        debugMagicCircleShader = getShaderThrowIfNotFound("debug_magic_circle");
        compositeShader     = getShaderThrowIfNotFound("composite");
    }

    /**
     * Registers a shader definition with a given name. This method is thread-safe.
     * If a shader with the same name is already registered, a warning will be logged.
     * <p>
     * 使用给定名称注册着色器定义。此方法是线程安全的。
     * 如果已注册同名着色器，将记录一条警告。
     *
     * @param registerName The name to register the shader with. / 注册着色器所用的名称。
     * @param info         The {@link ShaderInfo} object containing details about the shader. / 包含着色器详细信息的 {@link ShaderInfo} 对象。
     */
    public static void registerShaderInfo(String registerName, ShaderInfo info) {
        if (SHADER_DEFINITIONS.containsKey(registerName)) {
            Paramagic.LOG.warn("Shader definition for '{}' is being overridden. This may be unintentional.", registerName);
        }
        SHADER_DEFINITIONS.put(registerName, info);
    }

    private void loadRegisteredShaders() {
        Paramagic.LOG.debug("Loading {} registered shaders...", SHADER_DEFINITIONS.size());

        ShaderInfo defaultInfo = new ShaderInfo("", "position_color");
        registerShaderInfo("position_color", defaultInfo);
        boolean canUseComputerShader = ModRenderSystem.getInstance().canUseComputerShader();
        boolean canUseGeometryShader = ModRenderSystem.getInstance().canUseGeometryShader();
        for (Map.Entry<String, ShaderInfo> entry : SHADER_DEFINITIONS.entrySet()) {
            String name = entry.getKey();
            ShaderInfo info = entry.getValue();
            try {
                Shader shader = createShader(info, canUseComputerShader, canUseGeometryShader);
                if (shader != null) {
                    SHADER_REGISTRY.put(name, shader);
                    Paramagic.LOG.debug("Successfully loaded shader: {} (compute? {}, geometry? {})", name, info.isComputeShader(), info.hasGeometryShader());
                } else if (info.isComputeShader() || info.hasGeometryShader()) {
                    // 针对不支持的可选阶段分别打印更准确的日志
                    if (info.isComputeShader()) {
                        Paramagic.LOG.warn("Skipping compute shader '{}' (system unsupported).", name);
                    } else if (info.hasGeometryShader()) {
                        Paramagic.LOG.warn("Skipping geometry shader '{}' (system unsupported).", name);
                    }
                }
            } catch (Exception e) {
                Paramagic.LOG.error("Failed to load shader '{}' from path: '{}', fileName: '{}'", name, info.getPath(), info.getFileName(), e);
                throw new ShaderException("Shader load failure (fast-fail): " + name, e);
            }
        }
        this.positionColorShader = SHADER_REGISTRY.get("position_color");
        if (this.positionColorShader == null) {
            throw new ShaderException("Failed to load the default {" + defaultInfo.getFileName() + "} shader.");
        }
    }

    private static @Nullable Shader createShader(ShaderInfo info, boolean canUseComputerShader, boolean canUseGeometryShader) {
        if (info.isComputeShader() && !canUseComputerShader) {
            return null;
        }
        if (info.hasGeometryShader() && !canUseGeometryShader) {
            return null;
        }
        return ShaderProgramBuilder.buildFromInfo(info);
    }

    /**
     * Retrieves a shader by its registered name.
     * If the shader is not found, a warning is logged and the default position-color shader is returned.
     * <p>
     * 根据注册名称检索着色器。
     * 如果未找到着色器，将记录一条警告并返回默认的着色器。
     *
     * @param registerName The name of the shader to retrieve. / 要检索的着色器的名称。
     * @return The requested {@link Shader}, or the default shader if not found. / 请求的 {@link Shader}，如果未找到则为默认着色器。
     */
    public Shader getShader(String registerName) {
        if (SHADER_REGISTRY.containsKey(registerName)) {
            return SHADER_REGISTRY.get(registerName);
        }
        Paramagic.LOG.warn("Shader {} not found, returning default position color shader", registerName);
        return positionColorShader;
    }

    /**
     * Retrieves a shader by its registered name, throwing an exception if not found.
     * <p>
     * 根据注册名称检索着色器，如果未找到则抛出异常。
     *
     * @param registerName The name of the shader to retrieve. / 要检索的着色器的名称。
     * @return The requested {@link Shader}. / 请求的 {@link Shader}。
     * @throws RuntimeException if the shader with the given name is not found in the registry. / 如果在注册表中找不到具有给定名称的着色器。
     */
    public Shader getShaderThrowIfNotFound(String registerName) {
        if (!SHADER_REGISTRY.containsKey(registerName)) {
            throw new ShaderException("Shader {" + registerName + "} not found in registry.");
        }
        return SHADER_REGISTRY.get(registerName);
    }

    /**
     * 获取可能不存在的 shader，通常是 compute shader 或含有 geometry shader。
     * @param registerName shader 注册名。
     * @return 找不到时返回 null。
     */
    public @Nullable Shader getShaderNullable(String registerName) {
        return SHADER_REGISTRY.get(registerName);
    }

}
