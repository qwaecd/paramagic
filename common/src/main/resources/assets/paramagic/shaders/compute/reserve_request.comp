#version 430 core
#define BINDING_GLOBAL_DATA 0
#define BINDING_EFFECT_META_DATA 3
#define BINDING_REQUESTS 4
#define BINDING_EMISSION_TASKS 5

layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

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

struct EmissionTask {
    uint numParticlesToInit;
    uint indexStackOffset;
    uint _padding0;
    uint _padding1;
    EmissionRequest request;
};

struct EffectMetaData {
    uint maxParticles;
    uint currentCount;
    uint flags;
    uint _padding2;
};

layout(std430, binding = BINDING_GLOBAL_DATA) buffer Globals {
    GlobalCounters globalData;
};
layout(std430, binding = BINDING_EFFECT_META_DATA) buffer EffectData {
    EffectMetaData effectData[];
};
// From CPU
layout(std430, binding = BINDING_REQUESTS) buffer ParticleRequests {
    EmissionRequest requests[];
};

// Pass to initialize shader
layout(std430, binding = BINDING_EMISSION_TASKS) buffer Tasks {
    EmissionTask emissionTasks[];
};

uniform int u_requestCount;

// NOTE: This emission pass is SINGLE-THREADED.
// If you change glDispatchCompute to more than (1,1,1),
// you MUST enable the parallel allocation path with CAS protection.
void main() {
    uint invocationID = gl_GlobalInvocationID.x;

    if (invocationID != 0u || u_requestCount <= 0) {
        return;
    }

    globalData.successfulTaskCount = 0u;

    for (int i = 0; i < u_requestCount; ++i) {
        EmissionRequest req = requests[uint(i)];
        // no need to process invalid requests
        if (req.count <= 0) {
            continue;
        }

        // invaild effect id
        if (req.effectId < 0) {
            continue;
        }
        uint eid = uint(req.effectId);
        // exceed max particles in this effect
        if (uint(req.count) + effectData[eid].currentCount > effectData[eid].maxParticles) {
            continue;
        }

        if (globalData.deadListStackTop < uint(req.count)) {
            // not enough particles in the dead list
            continue;
        }

        // success
        uint start = globalData.deadListStackTop - uint(req.count);
        globalData.deadListStackTop -= uint(req.count);
        effectData[eid].currentCount += uint(req.count);


        uint taskIndex = globalData.successfulTaskCount;
        emissionTasks[taskIndex].numParticlesToInit = uint(req.count);
        emissionTasks[taskIndex].indexStackOffset   = start;
        emissionTasks[taskIndex].request            = req;
        globalData.successfulTaskCount += 1u;
    }
}
