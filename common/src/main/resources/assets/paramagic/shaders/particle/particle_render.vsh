#version 330 core

layout (location = 0) in vec3 in_position;

layout (location = 1) in vec3 in_velocity;  // no need in vertex shader

layout (location = 2) in float in_age;
layout (location = 3) in float in_lifetime;

layout (location = 4) in vec4 in_color; // pass it to fragment shader

layout (location = 5) in float in_intensity; // pass it to fragment shader

layout (location = 6) in float in_size;
layout (location = 7) in float in_angle;

layout (location = 8) in float in_angularVelocity;  // no need in vertex shader
layout (location = 9) in int in_index;  // no need in vertex shader

layout (location = 10) in vec3 in_meshPosition;

uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;

out vec4 v_color;
out vec2 v_texCoord;
out float v_intensity;

void main() {
     if (in_age >= in_lifetime) {
         gl_Position = vec4(-20.0, -20.0, -20.0, 1.0); // discard this vertex by moving it out of clip space
         v_color = vec4(0.0);
         v_texCoord = vec2(0.0);
         return;
     }

    v_color = in_color;
    v_texCoord = in_meshPosition.xy + 0.5;


    // --- Billboarding ---
    vec3 cameraRight_worldspace = vec3(u_viewMatrix[0][0], u_viewMatrix[1][0], u_viewMatrix[2][0]);
    vec3 cameraUp_worldspace = vec3(u_viewMatrix[0][1], u_viewMatrix[1][1], u_viewMatrix[2][1]);

    // b. 根据Quad的本地坐标、摄像机朝向和粒子大小，计算出顶点的世界空间偏移量
    vec3 vertex_offset =
    cameraRight_worldspace * in_meshPosition.x * in_size +
    cameraUp_worldspace    * in_meshPosition.y * in_size;

    if (in_angle != 0.0) {
        float angleRad = radians(in_angle);
        float cosAngle = cos(angleRad);
        float sinAngle = sin(angleRad);
        // 使用一个旋转矩阵来旋转这个偏移量
        vec3 rotated_offset;
        rotated_offset.x = dot(vertex_offset, vec3(cosAngle, -sinAngle, 0.0));
        rotated_offset.y = dot(vertex_offset, vec3(sinAngle,  cosAngle, 0.0));
        rotated_offset.z = vertex_offset.z;
        vertex_offset = rotated_offset;
    }

    // 4. 计算最终的世界空间位置
    vec3 vertex_position_worldspace = in_position + vertex_offset;

    gl_Position = u_projectionMatrix * u_viewMatrix * vec4(vertex_position_worldspace, 1.0);
    v_intensity = in_intensity;
}