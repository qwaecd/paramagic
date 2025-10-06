#version 430 core

#define EPS 1e-4

layout(local_size_x = 8, local_size_y = 8, local_size_z = 1) in;

// Thanks to https://michaelmoroz.github.io/TracingGeodesics/ for the basis of this shader

layout(binding = 0) uniform sampler2D u_sceneColorTex;
layout(binding = 1, rgba32f) writeonly uniform image2D u_outputImage;

uniform mat4 u_invViewProj;
uniform vec3 u_cameraPos;
uniform float worldToGeomScale;  // map world units -> geometric units
uniform vec3 u_bhPos;            // black hole position in world space

mat4 diag(vec4 a) {
    return mat4(a.x,0,0,0,
                0,a.y,0,0,
                0,0,a.z,0,
                0,0,0,a.w);
}

mat4 Metric(vec4 x) {
    // Kerr-Newman metric in Kerr-Schild coordinates
    // but now using Schwarzschild
    const float a = 0.0;
    const float m = 1.0;
    const float Q = 0.0;
    vec3 p = x.yzw;
    float rho = dot(p,p) - a*a;
    float r2 = 0.5*(rho + sqrt(rho*rho + 4.0*a*a*p.z*p.z));
    float r = sqrt(r2);
    vec4 k = vec4(1, (r*p.x + a*p.y)/(r2 + a*a), (r*p.y - a*p.x)/(r2 + a*a), p.z/r);
    float f = r2*(2.0*m*r - Q*Q)/(r2*r2 + a*a*p.z*p.z);
    return f*mat4(k.x*k, k.y*k, k.z*k, k.w*k)+diag(vec4(-1,1,1,1));
}

float Hamiltonian(vec4 x, vec4 p) {
    mat4 g_inv = inverse(Metric(x));
    return 0.5*dot(g_inv*p,p);
}

/*
float Lagrangian(vec4 x, vec4 dxdt) {
    return 0.5*dot(Metric(x)*dxdt,dxdt);
}
*/

vec4 HamiltonianGradient(vec4 x, vec4 p) {
    const float eps = 0.001;
    return (vec4(Hamiltonian(x + vec4(eps,0,0,0), p),
    Hamiltonian(x + vec4(0,eps,0,0), p),
    Hamiltonian(x + vec4(0,0,eps,0), p),
    Hamiltonian(x + vec4(0,0,0,eps), p)) - Hamiltonian(x,p))/eps;
}

void IntegrationStep(inout vec4 x, inout vec4 p) {
    const float TimeStep = 0.15;
    p = p - TimeStep * HamiltonianGradient(x, p);
    x = x + TimeStep * inverse(Metric(x)) * p;
}

vec4 GetNullMomentum(vec4 x, vec3 dir) {
    return Metric(x) * vec4(1.0, normalize(dir));
}

vec3 GetDirection(vec4 x, vec4 p) {
    vec4 dxdt = inverse(Metric(x)) * p;
    return normalize(dxdt.yzw);
}

void TraceGeodesic(inout vec3 pos, inout vec3 dir, inout float time) {
    vec4 x = vec4(time, pos);
    vec4 p = GetNullMomentum(x, dir);

    const int steps = 256;
    for(int i = 0; i < steps; i++) {
        IntegrationStep(x, p);
        //you can add a stop condition here when x is below the event horizon for example
    }

    pos = x.yzw;
    time = x.x;
    dir = GetDirection(x, p);
}

void main() {
    ivec2 pixel = ivec2(gl_GlobalInvocationID.xy);
    ivec2 res = imageSize(u_outputImage);

    if(pixel.x >= res.x || pixel.y >= res.y) {
        return;
    }

    vec2 fragPx = vec2(pixel) + vec2(0.5);
    vec2 ndc = 2.0 * (fragPx / vec2(res)) - 1.0;

    // unproject near/far by invViewProj
    vec4 clipNear = vec4(ndc.x, ndc.y, -1.0, 1.0);
    vec4 clipFar  = vec4(ndc.x, ndc.y,  1.0, 1.0);

    vec4 worldNear4 = u_invViewProj * clipNear; worldNear4 /= worldNear4.w + EPS;
    vec4 worldFar4  = u_invViewProj * clipFar;  worldFar4  /= worldFar4.w + EPS;

    vec3 worldNear = worldNear4.xyz;
    vec3 worldFar  = worldFar4.xyz;

    vec3 RayPos_world = u_cameraPos;
    vec3 RayDir_world = normalize(worldFar - u_cameraPos);

    RayPos_world += RayDir_world * 1e-4;    // nudge off camera

    // convert to geometry coords centered at BH
    vec3 RayPos_geom = (RayPos_world - u_bhPos) * worldToGeomScale;
    vec3 RayDir_geom = normalize(RayDir_world); // directions scale-invariant


//    vec3 RayPos = vec3(0.0, 0.0, 32.0);
//    vec3 RayDir = normalize(vec3(uv.x, uv.y, -1.0));
    float Time = 0.0;

    TraceGeodesic(RayPos_geom, RayDir_geom, Time);

    vec2 sampleUV = RayPos_geom.xy * 0.5 + 0.5;
    vec3 color = texture(u_sceneColorTex, sampleUV).rgb;

    imageStore(u_outputImage, pixel, vec4(color, 1.0));
}