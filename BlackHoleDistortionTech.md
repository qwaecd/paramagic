# Black Hole Distortion Tech

## Goal

当前实现把黑洞扭曲拆成两部分：

- 用 mesh 在单独的 `distortionFieldFBO` 里生成屏幕空间扭曲向量场。
- 用一次全屏 `screen_warp` pass 对最终可见画面做统一重采样。

这样黑洞数量增加时，成本主要是多画几次 mesh mask，而不是对整张场景做多次 ping-pong 重采样。

## Runtime Flow

1. `ModRenderSystem.renderObjectsToMainFBO()` 正常绘制 `opaque -> transparent -> additive -> particles` 到 `mainFbo`。
2. 粒子之后调用 `renderDistortionField()`：
   - 从 Minecraft 主目标复制深度到 `distortionFieldFbo`。
   - 清空 distortion color attachment。
   - 绘制 `RenderType.DISTORTION` 的物体到 `distortionFieldFbo`。
3. `postProcessScene()` 继续对 `mainFbo.scene + mainFbo.bloom` 做 bloom 和 HDR 合成。
4. `composeFinalScene()` 把 `sceneCopyFBO` 的原版世界颜色与 Paramagic 的 HDR 结果合成为一张完整可见画面。
5. `applyScreenSpaceWarp()` 读取 `combinedSceneFbo + distortionFieldFbo`，输出扭曲后的最终图像。
6. `presentFinalResultToMinecraft()` 用全屏 copy 覆盖回 Minecraft 主目标。

## New Render Resources

- `SceneMRTFramebuffer`
  - 仍然负责 Paramagic 自己的 `scene + bloom source`。
- `SceneCopyFBO`
  - 仍然保存原版世界颜色。
- `ColorDepthFramebuffer distortionFieldFbo`
  - 颜色附件存屏幕空间 UV 偏移。
  - 深度附件只用于 mesh mask 的遮挡测试。
- `SingleTargetFramebuffer combinedSceneFbo`
  - 保存原版世界和 Paramagic HDR 的完整合成结果。
- `SingleTargetFramebuffer warpedSceneFbo`
  - 保存最终扭曲后的结果。

## Render Queue Contract

- 新增 `RenderType.DISTORTION`。
- `RenderQueue` 为它维护单独的 `distortion` 列表，并按远到近排序。
- `DISTORTION` 不再走普通距离裁剪，否则远处黑洞会被直接整物体丢弃。

## Distortion Material Contract

当前第一版通过 `ScreenSpaceDistortionMaterial` 接入黑洞扭曲。

- Shader: `distortion_field`
- Render type: `RenderType.DISTORTION`
- 输出目标: `distortionFieldFbo`
- 关键 uniform:
  - `u_centerUv`: 黑洞中心的屏幕 UV，由 `ModRenderSystem` 每帧根据物体变换投影得到。
  - `u_viewportSize`: 当前 distortion target 的尺寸。
  - `u_distortionStrength`: 扭曲强度。
  - `u_innerRadius`: `1/r` 的最小半径钳制，避免中心奇异值。
  - `u_outerRadius`: 外圈作用半径。
  - `u_maxOffset`: 单黑洞的最大 UV 偏移上限。

fragment shader 当前写入：

- `RG`: UV offset
- `B`: 本像素偏移强度，便于后续调试/扩展
- `A`: 当前未使用

## Shader Behavior

### `distortion_field.fsh`

- 用 `gl_FragCoord / viewportSize` 还原当前像素的屏幕 UV。
- 以 `u_centerUv` 为中心做 `1 / max(r, innerRadius)` 近似扭曲。
- 在 `outerRadius` 附近使用 `smoothstep` 做边界过渡。
- 最终偏移再经过 `u_maxOffset` clamp。
- 多个黑洞通过 additive blend 叠加成一张向量场。

### `final_compose.fsh`

- 输入：
  - Paramagic HDR scene
  - 原版世界颜色
- 逻辑：
  - 先做 ACES tone map
  - 再按旧的 `final_blit` 逻辑合成为完整最终颜色

### `screen_warp.fsh`

- 输入：
  - `combinedSceneFbo`
  - `distortionFieldFbo`
- 逻辑：
  - 读取 distortion field 的 `xy`
  - 对最终合成画面做一次 `uv + offset` 重采样

## Usage

任意 `IRenderable` 只要使用 `ScreenSpaceDistortionMaterial`，就会被放进 distortion queue。

现有的 `Sphere` 已经支持传入自定义材质，因此可以直接作为黑洞 mask：

```java
ScreenSpaceDistortionMaterial material = new ScreenSpaceDistortionMaterial()
        .setDistortionStrength(0.012f)
        .setInnerRadius(0.018f)
        .setOuterRadius(0.22f)
        .setMaxOffset(0.05f);

Sphere blackHoleMask = new Sphere(material);
blackHoleMask.getTransform()
        .setPosition(x, y, z)
        .setScale(radius);
```

如果后续要做热扰动、折射、冲击波，可以继续沿用：

- `RenderType.DISTORTION`
- `distortionFieldFbo`
- `screen_warp` 末端统一重采样

只需要新增不同的 distortion material / shader 参数模型。

## Current Limits

- 当前 warp 发生在 `scene + bloom` 已经合成之后，而不是分别扭曲两个输入。
- distortion field 只基于单次 mesh 投影，不做物理正确的光线积分。
- 黑洞中心 UV 使用物体原点投影，适合以中心为扭曲核的球形/近球形 mesh。
- 目前没有额外实现事件视界遮挡、吸积盘发光、色散和背景多重折返。

## Future Extensions

- 将 `ScreenSpaceDistortionMaterial` 抽成更通用的 `DistortionFieldMaterial` 层级。
- 在 distortion field 中增加 mask/priority/channel，支持不同局部屏幕空间特效共存。
- 如果需要更强的视觉分层，可以把 warp 从“最终图像”前移到 “scene/bloom/game scene” 的多输入合成阶段。
