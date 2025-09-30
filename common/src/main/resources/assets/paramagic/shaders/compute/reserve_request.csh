#version 430 core
#define BINDING_PARTICLE_STACK_TOP 0
#define BINDING_EFFECT_COUNTERS 3
#define BINDING_REQUESTS 4
#define BINDING_EMITTION_TASKS 5
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

layout(binding = BINDING_PARTICLE_STACK_TOP, offset = 0) uniform atomic_uint deadListCounter;
layout(std430, binding = BINDING_EFFECT_COUNTERS) buffer EffectCounters {
    atomic_uint effectCounters[];
};
// From CPU
layout(std430, binding = BINDING_REQUESTS) buffer ParticleRequests {
    EmissionRequest requests[];
};

// Pass to initialize shader
layout(std430, binding = BINDING_EMITTION_TASKS) buffer Tasks {
    EmittionTask emittionTasks[];
};
layout(binding = BINDING_TASK_COUNT, offset = 0) uniform atomic_uint successfulTaskCount;

uniform int u_requestCount;

void main() {
    atomicCounterExchange(successfulTaskCount, 0);
    barrier();

    if (u_requestCount <= 0) {
        return;
    }

    for (int i = 0; i < u_requestCount; i++) {
        EmissionRequest req = requests[i];

        uint stackTop = atomicCounterAdd(deadListCounter, -N);
        if (stackTop < uint(N)) {
            // stackoverflow, revert
            atomicCounterAdd(deadListCounter, N);
            args.numParticlesToInit = 0;
        } else {
            args.numParticlesToInit = N;
            args.indexStackOffset = stackTop - N;
            args.request = req;
        }
    }
}