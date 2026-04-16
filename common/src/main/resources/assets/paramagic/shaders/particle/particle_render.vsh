#version 430 core
#define BINDING_PARTICLE_DATA 1
#define BINDING_EFFECT_META_DATA 3

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

uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;
uniform vec3 u_cameraPosition;

out vec4 v_color;
out float v_intensity;

void main() {
    Particle particle = particles[gl_VertexID];
    if (particle.attributes.x >= particle.attributes.y) {
        gl_Position = vec4(-2000.0, -2000.0, -2000.0, 1.0); // discard this vertex by moving it out of clip space
        v_color = vec4(0.0);
        v_intensity = 0.0;
        return;
    }

    v_color = particle.color;

    vec3 vertex_position_relative = particle.position.xyz - u_cameraPosition;

    gl_Position = u_projectionMatrix * u_viewMatrix * vec4(vertex_position_relative, 1.0);
    v_intensity = particle.renderAttribs.w;
    gl_PointSize = particle.renderAttribs.x;
}