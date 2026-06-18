#version 150

layout(std140) uniform QuadUniforms {
    vec4 uCoords;
    vec4 uCorners;
    vec4 uColor;
    float uBorderSize;
    vec4 uBorderColor;
    float uEdgeSoftness;
};

in vec2 f_Position;
out vec4 fragColor;


float roundedBoxSDF(vec2 p, vec2 halfSize, vec4 cornerRadius) {
    cornerRadius.xy = (p.x > 0.0) ? cornerRadius.xy : cornerRadius.zw;
    cornerRadius.x  = (p.y > 0.0) ? cornerRadius.x  : cornerRadius.y;
    vec2 q = abs(p) - halfSize + cornerRadius.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - cornerRadius.x;
}

void main() {


    vec2 centerOffset = f_Position.xy - uCoords.xy;
    vec2 halfSize = uCoords.zw * 0.5;

    float outer = roundedBoxSDF(centerOffset, halfSize, uCorners);

    float inner = roundedBoxSDF(
    centerOffset,
    halfSize - vec2(uBorderSize),
    max(uCorners - vec4(uBorderSize), 0.0)
    );

    float outerAlpha = 1.0 - smoothstep(0.0, uEdgeSoftness, outer);
    float innerAlpha = 1.0 - smoothstep(0.0, uEdgeSoftness, inner);

    float borderMask = clamp(outerAlpha - innerAlpha, 0.0, 1.0);

    vec3 borderColor = uBorderColor.rgb;

    vec4 fill = vec4(uColor.rgb, uColor.a * innerAlpha);
    vec4 border = vec4(borderColor, uBorderColor.a * borderMask);

    fragColor.rgb = border.rgb * border.a + fill.rgb * (1.0 - border.a);
    fragColor.a   = border.a + fill.a * (1.0 - border.a);
}