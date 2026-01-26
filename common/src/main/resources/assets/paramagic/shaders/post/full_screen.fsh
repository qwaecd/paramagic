#version 330 core

out vec4 FragColor;
in vec2 v_texCoords;

// mainFbo 的 attachment 0
uniform sampler2D u_sceneTexture;

uniform sampler2D u_bloomTexture;

// bloom 强度
uniform float u_bloomStrength = 1.0;

void main() {
    vec4 scene = texture(u_sceneTexture, v_texCoords);
    vec3 sceneColor = scene.rgb;
    float alpha = scene.a;
    vec3 bloomColor = texture(u_bloomTexture, v_texCoords).rgb;

    vec3 finalColor = sceneColor + bloomColor * u_bloomStrength;

    FragColor = vec4(finalColor, alpha);
}
