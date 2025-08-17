#version 330
layout(location = 0) out vec4 FragColor;
layout(location = 1) out vec4 BloomColor;
uniform vec4 u_sunColor;
uniform float u_intensity;
uniform float u_time;
void main() {
    // 简单的颜色计算
    vec3 baseColor = u_sunColor.rgb * u_intensity;
    // (可选) 使用噪声增加表面细节
    // vec3 noiseCoords = ...;
    // float noise = simplex_noise(noiseCoords + u_time);
    // baseColor *= (1.0 + noise * 0.2);
    // 输出到主颜色通道
    FragColor = vec4(baseColor, 1.0);
    float brightness = dot(baseColor, vec3(0.2126, 0.7152, 0.0722));
    float bloomThreshold = 1.0;
    if (brightness > bloomThreshold) {
        // 将超过阈值的部分写入Bloom通道
        // 减去阈值可以得到更平滑的辉光过渡
        vec3 bloom_color = baseColor * (brightness - bloomThreshold);
        BloomColor = vec4(bloom_color, 1.0);
    } else {
        BloomColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
}