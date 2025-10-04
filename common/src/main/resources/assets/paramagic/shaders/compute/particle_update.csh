#version 430 core
#define BINDING_GLOBAL_DATA 0
#define BINDING_PARTICLE_DATA 1
#define BINDING_DEAD_LIST 2
#define BINDING_EFFECT_COUNTERS 5
#define BINDING_EFFECT_PHYSICS_PARAMS 6

#define CF_EPS 1e-6

layout(local_size_x = 256, local_size_y = 1, local_size_z = 1) in;

struct GlobalCounters {
    uint deadListStackTop;  // Number of available (dead) particle slots
    uint successfulTaskCount;   // Number of tasks written so far
    uint _padding2;
    uint _padding3;
};

struct Particle {
    vec4 meta;        // x: effectId, y: unused, z: unused, w: unused
    vec4 position;    // x, y, z, mass(unused)
    vec4 velocity;    // vx, vy, vz, (unused)
    vec4 attributes;  // x: age, y: lifetime, z: current_anim_frame, w: anim_speed
    vec4 renderAttribs;  // x: size, y: angle, z: angular_velocity, w: bloom_intensity
    vec4 color; // rgba
};

struct EffectMetaData {
    uint maxParticles;
    uint currentCount;
    uint _padding1;
    uint _padding2;
};

struct EffectPhysicsParams {
    // F(r) = A * pow(r, B)
    vec4 centerForceParams; // x: A, y: B, z: maxRadius, w: enable (0 or 1)
    vec4 centerForcePos; // x, y, z: 力场中心位置, w: dragCoefficient (阻力系数), acceleration -= velocity * dragCoefficient;
    vec4 linearForce; // x, y, z: 线性力 (e.g. gravity + wind), w: enable (0 or 1)
};

layout(std430, binding = BINDING_GLOBAL_DATA) buffer Globals {
    GlobalCounters globalData;
};
layout(std430, binding = BINDING_PARTICLE_DATA) buffer ParticleData {
    Particle particles[];
};
layout(std430, binding = BINDING_DEAD_LIST) buffer DeadList {
    uint deadList[];
};
layout(std430, binding = BINDING_EFFECT_COUNTERS) buffer EffectData {
    EffectMetaData effectData[];
};
layout(std430, binding = BINDING_EFFECT_PHYSICS_PARAMS) buffer PhysicsParams {
    EffectPhysicsParams physicsParams[];
};

uniform int u_maxParticles;
uniform float u_deltaTime;
uniform int u_maxEffectCount;

void applyForce(uint idx) {
    uint effectId = uint(particles[idx].meta.x);
    if (effectId < 0 || effectId >= uint(u_maxEffectCount)) {
        return;
    }
    // apply center force
    vec3 pos = particles[idx].position.xyz;
    vec3 toCenter = physicsParams[effectId].centerForcePos.xyz - pos;
    float r = length(toCenter);
    if (r > CF_EPS && physicsParams[effectId].centerForceParams.w > 0.5 && r < physicsParams[effectId].centerForceParams.z) {
        vec3 dir = toCenter / r;
        float fmag = physicsParams[effectId].centerForceParams.x * pow(r, physicsParams[effectId].centerForceParams.y);
        // no mass
        particles[idx].velocity.xyz += dir * fmag * u_deltaTime;
    }
    // apply linear force
    particles[idx].velocity.xyz += physicsParams[effectId].linearForce.xyz * physicsParams[effectId].linearForce.w;
    // apply drag
    particles[idx].velocity.xyz -= particles[idx].velocity.xyz * physicsParams[effectId].centerForcePos.w * u_deltaTime;

    // integrate
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
    float newAge = age + u_deltaTime;

    if (newAge >= lifetime) {
        // just died
        particles[idx].attributes.x = lifetime;
        uint deadIdx = atomicAdd(globalData.deadListStackTop, 1u);
        deadList[deadIdx] = idx;
        atomicAdd(effectData[uint(particles[idx].meta.x)].currentCount, uint(-1));
        return;
    }
    particles[idx].attributes.x = newAge;
    update(idx);
}