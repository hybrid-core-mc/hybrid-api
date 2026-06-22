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

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    float dist = texture(Sampler0, texCoord).r;

    float edgeWidth = fwidth(dist) * 0.7;
    float time = u_EffectParams.x;

    vec2 screenCenter = vec2(500.0, 300.0);
    vec2 diff = gl_FragCoord.xy - screenCenter;
    float angle = atan(diff.y, diff.x);

    float hue = (angle / (3.1415926535 * 2.0)) + 0.5;
    hue += time * 0.15;
    hue = fract(hue);

    vec3 rainbowColor = hsv2rgb(vec3(hue, 1.0, 1.0));

    float innerTextAlpha = smoothstep(0.5 - edgeWidth, 0.5 + edgeWidth, dist);

    float glowAlpha = smoothstep(0.38 - edgeWidth, 0.5 + edgeWidth, dist);

    float smoothFadeGlow = pow(glowAlpha, 2.0) * 0.65;

    float finalVisibility = max(innerTextAlpha, smoothFadeGlow);

    vec3 finalRGB = rainbowColor * vertColor.rgb;
    vec4 combinedColor = vec4(finalRGB, vertColor.a * finalVisibility);

    if (combinedColor.a < 0.01) discard;

    fragColor = combinedColor * ColorModulator;
}