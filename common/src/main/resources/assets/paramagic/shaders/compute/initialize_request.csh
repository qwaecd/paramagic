#version 430 core
#define BINDING_GLOBAL_DATA 0
#define BINDING_PARTICLE_DATA 1
#define BINDING_DEAD_LIST 2
#define BINDING_REQUESTS 4
#define BINDING_EMISSION_TASKS 5
#define LOCAL_SIZE_X 256

#define POINT_EMITTER 1

#define PI 3.141592653

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

uniform float u_randomSeed;

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

float random(float offset) {
    uint seed = floatBitsToUint(u_randomSeed + offset);
    seed ^= gl_GlobalInvocationID.x * 0x9E3779B1u;
    seed ^= gl_GlobalInvocationID.y * 0x85EBCA77u;
    seed ^= gl_GlobalInvocationID.z * 0xC2B2AE3Du;
    seed ^= gl_WorkGroupID.x        * 0x27D4EB2Fu;
    seed ^= gl_WorkGroupID.y        * 0x165667B1u;
    seed ^= gl_WorkGroupID.z        * 0xD3A2646Cu;
    seed ^= gl_LocalInvocationID.x  * 0x94D049BBu;

    // Finalize with strong mixing and map to (0,1)
    return uintToUnitExclusive(hashUint(seed));
}
float randomFloatInRange(float min, float max, float offset) {
    return random(offset) * (max - min) + min;
}
// --- End random utilities ---
/**
 * 从单个向量 a_axis 构建一个标准正交基（一个坐标系）
 * a_axis:      输入的向量，将成为新坐标系的 Z 轴
 * out_axis_x:  输出的 X 轴
 * out_axis_y:  输出的 Y 轴
 * out_axis_z:  输出的 Z 轴（归一化后的 a_axis）
 */
void buildOrthonormalBasis(vec3 a_axis, out vec3 out_axis_x, out vec3 out_axis_y, out vec3 out_axis_z) {
    out_axis_z = normalize(a_axis);

    vec3 temp_up;
    if (abs(out_axis_z.y) > 0.999) {
        temp_up = vec3(1.0, 0.0, 0.0);
    } else {
        temp_up = vec3(0.0, 1.0, 0.0);
    }

    out_axis_x = normalize(cross(temp_up, out_axis_z));
    out_axis_y = cross(out_axis_z, out_axis_x);
}

/**
 * struct Particle {
 *   vec4 meta;     // x: effectId, y: unused, z: unused, w: unused
 *   vec4 position;    // x, y, z, mass
 *   vec4 velocity;    // vx, vy, vz, (unused)
 *   vec4 attributes;  // x: age, y: lifetime, z: current_anim_frame, w: anim_speed
 *   vec4 renderAttribs;  // x: size, y: angle, z: angular_velocity, w: bloom_intensity
 *   vec4 color;
 * };
 */
void pointEmitter(uint particleIndex, EmissionRequest req) {
    // Gram-Schmidt process
    vec3 axisX, axisY, axisZ;
    buildOrthonormalBasis(req.param2.xyz, axisX, axisY, axisZ);
    float phi = random(0.0) * 2.0 * PI;
    float cos_theta = mix(cos(radians(req.param5.x)), 1.0, random(1.0));
    float sin_theta = sqrt(1.0 - cos_theta * cos_theta);
    float x = sin_theta * cos(phi);
    float y = sin_theta * sin(phi);
    float z = cos_theta;
    vec3 local_dir = vec3(x, y, z);
    vec3 random_dir_world = local_dir.x * axisX + local_dir.y * axisY + local_dir.z * axisZ;
    vec3 initial_velocity = random_dir_world * length(req.param2.xyz);


    particles[particleIndex].meta = vec4(float(req.effectId), 0.0, 0.0, 0.0);
    particles[particleIndex].position = req.param1;
    particles[particleIndex].velocity = vec4(initial_velocity, req.param2.w);
    particles[particleIndex].attributes = vec4(0.0, randomFloatInRange(req.param4.x, req.param4.y, 2.0), 0.0, 0.0);
    particles[particleIndex].renderAttribs = vec4(randomFloatInRange(req.param4.z, req.param4.w, 3.0), 0.0, 0.0, req.param5.y);
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

        switch(task.request.emitterType) {
            case POINT_EMITTER: pointEmitter(particleId, task.request); break;
        }
    }
}

subroutine(EmitterInitializer)
void pointEmitterInitializer(uint particleIndex, EmissionRequest req) {
    return;
}
