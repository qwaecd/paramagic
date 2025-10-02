#version 430 core
#define BINDING_GLOBAL_DATA 0
#define BINDING_PARTICLE_DATA 1
#define BINDING_DEAD_LIST 2
#define BINDING_REQUESTS 4
#define BINDING_EMISSION_TASKS 5
#define LOCAL_SIZE_X 256

layout(local_size_x = LOCAL_SIZE_X, local_size_y = 1, local_size_z = 1) in;

struct GlobalCounters {
    uint deadListStackTop;  // Number of available (dead) particle slots
    uint successfulTaskCount;   // Number of tasks written so far
    uint _padding2;
    uint _padding3;
};

struct EmissionRequest {
    int count;
    int emitterType;
    int effectId;
    int _padding;

    vec4 param1;
    vec4 param2;
    vec4 param3;
    vec4 param4;
    vec4 param5;
};

struct Particle {
    vec4 meta;        // x: effectId, y: unused, z: unused, w: unused
    vec4 position;    // x, y, z, mass(unused)
    vec4 velocity;    // vx, vy, vz, (unused)
    vec4 attributes;  // x: age, y: lifetime, z: current_anim_frame, w: anim_speed
    vec4 renderAttribs;  // x: size, y: angle, z: angular_velocity, w: bloom_intensity
    vec4 color; // rgba
};

struct EmissionTask {
    uint numParticlesToInit;
    uint indexStackOffset;
    uint _padding0;
    uint _padding1;
    EmissionRequest request;
};

layout(std430, binding = BINDING_GLOBAL_DATA) buffer Globals {
    GlobalCounters globalData;
};
layout(std430, binding = BINDING_REQUESTS) buffer ParticleRequests {
    EmissionRequest requests[];
};
layout(std430, binding = BINDING_PARTICLE_DATA) buffer ParticleData {
    Particle particles[];
};
layout(std430, binding = BINDING_DEAD_LIST) buffer DeadList {
    uint deadList[];
};
layout(std430, binding = BINDING_EMISSION_TASKS) buffer Tasks {
    EmissionTask emissionTasks[];
};

subroutine void EmitterInitializer(uint particleIndex, EmissionRequest req);
subroutine uniform EmitterInitializer u_emitterInitializerFunc;

// --- Random utilities ---
// 32-bit mix hash (variant of Wang/Jenkins style) producing good bit diffusion.
uint hashUint(uint x) {
    x ^= x >> 16;
    x *= 0x7feb352du;
    x ^= x >> 15;
    x *= 0x846ca68bu;
    x ^= x >> 16;
    return x;
}

// Convert hashed uint to float strictly inside (0,1) by adding 0.5 then dividing by 2^32.
float uintToUnitExclusive(uint h) {
    return (float(h) + 0.5) / 4294967296.0; // 4294967296 = 2^32
}

// Stateless random in (0,1), exclusive of boundaries, seeded from global + workgroup IDs.
// return value ~ U(0,1)
float random() {
    uint seed = gl_GlobalInvocationID.x
              ^ (gl_GlobalInvocationID.y << 10)
              ^ (gl_GlobalInvocationID.z << 20)
              ^ (gl_WorkGroupID.x * 747796405u)
              ^ (gl_WorkGroupID.y * 2891336453u)
              ^ (gl_WorkGroupID.z * 1181783497u);
    return uintToUnitExclusive(hashUint(seed));
}
// --- End random utilities ---

void debugInit(uint particleIndex, EmissionRequest req) {
    particles[particleIndex].meta = vec4(float(req.effectId), 0.0, 0.0, 0.0);
    particles[particleIndex].position = req.param1;
    particles[particleIndex].velocity = req.param2;
    particles[particleIndex].attributes = vec4(0.0, random() * (req.param4.y - req.param4.x) + req.param4.x, 0.0, 0.0);
    particles[particleIndex].renderAttribs = vec4(req.param4.w, 0.0, 0.0, 1.0);
    particles[particleIndex].color = req.param3;
}

void main() {
    uint workGroupID = gl_WorkGroupID.x;

    if (workGroupID >= globalData.successfulTaskCount) {
        return;
    }

    EmissionTask task = emissionTasks[workGroupID];
    for (uint i = 0u; i < task.numParticlesToInit; i += uint(LOCAL_SIZE_X)) {
        uint idxInTask = i + gl_LocalInvocationID.x;
        if (idxInTask >= task.numParticlesToInit) {
            break;
        }
        uint particleId = deadList[task.indexStackOffset + idxInTask];
//        u_emitterInitializerFunc(particleId, task.request);
        debugInit(particleId, task.request);
    }
}

subroutine(EmitterInitializer)
void pointEmitterInitializer(uint particleIndex, EmissionRequest req) {
//struct EmissionRequest {
//    int count;
//    int emitterType;
//    int effectId;
//    int _padding;
//
//    vec4 param1;
//    vec4 param2;
//    vec4 param3;
//    vec4 param4;
//    vec4 param5;
//};
//struct Particle {
//    vec4 meta;        // x: effectId, y: unused, z: unused, w: unused
//    vec4 position;    // x, y, z, mass(unused)
//    vec4 velocity;    // vx, vy, vz, (unused)
//    vec4 attributes;  // x: age, y: lifetime, z: current_anim_frame, w: anim_speed
//    vec4 renderAttribs;  // x: size, y: angle, z: angular_velocity, w: bloom_intensity
//    vec4 color; // rgba
//};
}