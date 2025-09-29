package com.qwaecd.paramagic.core.particle.compute;

/**。
 * Provides GPU compute shaders used in the particle system pipeline (particle emission & update).
 * <p>
 * Contract / 使用约定:
 * <ul>
 *   <li>If {@link #isSupported()} returns {@code true}, every shader getter in this interface MUST return a
 *       non-null, fully initialized {@link ComputeShader}. （当 {@code isSupported()==true} 时，下面所有方法都必须返回
 *       非 null 且已正确初始化的 {@link ComputeShader} 实例。）</li>
 *   <li>The provider represents an all-or-nothing capability: partial availability of individual shaders is
 *       NOT expected. （本接口表达“要么全部支持要么全部不支持”的能力，不期望出现某个 shader 单独缺失的情况。）</li>
 * </ul>
 * <p>
 * Typical usage / 典型用法:
 * <pre>{@code
 * IComputeShaderProvider provider = ...;
 * if (!provider.isSupported()) {
 *     // Fallback to CPU path or skip compute-based emission.
 *     return;
 * }
 * ComputeShader reserve = provider.reserveRequestShader();
 * ComputeShader init    = provider.initializeRequestShader();
 * ComputeShader update  = provider.particleUpdateShader();
 * // Use the shaders...
 * }</pre>
 * <p>
 */
public interface IComputeShaderProvider {
    /**
     * Indicates whether GPU compute shaders are supported and fully initialized.
     * <p>Chinese: 指示当前环境是否支持并已成功初始化所有需要的 compute shader。</p>
     *
     * @return {@code true} if all compute shaders are available; {@code false} otherwise.
     */
    boolean isSupported();

    /**
     * Returns the compute shader used to initialize (emit) newly reserved particle instances
     * after buffer space has been reserved.
     * <p>Chinese: 返回用于在预留缓冲区空间后，初始化（发射）新粒子的 compute shader。</p>
     * <p>
     * Precondition / 前置条件: {@link #isSupported()} MUST be {@code true}.
     * </p>
     * @return non-null {@link ComputeShader} (never null when contract is respected)
     */
    ComputeShader initializeRequestShader();

    /**
     * Returns the compute shader responsible for reserving / allocating space (e.g., indices, slots)
     * needed for upcoming particle emissions.
     * <p>Chinese: 返回负责为即将发射的粒子预留/分配缓冲区空间（如索引、槽位等）的 compute shader。</p>
     * <p>
     * Precondition / 前置条件: {@link #isSupported()} MUST be {@code true}.
     * （调用前必须保证 {@code isSupported()==true}。）
     * </p>
     * @return non-null {@link ComputeShader}
     */
    ComputeShader reserveRequestShader();

    /**
     * Returns the compute shader that updates particle state each frame / tick (e.g., position, life, color).
     * <p>Chinese: 返回用于每帧/每 tick 更新粒子状态（位置、生命周期、颜色等）的 compute shader。</p>
     * <p>
     * Precondition / 前置条件: {@link #isSupported()} MUST be {@code true}.
     * </p>
     * @return non-null {@link ComputeShader}
     */
    ComputeShader particleUpdateShader();
}
