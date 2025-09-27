#version 430 core
#define BINDING_ATOMIC_COUNTER 0
#define BINDING_PARTICLE_DATA 1
#define BINDING_DEAD_LIST 2
layout(local_size_x = 256, local_size_y = 1, local_size_z = 1) in;

struct Particle {
    vec4 position;    // x, y, z, mass(unused)
    vec4 velocity;    // vx, vy, vz, (unused)
    vec4 attributes;  // x: age, y: lifetime, z: current_anim_frame, w: anim_speed
    vec4 renderAttribs;  // x: size, y: angle, z: angular_velocity, w: bloom_intensity
    vec4 color;
};

layout(binding = BINDING_ATOMIC_COUNTER, offset = 0) uniform atomic_uint deadListCounter;
layout(std430, binding = BINDING_PARTICLE_DATA) buffer ParticleData {
    Particle particles[];
};
layout(std430, binding = BINDING_DEAD_LIST) buffer DeadList {
    uint deadList[];
};

void main() {
    uint idx = gl_GlobalInvocationID.x;
}