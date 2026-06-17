#version 150

layout(std140) uniform QuadUniforms {
    vec4 u_Rect;
    vec4 u_Radii; // The radius of each corner, (r1, r2, r3, r4)
    vec4 u_colorRect; // First color of gradient
    vec4 u_colorRect2; // Second color for gradient
    vec4 u_colorShadow; // Color of the shadow.
    vec2 u_gradientDirectionVector; // Direction of the gradient based on which variable to use, for example a gradient from left to right will use (1.0, 0.0) to use the positive x value of the length from the middle of the rectangle
    float u_edgeSoftness; // Softness of edges, free antialiasing, but breaks rounded corners over certain values.
    float u_shadowSoftness; // Softness of shadow. At 0 this will make shadow invisible.
};

#define u_rectCenter u_Rect.xy
#define u_rectSize u_Rect.zw

in vec2 f_Position;

out vec4 fragColor;


float roundedBoxSDF(vec2 CenterPosition, vec2 Size, vec4 Radius) {
    Radius.xy = (CenterPosition.x > 0.) ? Radius.xy : Radius.zw;
    Radius.x  = (CenterPosition.y > 0.) ? Radius.x  : Radius.y;

    vec2 q = abs(CenterPosition)-Size+Radius.x;
    return min(max(q.x,q.y), 0.) + length(max(q, 0.)) - Radius.x;
}

void main() {

    vec2 uv = (f_Position - u_rectCenter) / u_rectSize;
    float gradientStrength = clamp(dot(uv, u_gradientDirectionVector) + .5, 0., 1.);
    vec4 gradientColor = mix(u_colorRect, u_colorRect2, gradientStrength);

    vec4 u_colorBg = vec4(0.);
    vec2 halfSize = (u_rectSize / 2.);


    float distance = roundedBoxSDF(f_Position.xy - u_rectCenter, halfSize, u_Radii);

    float smoothedAlpha = 1. - smoothstep(0., u_edgeSoftness, distance);

    float shadowAlpha = 1. - smoothstep(-u_shadowSoftness, u_shadowSoftness, distance);

    vec4 resShadowColor = mix(
    u_colorBg,
    vec4(u_colorShadow.rgb, shadowAlpha),
    shadowAlpha
    );

    fragColor = mix(resShadowColor, gradientColor, smoothedAlpha);
}