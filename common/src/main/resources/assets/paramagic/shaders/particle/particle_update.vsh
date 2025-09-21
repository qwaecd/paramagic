#version 330 core
//#extension GL_EXT_transform_feedback : enable
#extension GL_EXT_transform_feedback : require

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec3 in_velocity;
layout (location = 2) in float in_age;
layout (location = 3) in float in_lifetime;
layout (location = 4) in vec4 in_color;
layout (location = 5) in float in_intensity;
layout (location = 6) in float in_size;
layout (location = 7) in float in_angle;
layout (location = 8) in float in_angularVelocity;
layout (location = 9) in int in_index;

out vec3 out_position;
out vec3 out_velocity;
out float out_age;
out float out_lifetime;
out vec4 out_color;
out float out_intensity;
out float out_size;
out float out_angle;
out float out_angularVelocity;
out int out_index;

uniform float u_deltaTime;
// not center force
uniform vec3 u_gravity;
uniform float u_drag;
// center force = A * pow(r, B) + C
uniform vec3 u_attractorPosition;
uniform float u_constantA;
uniform float u_exponentB;
uniform float u_constantC;
// update emitter
uniform vec3 u_emitterPosition;
uniform vec3 u_baseVelocity;
uniform float u_velocitySpread;
uniform float u_minLifetime;
uniform float u_maxLifetime;
// respawn
uniform int u_emitCount;
uniform int u_activationBaseIndex;
uniform int u_particleCountInSlice;

const float PI = 3.14159265359;

vec3 applyForces() {
    vec3 notCenterForce = u_gravity - in_velocity * u_drag;

    vec3 centerForce = vec3(0.0);
    vec3 toAttractor = u_attractorPosition - in_position;
    float distance = length(toAttractor);

    if (u_constantA != 0.0 && distance > 0.001) {
        vec3 direction = toAttractor / distance;
        float magnitude = u_constantA * pow(distance, u_exponentB) + u_constantC;
        centerForce = direction * magnitude;
    }
    return notCenterForce + centerForce;
}

void update() {
    float new_age = in_age + u_deltaTime;
    vec3 newVelocity = in_velocity + applyForces() * u_deltaTime;
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
    out_index = in_index;
}

void death() {
    out_position = in_position;
    out_velocity = in_velocity;
    out_age = in_age;
    out_lifetime = in_lifetime;
    out_color = in_color;
    out_intensity = in_intensity;
    out_size = in_size;
    out_angle = in_angle;
    out_angularVelocity = in_angularVelocity;
    out_index = in_index;
}

void respawn() {
    float random = fract(sin(float(in_index) * 43758.5453 + u_deltaTime) * 1.0);

    out_age = 0.0;
    out_lifetime = mix(u_minLifetime, u_maxLifetime, random);

    vec3 randomDir;
    if (u_velocitySpread > 0.0) {
        float phi = random * 2.0 * PI; // 随机方位角
        float cosTheta = 1.0 - random * (1.0 - cos(radians(u_velocitySpread / 2.0)));
        float sinTheta = sqrt(1.0 - cosTheta * cosTheta);

        vec3 deviation = vec3(cos(phi) * sinTheta, sin(phi) * sinTheta, cosTheta);

        // 创建一个垂直于基础速度的坐标系来旋转偏移量
        vec3 up = abs(u_baseVelocity.y) > 0.999 ? vec3(1,0,0) : vec3(0,1,0);
        vec3 tangent = normalize(cross(up, u_baseVelocity));
        vec3 bitangent = cross(u_baseVelocity, tangent);
        mat3 rotation = mat3(tangent, bitangent, u_baseVelocity);

        randomDir = rotation * deviation;
    } else {
        randomDir = u_baseVelocity;
    }
    out_position = u_emitterPosition;
    out_velocity = randomDir * length(u_baseVelocity);
    out_color = in_color;
    out_intensity = in_intensity;
    out_size = in_size;
    out_angle = random * 360.0;
    out_angularVelocity = in_angularVelocity;
    out_index = in_index;
}

void main() {
    int circularIndex = (in_type - u_activationBaseIndex + u_particleCountInSlice) % u_particleCountInSlice;
    bool shouldActivate = (u_emitCount > 0) && (circularIndex < u_emitCount);
    if (in_age >= in_lifetime && shouldActivate) {
        respawn();
    } else if (in_age < in_lifetime) {
        update();
    } else {
        death();
    }
}