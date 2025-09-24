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
void main() {
    uint idx = gl_GlobalInvocationID.x;
    if (idx >= uint(u_numParticles))
        return;
    // 更新速度
    velocities[idx].xyz += u_gravity * u_deltaTime;
    // 更新位置
    positions[idx].xyz += velocities[idx].xyz * u_deltaTime;
}
