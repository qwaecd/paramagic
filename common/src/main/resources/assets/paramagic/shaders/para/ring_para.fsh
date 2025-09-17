#version 330

in vec4 vertex_color;

layout(location = 0) out vec4 o_color;
layout(location = 1) out vec4 o_bloomColor;

uniform vec3 u_emissiveColor;
uniform float u_emissiveIntensity;

void main(){
    vec3 growHDRColor = u_emissiveColor.rgb * u_emissiveIntensity;
    // 黑魔法不需要符合物理规律，fuck you dot.
//    float brightness = dot(growHDRColor, vec3(0.2126, 0.7152, 0.0722));
    o_bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
    const float threshold = 0.1;
    if (u_emissiveIntensity > threshold) {
        o_bloomColor = vec4(growHDRColor - threshold, 1.0);
    }
    o_color = vertex_color;
}