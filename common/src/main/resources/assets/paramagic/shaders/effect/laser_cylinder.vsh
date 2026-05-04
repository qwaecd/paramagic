#version 330

layout(location = 0) in vec3 i_position;
layout(location = 2) in vec2 i_uv;
layout(location = 3) in vec3 i_normal;

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_model;

out vec2 v_uv;
out vec3 v_viewPos;
out vec3 v_viewNormal;

void main() {
    vec4 worldPos = u_model * vec4(i_position, 1.0);
    vec4 viewPos = u_view * worldPos;
    gl_Position = u_projection * viewPos;

    v_uv = i_uv;
    v_viewPos = viewPos.xyz;
    v_viewNormal = normalize((u_view * u_model * vec4(i_normal, 0.0)).xyz);
}
