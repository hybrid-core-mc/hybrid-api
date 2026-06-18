#version 150

layout(std140) uniform CircleUniforms {
    vec4 uCoords;
    vec4 uColor;
    float uEdgeSoftness;
    float uGlowSize;
    float uGlowing;
};

in vec2 f_Position;
out vec4 fragColor;

void main() {
    vec2 center = uCoords.xy;
    float radius = uCoords.z;
    float dist = length(f_Position - center);

    float distanceToEdge = dist - radius;

    float coreAlpha = 1.0 - smoothstep(-uEdgeSoftness, uEdgeSoftness, distanceToEdge);

    float glowFactor = distanceToEdge / uGlowSize;
    glowFactor = clamp(glowFactor, 0.0, 1.0);
    float glowAlpha = pow(1.0 - glowFactor, 5.0);

    if (distanceToEdge < 0.0) {
        glowAlpha = smoothstep(-uEdgeSoftness * 2.0, 0.0, distanceToEdge);
    }

    float glowIntensity = 0.25 * (uGlowing > 0.5 ? 1.0 : 0.0);

    float finalAlpha = max(coreAlpha, glowAlpha * glowIntensity);

    fragColor = vec4(uColor.rgb, uColor.a * finalAlpha);
}