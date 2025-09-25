#version 430
// Removed dummy vertex attribute; using gl_VertexID with SSBO data
layout(std430, binding = 0) buffer Positions {
    vec4 positions[];
};

uniform mat4 u_projection;
uniform mat4 u_view;          // expected to contain only rotation (following engine convention)
uniform vec3 u_cameraPos;     // world-space camera position

void main() {
    uint idx = uint(gl_VertexID);
    vec3 worldPos = positions[idx].xyz;
    // Engine pattern: view matrix likely lacks translation, so subtract cameraPos manually
    vec3 relative = worldPos - u_cameraPos;
    gl_Position = u_projection * u_view * vec4(relative, 1.0);
    gl_PointSize = 1.0; // sized via GL_PROGRAM_POINT_SIZE
}