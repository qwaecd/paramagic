#version 430 core
#define BINDING_PARTICLE_STACK_TOP 0
#define BINDING_EFFECT_COUNTERS 3
#define BINDING_REQUESTS 4
#define BINDING_EMISSION_TASKS 5
#define BINDING_TASK_COUNT 6

layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

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

struct EmittionTask {
    uint numParticlesToInit;
    uint indexStackOffset;
    uint _padding0;
    uint _padding1;
    EmissionRequest request;
};

struct EffectMetaData {
    uint maxParticles;
    uint currentCount;
    uint _padding1;
    uint _padding2;
};

layout(binding = BINDING_PARTICLE_STACK_TOP, offset = 0) uniform atomic_uint deadListCounter;
layout(std430, binding = BINDING_EFFECT_COUNTERS) buffer EffectData {
    EffectMetaData effectData[];
};
// From CPU
layout(std430, binding = BINDING_REQUESTS) buffer ParticleRequests {
    EmissionRequest requests[];
};

// Pass to initialize shader
layout(std430, binding = BINDING_EMISSION_TASKS) buffer Tasks {
    EmittionTask emissionTasks[];
};
layout(binding = BINDING_TASK_COUNT, offset = 0) uniform atomic_uint successfulTaskCount;

uniform int u_requestCount;

void main() {
    // TODO: 需要重新修改计数逻辑以及栈空间分配
    uint invocationID = gl_GlobalInvocationID.x;

    if (invocationID > 0u || u_requestCount <= 0) {
        return;
    }

    atomicCounterExchange(successfulTaskCount, 0);
    barrier();

    for (int i = 0; i < u_requestCount; i++) {
        EmissionRequest req = requests[i];
        if (req.count <= 0) {
            continue;
        }

        uint effectId = uint(req.effectId);
        EffectMetaData meta = effectData[effectId];
        EmittionTask task = emissionTasks[atomicCounter(successfulTaskCount)];

        // check effect capacity
        if (meta.currentCount + uint(req.count) > meta.maxParticles) {
            task.numParticlesToInit = 0u;
            continue;
        }

        // check particle stack
        uint stackTop = atomicCounterAdd(deadListCounter, -req.count);
        if (stackTop < uint(req.count)) {
            // stackoverflow, revert
            atomicCounterAdd(deadListCounter, req.count);
            return;
        } else {
            // success
            atomicCounterAdd(successfulTaskCount, 1u);
            task.numParticlesToInit = req.count;
            task.indexStackOffset = stackTop - req.count;
            task.request = req;

            atomicAdd(meta.currentCount, uint(req.count));
        }
    }
}