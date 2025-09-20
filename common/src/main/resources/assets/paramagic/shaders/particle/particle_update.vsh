#version 330 core

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec3 in_velocity;
layout (location = 2) in float in_age;
layout (location = 3) in float in_lifetime;
layout (location = 4) in vec4 in_color;
layout (location = 5) in float in_intensity;
layout (location = 6) in float in_size;
layout (location = 7) in float in_angle;
layout (location = 8) in float in_angularVelocity;
layout (location = 9) in int in_type;

uniform float u_deltaTime;

out vec3 out_position;
out vec3 out_velocity;
out float out_age;
out float out_lifetime;
out vec4 out_color;
out float out_intensity;
out float out_size;
out float out_angle;
out float out_angularVelocity;
out int out_type;

void update() {
    float new_age = in_age + u_deltaTime;
    vec3 newVelocity = in_velocity;
    vec3 newPosition = in_position + newVelocity * u_deltaTime;
    float newAngle = in_angle + in_angularVelocity * u_deltaTime;

    out_position = newPosition;
    out_velocity = newVelocity;
    out_age = new_age;
    out_lifetime = in_lifetime;
    out_color = in_color;
    out_intensity = in_intensity;
    out_size = in_size;
    out_angle = newAngle;
    out_angularVelocity = in_angularVelocity;
    out_type = in_type;
}

void main() {
    if (in_age >= in_lifetime) {
        out_position = in_position;
        out_velocity = in_velocity;
        out_age = in_age;
        out_lifetime = in_lifetime;
        out_color = in_color;
        out_intensity = in_intensity;
        out_size = in_size;
        out_angle = in_angle;
        out_angularVelocity = in_angularVelocity;
        out_type = in_type;
        return;
    }
    update();
}