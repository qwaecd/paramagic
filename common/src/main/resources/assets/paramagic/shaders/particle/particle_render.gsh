#version 430 core
layout(points) in;
layout(triangle_strip, max_vertices = 4) out;

in ParticleVaryings {
    vec4 color;
    float intensity;
    float size;
    float angle;
    vec3 centerView;
    vec3 normalView;
    flat uint particleSeed;
    flat uint shapeFlags;
    flat uint facingMode;
} particleIn[];

uniform mat4 u_projectionMatrix;
uniform int u_bucketType; // 1=triangle, 2=quad

out ParticleVaryings {
    vec4 color;
    float intensity;
    float size;
    float angle;
    vec3 centerView;
    vec3 normalView;
    flat uint particleSeed;
    flat uint shapeFlags;
    flat uint facingMode;
} particleOut;

const int BUCKET_TRIANGLE = 1;
const int BUCKET_QUAD = 2;
const float SQRT3_OVER_2 = 0.86602540378;
const uint PARTICLE_FACING_CAMERA = 0u;
const uint PARTICLE_FACING_NORMAL = 1u;
const float NORMAL_EPS = 1e-6;
const uint SHAPE_MODE_MASK = 0x3u;
const uint SHAPE_MODE_FIXED = 0u;
const uint SHAPE_MODE_JITTERED = 1u;
const float JITTER_STRENGTH = 0.8;

vec2 rotate2D(vec2 p, float angle) {
    float c = cos(angle);
    float s = sin(angle);
    return vec2(c * p.x - s * p.y, s * p.x + c * p.y);
}

uint hashUint(uint x) {
    x ^= x >> 16;
    x *= 0x7feb352du;
    x ^= x >> 15;
    x *= 0x846ca68bu;
    x ^= x >> 16;
    return x;
}

float uintToSignedUnit(uint h) {
    return ((float(h) + 0.5) / 4294967296.0) * 2.0 - 1.0;
}

bool useJitteredShape() {
    return (particleIn[0].shapeFlags & SHAPE_MODE_MASK) == SHAPE_MODE_JITTERED;
}

vec2 randomJitter(uint particleSeed, uint vertexIndex, float halfSize) {
    uint seedA = particleSeed ^ (0x9E3779B9u * (vertexIndex * 2u + 1u));
    uint seedB = particleSeed ^ (0x85EBCA77u * (vertexIndex * 2u + 2u));
    vec2 jitterDir = vec2(uintToSignedUnit(hashUint(seedA)), uintToSignedUnit(hashUint(seedB)));
    return jitterDir * (halfSize * JITTER_STRENGTH);
}

void emitViewVertex(vec3 viewPos) {
    gl_Position = u_projectionMatrix * vec4(viewPos, 1.0);
    particleOut.color = particleIn[0].color;
    particleOut.intensity = particleIn[0].intensity;
    particleOut.size = particleIn[0].size;
    particleOut.angle = particleIn[0].angle;
    particleOut.centerView = viewPos;
    particleOut.normalView = particleIn[0].normalView;
    particleOut.particleSeed = particleIn[0].particleSeed;
    particleOut.shapeFlags = particleIn[0].shapeFlags;
    particleOut.facingMode = particleIn[0].facingMode;
    EmitVertex();
}

void resolveAxes(out vec3 axisX, out vec3 axisY) {
    if (particleIn[0].facingMode == PARTICLE_FACING_NORMAL) {
        vec3 n = particleIn[0].normalView;
        float nLen = length(n);
        if (nLen > NORMAL_EPS) {
            n /= nLen;
            vec3 ref = (abs(n.y) > 0.99) ? vec3(1.0, 0.0, 0.0) : vec3(0.0, 1.0, 0.0);
            axisX = normalize(cross(ref, n));
            axisY = normalize(cross(n, axisX));
            return;
        }
    }
    // camera-facing fallback
    axisX = vec3(1.0, 0.0, 0.0);
    axisY = vec3(0.0, 1.0, 0.0);
}

void emitTriangle(vec3 centerView, float halfSize, float angle, vec3 axisX, vec3 axisY) {
    vec2 p0 = vec2(0.0, 1.0) * halfSize;
    vec2 p1 = vec2(-SQRT3_OVER_2, -0.5) * halfSize;
    vec2 p2 = vec2(SQRT3_OVER_2, -0.5) * halfSize;
    if (useJitteredShape()) {
        uint seed = particleIn[0].particleSeed;
        p0 += randomJitter(seed, 0u, halfSize);
        p1 += randomJitter(seed, 1u, halfSize);
        p2 += randomJitter(seed, 2u, halfSize);
    }
    p0 = rotate2D(p0, angle);
    p1 = rotate2D(p1, angle);
    p2 = rotate2D(p2, angle);

    emitViewVertex(centerView + axisX * p0.x + axisY * p0.y);
    emitViewVertex(centerView + axisX * p1.x + axisY * p1.y);
    emitViewVertex(centerView + axisX * p2.x + axisY * p2.y);
    EndPrimitive();
}

void emitQuad(vec3 centerView, float halfSize, float angle, vec3 axisX, vec3 axisY) {
    vec2 bl = vec2(-1.0, -1.0) * halfSize;
    vec2 br = vec2( 1.0, -1.0) * halfSize;
    vec2 tl = vec2(-1.0,  1.0) * halfSize;
    vec2 tr = vec2( 1.0,  1.0) * halfSize;
    if (useJitteredShape()) {
        uint seed = particleIn[0].particleSeed;
        bl += randomJitter(seed, 0u, halfSize);
        br += randomJitter(seed, 1u, halfSize);
        tl += randomJitter(seed, 2u, halfSize);
        tr += randomJitter(seed, 3u, halfSize);
    }
    bl = rotate2D(bl, angle);
    br = rotate2D(br, angle);
    tl = rotate2D(tl, angle);
    tr = rotate2D(tr, angle);

    emitViewVertex(centerView + axisX * bl.x + axisY * bl.y);
    emitViewVertex(centerView + axisX * br.x + axisY * br.y);
    emitViewVertex(centerView + axisX * tl.x + axisY * tl.y);
    emitViewVertex(centerView + axisX * tr.x + axisY * tr.y);
    EndPrimitive();
}

void main() {
    float size = particleIn[0].size;
    if (size <= 0.0) {
        return;
    }
    float halfSize = size * 0.5;
    vec3 centerView = particleIn[0].centerView;
    float angle = particleIn[0].angle;
    vec3 axisX;
    vec3 axisY;
    resolveAxes(axisX, axisY);

    if (u_bucketType == BUCKET_TRIANGLE) {
        emitTriangle(centerView, halfSize, angle, axisX, axisY);
        return;
    }
    if (u_bucketType == BUCKET_QUAD) {
        emitQuad(centerView, halfSize, angle, axisX, axisY);
    }
}
