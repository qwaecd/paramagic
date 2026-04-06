# Geometric Mask & Black Hole Distortion

## 几何遮罩（主线）

**几何遮罩**指：用 mesh 片元 + 深度（可选 alpha）在屏幕空间界定效果作用域，将数据写入 **mask FBO**（主路径）；模板缓冲仅作可选辅助（硬边裁剪），不作为主要 mask 载体。实现位于包 `com.qwaecd.paramagic.core.render.geometricmask`。

- **`GeometricEffectCaster`**：mesh + `Transform` + `IGeometricMaskEffect`，通过 `ModRenderSystem.addGeometricEffectCaster` 注册，**不**进入 `RenderQueue` / `RenderType`。
- **`ScreenSpaceEffectManager`**：在 `combinedSceneFbo` 合成之后，对每个 caster 依次执行 mask pass → 全屏 effect pass（内部 ping-pong）。
- **`IGeometricMaskEffect`**：声明 `getMaskShader()` / `getEffectShader()`、`GeometricMaskInputPolicy`、`GeometricMaskBlendPolicy`、`GeometricMaskRegionStrategy`，以及 mask/effect 的 uniform 绑定。

扭曲示例实现：**`DistortionGeometricMaskEffect`**（`distortion_field` + `screen_warp`）。Mask 通道约定见 `GeometricMaskChannelLayout.DOC_DISTORTION_FIELD`（RG = UV offset，等）。

## Runtime Flow

1. `ModRenderSystem.renderObjectsToMainFBO()`：仅绘制普通 `opaque → transparent → additive → particles` 到 `mainFbo`（无 `RenderType.DISTORTION`）。
2. `postProcessScene()`：`mainFbo` 的 scene + bloom 经 `PostProcessingManager`。
3. `composeFinalScene()`：`sceneCopyFBO` 与 Paramagic HDR 合成到 **`combinedSceneFbo`**（最终可见颜色，几何遮罩效果的默认输入）。
4. `ScreenSpaceEffectManager.applyGeometricMaskEffects(...)`：
   - 从主目标复制深度到 **`geometricMaskFbo`**（`ColorDepthFramebuffer`）；
   - 对每个 `GeometricEffectCaster`（远到近）：清空 mask 颜色 → 绘制 mask mesh → 全屏 effect，结果在内部 ping-pong FBO 间传递。
5. `presentFinalResultToMinecraft()`：将最后一道纹理覆盖到 Minecraft 主目标。

## 辉光 / Bloom（约定）

需要走**现有** bloom 管线的发光几何，必须在 **bloom 之前**写入 `mainFbo` 的 scene 与 bloom 源（MRT），以便 `PostProcessingManager` 能采样到高光。

几何遮罩阶段默认只在 **`POST_BLOOM_COMBINED`** 输入上工作，**不**负责新增需 bloom 的能量。若将来必须在遮罩之后才引入新辉光，需单独设计「Late 层 + 二次 bloom」（远期扩展）。

## 使用示例（调试黑洞）

```java
DistortionGeometricMaskEffect effect = new DistortionGeometricMaskEffect()
        .setDistortionStrength(0.14f)
        .setInnerRadius(0.018f)
        .setOuterRadius(2.0f)
        .setMaxOffset(0.05f);
Transform transform = new Transform();
transform.setPosition(x, y, z).setScale(r, r, r);
GeometricEffectCaster caster = new GeometricEffectCaster(
        SpherePrototype.getINSTANCE().getMesh(),
        transform,
        effect);
ModRenderSystem.getInstance().addGeometricEffectCaster(caster);
```

## Shader 行为摘要

- **`distortion_field`**：在 mask FBO 中写入扭曲向量场（见上文物理通道约定）。
- **`final_compose`**：HDR + 原版场景合成到 `combinedSceneFbo`。
- **`screen_warp`**：采样 `combinedScene` 与 mask，做 `uv + offset` 重采样。

## Current Limits

- 多 caster 时为**顺序** mask + effect（每实例一次全屏 pass），非「先累积全场再单次 resolve」的 Field 型优化。
- `GeometricMaskInputPolicy` 除 `POST_BLOOM_COMBINED` 外尚未接管线。
- 事件视界、色散、吸积盘等仍为效果层扩展项。

## Future Extensions

- 新增实现 `IGeometricMaskEffect`（热扰动、折射等），共用 `geometricMaskFbo` 与 `ScreenSpaceEffectManager`。
- Field 型：多实例先写入同一 field FBO（additive），再一次 resolve。
- 按需接入 `STENCIL_OPTIONAL` 减少 overdraw。
