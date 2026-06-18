#version 150

layout(std140) uniform QuadUniforms {
    vec4 u_Rect;
    vec4 u_Radii;
    vec4 u_colorRect;
    float u_edgeSoftness;
};

#define u_rectCenter u_Rect.xy
#define u_rectSize u_Rect.zw

in vec2 f_Position;
out vec4 fragColor;

float roundedBoxSDF(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x  : r.y;
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    vec2 halfSize = u_rectSize * 0.5;
    float d = roundedBoxSDF(f_Position.xy - u_rectCenter, halfSize, u_Radii);
    float alpha = 1.0 - smoothstep(0.0, u_edgeSoftness, d);
    fragColor = vec4(u_colorRect.rgb, u_colorRect.a * alpha);
}