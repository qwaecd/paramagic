#version 330

in vec4 vertex_color;

out vec4 o_color;

void main(){
    o_color = vertex_color;
}