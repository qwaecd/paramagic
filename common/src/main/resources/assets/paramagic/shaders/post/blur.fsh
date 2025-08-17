#version 330 core

out vec4 fragColor;
in vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_texelSize;
uniform bool u_horizontal;

float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main() {
//    vec2 offset = u_horizontal ? vec2(u_texelSize.x, 0.0) : vec2(0.0, u_texelSize.y);
    // 基于当前绑定的纹理尺寸自动计算每个 texel 的步长，避免与渲染目标尺寸不一致
    vec2 texel = 1.0 / vec2(textureSize(u_texture, 0));
    vec2 offset = u_horizontal ? vec2(texel.x, 0.0) : vec2(0.0, texel.y);

    vec3 result = texture(u_texture, v_texCoords).rgb * weight[0];
    for (int i = 1; i < 5; i++) {
        result += texture(u_texture, v_texCoords + offset * float(i)).rgb * weight[i];
        result += texture(u_texture, v_texCoords - offset * float(i)).rgb * weight[i];
    }
    fragColor = vec4(result, 1.0);
}