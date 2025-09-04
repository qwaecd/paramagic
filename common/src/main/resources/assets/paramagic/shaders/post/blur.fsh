#version 330 core

out vec4 fragColor;
in vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_texelSize;
uniform bool u_horizontal;
uniform float u_blurRadius = 1.0; // 模糊半径（扩张距离）
float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main() {
    vec2 offset = u_horizontal ? vec2(u_texelSize.x, 0.0) : vec2(0.0, u_texelSize.y);

    vec3 result = texture(u_texture, v_texCoords).rgb * weight[0];
    for (int i = 1; i < 5; i++) {
        vec2 sampleOffset = offset * float(i) * u_blurRadius;
        result += texture(u_texture, v_texCoords + sampleOffset).rgb * weight[i];
        result += texture(u_texture, v_texCoords - sampleOffset).rgb * weight[i];
    }
    fragColor = vec4(result, 1.0);
}