#version 430
layout(local_size_x = 256, local_size_y = 1, local_size_z = 1) in;
layout(std430, binding = 0) buffer Positions {
    vec4 positions[];
};
layout(std430, binding = 1) buffer Velocities {
    vec4 velocities[];
};
uniform int u_numParticles;
uniform float u_deltaTime;
uniform vec3 u_gravity;

const vec3 CF_CENTER = vec3(0.0, 80.0, 10.0);
const float CF_A = 0.06;   // 力系数 A
const float CF_B = -2.0;        // 幂指数 B: 力大小 = A * r^B
const float CF_EPS = 1e-6;     // 避免 r=0 数值问题

void main() {
    uint idx = gl_GlobalInvocationID.x;
    if (idx >= uint(u_numParticles)) return;

    // 当前位置
    vec3 pos = positions[idx].xyz;
    vec3 toCenter = CF_CENTER - pos;
    float r = length(toCenter);
    if (r > CF_EPS) {
        vec3 dir = toCenter / r; // 指向中心的单位向量
        float fmag = CF_A * pow(r, CF_B); // 力大小（可正可负）
        // 加速度 ~ 力，这里直接将力视为加速度（若需质量，可再除以质量）
        velocities[idx].xyz += dir * fmag * u_deltaTime;
    }

    // 原先的重力更新已移除，如需叠加，可取消下行注释：
    // velocities[idx].xyz += u_gravity * u_deltaTime;

    // 更新位置
    positions[idx].xyz += velocities[idx].xyz * u_deltaTime;
}
