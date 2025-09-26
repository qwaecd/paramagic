package com.qwaecd.paramagic.tools;

import com.qwaecd.paramagic.Paramagic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import static org.lwjgl.opengl.GL20.glGetInteger;
import static org.lwjgl.opengl.GL32.GL_MAX_GEOMETRY_OUTPUT_VERTICES;
import static org.lwjgl.opengl.GL43.GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS;

/**
 * 基于真实 OpenGL Capabilities / 扩展的着色器能力检测工具。
 * 与仅凭 OpenGL 版本号的粗略判断相比，此工具通过 capabilities + 硬件查询更精确判断几何 / 计算着色器是否可用。
 * 使用前提：必须在已创建有效 OpenGL 上下文（Minecraft 客户端渲染线程）后调用。
 */
public final class ShaderCapabilityChecker {
    private static CapabilityReport cachedReport;

    private ShaderCapabilityChecker() {}

    /**
     * 结果快照，避免上层多次做昂贵检查。
     */
    public static final class CapabilityReport {
        public final boolean geometrySupported;
        public final boolean computeSupported;
        public final String geometryReason;
        public final String computeReason;

        private CapabilityReport(boolean geometrySupported, boolean computeSupported, String geometryReason, String computeReason) {
            this.geometrySupported = geometrySupported;
            this.computeSupported = computeSupported;
            this.geometryReason = geometryReason;
            this.computeReason = computeReason;
        }

        @Override
        public String toString() {
            return "CapabilityReport{" +
                    "geometrySupported=" + geometrySupported +
                    ", computeSupported=" + computeSupported +
                    ", geometryReason='" + geometryReason + '\'' +
                    ", computeReason='" + computeReason + '\'' +
                    '}';
        }
    }

    /**
     * 运行一次检测（若已有缓存直接返回）。
     */
    public static synchronized CapabilityReport detect() {
        if (cachedReport != null) {
            return cachedReport;
        }
        GLCapabilities caps = GL.getCapabilities();
        if (caps == null) {
            CapabilityReport report = new CapabilityReport(false, false, "GLCapabilities null (context not current)", "GLCapabilities null (context not current)");
            cachedReport = report;
            Paramagic.LOG.warn("[ShaderCapabilityChecker] GL context not ready when detecting capabilities.");
            return report;
        }

        // Geometry shader 条件：Core 3.2 / ARB_geometry_shader4 / EXT_geometry_shader4 任一存在 + 合法的最大输出顶点查询
        boolean geomFlag = caps.OpenGL32 || caps.GL_ARB_geometry_shader4 || caps.GL_EXT_geometry_shader4;
        String geomReason;
        boolean geometrySupported = false;
        if (!geomFlag) {
            geomReason = "Missing core 3.2 and geometry shader extensions";
        } else {
            int maxOut = 0;
            try {
                maxOut = glGetInteger(GL_MAX_GEOMETRY_OUTPUT_VERTICES); // 若不支持会抛错或返回 0
            } catch (Throwable t) {
                // ignore
            }
            if (maxOut > 0) {
                geometrySupported = true;
                geomReason = "Capability ok (maxOut=" + maxOut + ")";
            } else {
                geomReason = "GL_MAX_GEOMETRY_OUTPUT_VERTICES <= 0";
            }
        }

        // Compute shader 条件：Core 4.3 / ARB_compute_shader
        boolean computeFlag = caps.OpenGL43 || caps.GL_ARB_compute_shader;
        String computeReason;
        boolean computeSupported = false;
        if (!computeFlag) {
            computeReason = "Missing core 4.3 and ARB_compute_shader";
        } else {
            int invocations = 0;
            try {
                invocations = glGetInteger(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS);
            } catch (Throwable t) {
                // ignore
            }
            if (invocations > 0) {
                computeSupported = true;
                computeReason = "Capability ok (maxInvocations=" + invocations + ")";
            } else {
                computeReason = "GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS <= 0";
            }
        }

        cachedReport = new CapabilityReport(geometrySupported, computeSupported, geomReason, computeReason);
        Paramagic.LOG.info("[ShaderCapabilityChecker] {}", cachedReport);
        return cachedReport;
    }

    public static boolean isGeometrySupported() {
        return detect().geometrySupported;
    }

    public static boolean isComputeSupported() {
        return detect().computeSupported;
    }

    /**
     * 允许在需要重新检测（例如显卡热插拔？极少见）时手动失效缓存。
     */
    public static synchronized void invalidateCache() {
        cachedReport = null;
    }
}

