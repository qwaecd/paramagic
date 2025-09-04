#version 330
layout(location = 0) out vec4 FragColor;
layout(location = 1) out vec4 BloomColor;
uniform vec4 u_sunColor;
uniform float u_intensity;
uniform float u_time;
void main() {
    vec3 growHDRColor = u_sunColor.rgb * u_intensity;
    FragColor = u_sunColor * min(u_intensity, 1.0);
    float brightness = dot(growHDRColor, vec3(0.2126, 0.7152, 0.0722));
    float bloomThreshold = 1.0;
    if (brightness > bloomThreshold) {
        vec3 bloom_color = growHDRColor - bloomThreshold;
        BloomColor = vec4(bloom_color/* * (sin(u_time * 1.3) + 1.1) * 0.5*/, 1.0);
    } else {
        BloomColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
}