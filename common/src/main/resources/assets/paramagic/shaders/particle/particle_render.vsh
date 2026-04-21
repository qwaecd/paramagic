#version 430 core
#define BINDING_PARTICLE_DATA 1
#define BINDING_EFFECT_META_DATA 3
#define BINDING_BUCKET_POINT_INDICES 8
#define BINDING_BUCKET_TRIANGLE_INDICES 9
#define BINDING_BUCKET_QUAD_INDICES 10

struct Particle {
    vec4 meta;        // x: effectId, y: primitiveType(bits in float), z: facingMode(bits in float), w: unused
    vec4 position;    // x, y, z, mass(unused)
    vec4 velocity;    // vx, vy, vz, normal.x (when facingMode=normal)
    vec4 attributes;  // x: age, y: lifetime, z: normal.y, w: normal.z
    vec4 renderAttribs;  // x: size, y: angle, z: angular_velocity, w: bloom_intensity
    vec4 color; // rgba
};

struct EffectMetaData {
    uint maxParticles;
    uint currentCount;
    uint flags;
    uint _padding2;
    mat4 modelMatrix;
};

layout(std430, binding = BINDING_PARTICLE_DATA) buffer ParticleData {
    Particle particles[];
};

layout(std430, binding = BINDING_EFFECT_META_DATA) buffer EffectData {
    EffectMetaData effectData[];
};

layout(std430, binding = BINDING_BUCKET_POINT_INDICES) buffer PointBucket {
    uint pointIndices[];
};

layout(std430, binding = BINDING_BUCKET_TRIANGLE_INDICES) buffer TriangleBucket {
    uint triangleIndices[];
};

layout(std430, binding = BINDING_BUCKET_QUAD_INDICES) buffer QuadBucket {
    uint quadIndices[];
};

uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;
uniform vec3 u_cameraPosition;
uniform int u_bucketType; // 0=point,1=triangle,2=quad

out ParticleVaryings {
    vec4 color;
    float intensity;
    float size;
    float angle;
    vec3 centerView;
    vec3 normalView;
    flat uint facingMode;
} particleOut;

uint resolveParticleIndex(uint bucketSlot) {
    if (u_bucketType == 1) {
        return triangleIndices[bucketSlot];
    }
    if (u_bucketType == 2) {
        return quadIndices[bucketSlot];
    }
    return pointIndices[bucketSlot];
}

void main() {
    uint particleIndex = resolveParticleIndex(uint(gl_VertexID));
    Particle particle = particles[particleIndex];

    if (particle.attributes.x >= particle.attributes.y) {
        gl_Position = vec4(-2000.0, -2000.0, -2000.0, 1.0);
        gl_PointSize = 0.0;
        particleOut.color = vec4(0.0);
        particleOut.intensity = 0.0;
        particleOut.size = 0.0;
        particleOut.angle = 0.0;
        particleOut.centerView = vec3(0.0);
        particleOut.normalView = vec3(0.0);
        particleOut.facingMode = 0u;
        return;
    }

    particleOut.color = particle.color;
    particleOut.intensity = particle.renderAttribs.w;
    particleOut.size = particle.renderAttribs.x;
    particleOut.angle = particle.renderAttribs.y;

    uint effectId = uint(particle.meta.x);
    vec4 worldPosition = effectData[effectId].modelMatrix * vec4(particle.position.xyz, 1.0);
    vec3 vertexPositionRelative = worldPosition.xyz - u_cameraPosition;
    vec4 centerView = u_viewMatrix * vec4(vertexPositionRelative, 1.0);
    particleOut.centerView = centerView.xyz;

    particleOut.facingMode = floatBitsToUint(particle.meta.z);
    vec3 localNormal = vec3(particle.velocity.w, particle.attributes.z, particle.attributes.w);
    vec3 worldNormal = mat3(effectData[effectId].modelMatrix) * localNormal;
    vec3 viewNormal = (u_viewMatrix * vec4(worldNormal, 0.0)).xyz;
    float nLen = length(viewNormal);
    particleOut.normalView = (nLen > 1e-6) ? (viewNormal / nLen) : vec3(0.0);

    gl_Position = u_projectionMatrix * centerView;
    gl_PointSize = max(particleOut.size, 0.0);
}
