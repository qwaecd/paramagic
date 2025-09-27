#version 430 core
#define BINDING_ATOMIC_COUNTER 0
#define BINDING_PARTICLE_DATA 1
#define BINDING_DEAD_LIST 2
#define CF_EPS 1e-6

layout(local_size_x = 256, local_size_y = 1, local_size_z = 1) in;

struct Particle {
    vec4 position;    // x, y, z, mass(unused)
    vec4 velocity;    // vx, vy, vz, (unused)
    vec4 attributes;  // x: age, y: lifetime, z: current_anim_frame, w: anim_speed
    vec4 renderAttribs;  // x: size, y: angle, z: angular_velocity, w: bloom_intensity
    vec4 color; // rgba
};

layout(binding = BINDING_ATOMIC_COUNTER, offset = 0) uniform atomic_uint deadListCounter;
layout(std430, binding = BINDING_PARTICLE_DATA) buffer ParticleData {
    Particle particles[];
};
layout(std430, binding = BINDING_DEAD_LIST) buffer DeadList {
    uint deadList[];
};
uniform int u_maxParticles;
uniform float u_deltaTime;
// center force parameters
// CF = A * r ^ B
uniform float CF_A;
uniform float CF_B;
uniform vec3 u_centerForcePos;

void applyForce(uint idx) {
    vec3 pos = particles[idx].position.xyz;
    vec3 toCenter = u_centerForcePos - pos;
    float r = length(toCenter);
    if (r > CF_EPS) {
        vec3 dir = toCenter / r;
        float fmag = CF_A * pow(r, CF_B);
        // no mass
        particles[idx].velocity.xyz += dir * fmag * u_deltaTime;
    }
    particles[idx].position.xyz += particles[idx].velocity.xyz * u_deltaTime;
}

void update(uint idx) {
    applyForce(idx);
}

void main() {
    uint idx = gl_GlobalInvocationID.x;

    if (idx >= uint(u_maxParticles)) {
        return;
    }

    float age = particles[idx].attributes.x;
    const float lifetime = particles[idx].attributes.y;

    if (age >= lifetime) {
        // died
        return;
    }
    particles[idx].attributes.x += u_deltaTime;

    if (particles[idx].attributes.x >= lifetime) {
        // just died
        uint deadIdx = atomicCounterAdd(deadListCounter, 1);
        deadList[deadIdx] = idx;
        return;
    }
    update(idx);
}