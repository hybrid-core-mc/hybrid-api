#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

// Kept completely identical - untouched!
layout(std140) uniform FontUniforms {
    vec4 u_EffectParams; // x = u_Time, yzw = padding/future use
};

uniform sampler2D Sampler0;

in vec2 texCoord;
in vec4 vertColor;
out vec4 fragColor;

void main() {
     float dist = texture(Sampler0, texCoord).r;

    float edgeWidth = fwidth(dist) * 0.60;

    float innerTextAlpha = smoothstep(0.5 - edgeWidth, 0.5 + edgeWidth, dist);
    float glowAlpha = smoothstep(0.38 - edgeWidth, 0.5 + edgeWidth, dist);
    float smoothFadeGlow = pow(glowAlpha, 2.0) * 0.65;
    float finalVisibility = max(innerTextAlpha, smoothFadeGlow);

    vec4 combinedColor = vec4(vertColor.rgb, vertColor.a * finalVisibility);

    if (combinedColor.a < 0.01) discard;

    fragColor = combinedColor * ColorModulator;
}