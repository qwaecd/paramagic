#version 330

in vec4 vertex_color;

layout(location = 0) out vec4 o_color;
layout(location = 1) out vec4 o_bloomColor;

void main(){
    float brightness = dot(vertex_color.rgb, vec3(0.2126, 0.7152, 0.0722));
    o_bloomColor = vec4(0.0, 0.0, 0.0, 1.0);
    if (brightness > 2.0) {
        o_bloomColor = vec4(vertex_color.rgb, 1.0);
    }
    o_color = vertex_color;
}