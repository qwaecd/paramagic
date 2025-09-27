#version 430 core
#define BINDING_ATOMIC_COUNTER 0
#define BINDING_PARTICLE_DATA 1
#define BINDING_DEAD_LIST 2
#define BINDING_REQUESTS 3
#define BINDING_DISPATCH_ARGS 4

layout(local_size_x = 256, local_size_y = 1, local_size_z = 1) in;

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
    vec4 position;    // x, y, z, mass(unused)
    vec4 velocity;    // vx, vy, vz, (unused)
    vec4 attributes;  // x: age, y: lifetime, z: current_anim_frame, w: anim_speed
    vec4 renderAttribs;  // x: size, y: angle, z: angular_velocity, w: bloom_intensity
    vec4 color; // rgba
};

struct DispatchArgs {
    uint numParticlesToInit;
    uint indexStackOffset;
    EmissionRequest request;
};

layout(binding = BINDING_ATOMIC_COUNTER, offset = 0) uniform atomic_uint deadListCounter;
layout(std430, binding = BINDING_REQUESTS) buffer ParticleRequests {
    EmissionRequest requests[];
};
layout(std430, binding = BINDING_PARTICLE_DATA) buffer ParticleData {
    Particle particles[];
};
layout(std430, binding = BINDING_DEAD_LIST) buffer DeadList {
    uint deadList[];
};
layout(std430, binding = BINDING_DISPATCH_ARGS) readonly buffer IndirectDispatchArgs {
    DispatchArgs args;
};

subroutine void EmitterInitializer(uint particleIndex, EmissionRequest req);
subroutine uniform EmitterInitializer u_emitterInitializerFunc;

void main() {
    uint invocationID = gl_GlobalInvocationID.x;

    if (invocationID >= args.numParticlesToInit) {
        return;
    }

    uint deadListIndex = args.indexStackOffset + invocationID;
    uint particleIndex = deadList[deadListIndex];
    EmissionRequest req = args.request;

    u_emitterInitializerFunc(particleIndex, req);
}

subroutine(EmitterInitializer)
void PointEmitterInitializer(uint particleIndex, EmissionRequest req) {
    // TODO: implement emitter
}