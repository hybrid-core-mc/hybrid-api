#version 150

layout(std140) uniform Uniforms {
    vec4 hueBounds;
};

in vec2 uv;
out vec4 fragColor;

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0/3.0, 1.0/3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float sdRoundBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {

    vec2 local = uv - hueBounds.xy;
    vec2 size  = hueBounds.zw;

    vec2 p = local / size - 0.5;
    p.x *= size.x / size.y;

    float r = length(p);

    float outerRadius = 0.35;
    float thickness   = 0.11;
    float innerRadius = outerRadius - thickness;

    float fw = fwidth(r);

    float innerAA = smoothstep(innerRadius - fw, innerRadius + fw, r);
    float outerAA = 1.0 - smoothstep(outerRadius - fw, outerRadius + fw, r);

    float ring = innerAA * outerAA;

    float angle = atan(p.y, p.x);

    float spin = 0.76;
    float hue = fract(
    0.25
    + angle / (2.0 * 3.14159265)
    + spin
    );

    vec3 color = hsv2rgb(vec3(hue, 1.0, 1.0));

    vec2 boxP = local - size * 0.5;
    float cornerRadius = min(size.x, size.y) * 0.12;
    float feather = 1.5;

    float boxDist = sdRoundBox(boxP, size * 0.5, cornerRadius);
    float cornerAlpha = smoothstep(feather, 0.0, boxDist);

    float fadeStart = outerRadius * 0.85;
    float fadeEnd   = outerRadius * 1.05;
    float radialFade = smoothstep(fadeEnd, fadeStart, r);

    float alpha = ring * cornerAlpha * radialFade;

    fragColor = vec4(color * alpha, alpha);
}