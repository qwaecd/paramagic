#version 430 core
#define BINDING_ATOMIC_COUNTER 0
#define BINDING_REQUESTS 3
#define BINDING_DISPATCH_ARGS 4

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

struct DispatchArgs {
    uint numParticlesToInit;
    uint indexStackOffset;
    EmissionRequest request;
};

layout(binding = BINDING_ATOMIC_COUNTER, offset = 0) uniform atomic_uint deadListCounter;
layout(std430, binding = BINDING_REQUESTS) buffer ParticleRequests {
    EmissionRequest requests[];
};
layout(std430, binding = BINDING_DISPATCH_ARGS) buffer IndirectDispatchArgs {
    DispatchArgs args;
};


void main() {
    uint idx = gl_GlobalInvocationID.x;
    EmissionRequest req = requests[0];
    const int N = req.count;

    if (N <= 0) {
        return;
    }

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