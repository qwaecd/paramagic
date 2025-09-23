#version 430
layout(local_size_x = 256) in;
layout(std430, binding = 0) buffer Positions {
    vec4 positions[];
};
layout(std430, binding = 1) buffer Velocities {
    vec4 velocities[];
};
uniform uint u_numParticles;
uniform float u_dt;
uniform vec3 u_gravity;
void main() {
    uint idx = gl_GlobalInvocationID.x;
    if (idx >= u_numParticles)
        return;
    // 更新速度
    velocities[idx].xyz += u_gravity * u_dt;
    // 更新位置
    positions[idx].xyz += velocities[idx].xyz * u_dt;
}
