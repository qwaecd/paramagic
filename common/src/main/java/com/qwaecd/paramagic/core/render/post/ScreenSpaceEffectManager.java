package com.qwaecd.paramagic.core.render.post;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.qwaecd.paramagic.client.renderbase.factory.FullScreenQuadFactory;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.geometricmask.DistortionGeometricMaskEffect;
import com.qwaecd.paramagic.core.render.geometricmask.GeometricEffectCaster;
import com.qwaecd.paramagic.core.render.geometricmask.GeometricMaskUniformContext;
import com.qwaecd.paramagic.core.render.geometricmask.IGeometricMaskEffect;
import com.qwaecd.paramagic.core.render.post.buffer.ColorDepthFramebuffer;
import com.qwaecd.paramagic.core.render.post.buffer.FramebufferUtils;
import com.qwaecd.paramagic.core.render.post.buffer.SingleTargetFramebuffer;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.things.IMatrixStackProvider;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

/**
 * 编排几何遮罩：对每个 {@link GeometricEffectCaster} 依次执行 mask pass（写入 mask FBO）与 effect pass（全屏 ping-pong）。
 */
public class ScreenSpaceEffectManager implements AutoCloseable {
    private final ColorDepthFramebuffer maskFbo;
    private SingleTargetFramebuffer pingFbo;
    private SingleTargetFramebuffer pongFbo;
    private Mesh fullscreenQuad;

    private final Matrix4f reusableMatrix = new Matrix4f();
    private final Matrix4f reusableClipMatrix = new Matrix4f();
    private final Vector4f reusableClipPosition = new Vector4f();
    private final GeometricMaskUniformContext uniformContext = new GeometricMaskUniformContext();

    public ScreenSpaceEffectManager(ColorDepthFramebuffer maskFbo) {
        this.maskFbo = maskFbo;
    }

    public void initialize(int width, int height) {
        this.pingFbo = new SingleTargetFramebuffer(width, height);
        this.pongFbo = new SingleTargetFramebuffer(width, height);
        this.fullscreenQuad = FullScreenQuadFactory.createFullscreenQuad();
    }

    public void onResize(int newWidth, int newHeight) {
        if (pingFbo != null) {
            pingFbo.resize(newWidth, newHeight);
        }
        if (pongFbo != null) {
            pongFbo.resize(newWidth, newHeight);
        }
    }

    /**
     * @param combinedSceneTextureId 已与原版合成后的场景颜色纹理
     * @return 最后一道效果输出纹理；若无 caster 则返回输入纹理
     */
    public int applyGeometricMaskEffects(
            RenderContext context,
            float timeSeconds,
            int combinedSceneTextureId,
            RenderTarget mainRenderTarget,
            List<GeometricEffectCaster> casters
    ) {
        if (casters == null || casters.isEmpty()) {
            return combinedSceneTextureId;
        }

        List<GeometricEffectCaster> sorted = new ArrayList<>(casters);
        Vector3d cam = context.getCamera().position();
        sorted.sort(Comparator.comparingDouble(c -> -distanceSq(c, cam)));

        FramebufferUtils.copyDepth(mainRenderTarget, maskFbo);

        int readTextureId = combinedSceneTextureId;
        for (int i = 0; i < sorted.size(); i++) {
            GeometricEffectCaster caster = sorted.get(i);
            boolean usePing = (i % 2) == 0;
            SingleTargetFramebuffer writeFbo = usePing ? pingFbo : pongFbo;

            renderMaskForCaster(context, timeSeconds, caster);
            runEffectPass(caster.getEffect(), readTextureId, writeFbo);
            readTextureId = writeFbo.getColorTextureId();
        }
        return readTextureId;
    }

    private static double distanceSq(GeometricEffectCaster c, Vector3d cam) {
        Matrix4f world = c.getPrecomputedWorldTransform().orElseGet(() -> c.getTransform().getModelMatrix());
        double dx = world.m30() - cam.x;
        double dy = world.m31() - cam.y;
        double dz = world.m32() - cam.z;
        return dx * dx + dy * dy + dz * dz;
    }

    private void renderMaskForCaster(RenderContext context, float timeSeconds, GeometricEffectCaster caster) {
        IGeometricMaskEffect effect = caster.getEffect();
        IMatrixStackProvider matrixProvider = context.getMatrixStackProvider();
        Vector3d cameraPos = context.getCamera().position();
        Matrix4f worldModelMatrix = caster.getPrecomputedWorldTransform()
                .orElseGet(() -> caster.getTransform().getModelMatrix());

        Matrix4f relativeModelMatrix = reusableMatrix.set(worldModelMatrix);
        float relativeX = (float) (worldModelMatrix.m30() - cameraPos.x);
        float relativeY = (float) (worldModelMatrix.m31() - cameraPos.y);
        float relativeZ = (float) (worldModelMatrix.m32() - cameraPos.z);
        relativeModelMatrix.setTranslation(relativeX, relativeY, relativeZ);

        Matrix4f projectionMatrix = context.getProjectionMatrix();
        Matrix4f view = matrixProvider.getViewMatrix();

        if (!updateProjectedCenterForDistortion(effect, projectionMatrix, view, relativeModelMatrix)) {
            maskFbo.bind();
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            maskFbo.unbind();
            return;
        }

        uniformContext.projection.set(projectionMatrix);
        uniformContext.view.set(view);
        uniformContext.modelRelative.set(relativeModelMatrix);
        uniformContext.timeSeconds = timeSeconds;
        uniformContext.maskFramebufferWidth = maskFbo.getWidth();
        uniformContext.maskFramebufferHeight = maskFbo.getHeight();

        Shader maskShader = effect.getMaskShader();
        maskFbo.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(false);

        maskShader.bind();
        maskShader.setUniformMatrix4f("u_projection", projectionMatrix);
        maskShader.setUniformMatrix4f("u_view", view);
        maskShader.setUniformMatrix4f("u_model", relativeModelMatrix);
        maskShader.setUniformValue1f("u_time", timeSeconds);
        effect.applyMaskShaderUniforms(maskShader, uniformContext);
        caster.getMesh().draw();
        maskShader.unbind();

        glDepthMask(true);
        maskFbo.unbind();
    }

    private boolean updateProjectedCenterForDistortion(IGeometricMaskEffect effect, Matrix4f projection, Matrix4f view, Matrix4f model) {
        if (!(effect instanceof DistortionGeometricMaskEffect d)) {
            return true;
        }
        reusableClipMatrix.set(projection).mul(view).mul(model);
        reusableClipPosition.set(0.0f, 0.0f, 0.0f, 1.0f);
        reusableClipMatrix.transform(reusableClipPosition);
        if (reusableClipPosition.w <= 0.0f) {
            return false;
        }
        float invW = 1.0f / reusableClipPosition.w;
        float ndcX = reusableClipPosition.x * invW;
        float ndcY = reusableClipPosition.y * invW;
        d.setProjectedCenterU(ndcX * 0.5f + 0.5f);
        d.setProjectedCenterV(ndcY * 0.5f + 0.5f);
        return true;
    }

    private void runEffectPass(IGeometricMaskEffect effect, int sceneTextureId, SingleTargetFramebuffer writeFbo) {
        Shader effectShader = effect.getEffectShader();
        writeFbo.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        effectShader.bind();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, sceneTextureId);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, maskFbo.getColorTextureId());
        effect.applyEffectShaderUniforms(effectShader);
        fullscreenQuad.draw();
        effectShader.unbind();

        writeFbo.unbind();
    }

    @Override
    public void close() throws Exception {
        if (pingFbo != null) {
            pingFbo.close();
        }
        if (pongFbo != null) {
            pongFbo.close();
        }
    }
}
