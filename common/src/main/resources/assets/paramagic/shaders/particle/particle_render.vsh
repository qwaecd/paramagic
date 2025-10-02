#version 430 core
#define BINDING_PARTICLE_DATA 1

struct Particle {
    vec4 meta;        // x: effectId, y: unused, z: unused, w: unused
    vec4 position;    // x, y, z, mass(unused)
    vec4 velocity;    // vx, vy, vz, (unused)
    vec4 attributes;  // x: age, y: lifetime, z: current_anim_frame, w: anim_speed
    vec4 renderAttribs;  // x: size, y: angle, z: angular_velocity, w: bloom_intensity
    vec4 color; // rgba
};

layout(std430, binding = BINDING_PARTICLE_DATA) buffer ParticleData {
    Particle particles[];
};

uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;
uniform vec3 u_cameraPosition;

out vec4 v_color;
out float v_intensity;

void main() {
    Particle particle = particles[gl_VertexID];
     if (particle.attributes.x >= particle.attributes.y) {
         gl_Position = vec4(-20.0, -20.0, -20.0, 1.0); // discard this vertex by moving it out of clip space
         v_color = vec4(0.0);
         v_intensity = 0.0;
         return;
     }

    v_color = particle.color;
//    // --- Billboarding ---
//    vec3 cameraRight_worldspace = vec3(u_viewMatrix[0][0], u_viewMatrix[1][0], u_viewMatrix[2][0]);
//    vec3 cameraUp_worldspace = vec3(u_viewMatrix[0][1], u_viewMatrix[1][1], u_viewMatrix[2][1]);
//
//    // b. 根据Quad的本地坐标、摄像机朝向和粒子大小，计算出顶点的世界空间偏移量
//    vec3 vertex_offset =
//    cameraRight_worldspace * in_meshPosition.x * in_size +
//    cameraUp_worldspace    * in_meshPosition.y * in_size;
//
//    if (in_angle != 0.0) {
//        float angleRad = radians(in_angle);
//        float cosAngle = cos(angleRad);
//        float sinAngle = sin(angleRad);
//        // 使用一个旋转矩阵来旋转这个偏移量
//        vec3 rotated_offset;
//        rotated_offset.x = dot(vertex_offset, vec3(cosAngle, -sinAngle, 0.0));
//        rotated_offset.y = dot(vertex_offset, vec3(sinAngle,  cosAngle, 0.0));
//        rotated_offset.z = vertex_offset.z;
//        vertex_offset = rotated_offset;
//    }

    // 4. 计算最终的世界空间位置
    vec3 vertex_position_relative = particle.position.xyz - u_cameraPosition;

    gl_Position = u_projectionMatrix * u_viewMatrix * vec4(vertex_position_relative, 1.0);
    v_intensity = particle.renderAttribs.w;
    gl_PointSize = particle.renderAttribs.x;
}