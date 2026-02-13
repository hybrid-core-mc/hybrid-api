#version 150

layout(std140) uniform Uniforms {
    vec4 u_Rect;
};

in vec2 uv;
out vec4 fragColor;

float circleee(vec2 p, float r) {
    return length(p) - r;
}

float steppyyy(float dist) {
    float fw = fwidth(dist);
    return smoothstep(fw, -fw, dist);
}

void main() {

    vec2 local = uv - u_Rect.xy;

    vec2 center = u_Rect.zw * 0.5;
    float radius = min(u_Rect.z, u_Rect.w) * 0.5;

    float d = circleee(local - center, radius);

    float alpha = steppyyy(d);

    fragColor = vec4(1.0, 1.0, 1.0, alpha);
}