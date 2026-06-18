#version 150

layout(std140) uniform QuadUniforms {
    vec4 uCoords; 
    vec4 uCorners;
    vec4 uColor; 
    float uBorderSize;
    vec4 uBorderColor;
    float splitBorder;
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


    float leftEdge = uCoords.x - halfSize.x;
    float quadWidth = uCoords.z;


    float normalizedX = clamp((f_Position.x - leftEdge) / quadWidth, 0.0, 1.0);

    
    float outer = roundedBoxSDF(centerOffset, halfSize, uCorners);


    float innerDist = uBorderSize;
    if (splitBorder == 1.0) {
        innerDist = uBorderSize * 2.0;
    }

    float inner = roundedBoxSDF(
    centerOffset,
    halfSize - vec2(innerDist),
    max(uCorners - vec4(innerDist), 0.0)
    );

    float outerAlpha = 1.0 - smoothstep(0.0, uEdgeSoftness, outer);
    float innerAlpha = 1.0 - smoothstep(0.0, uEdgeSoftness, inner);
    float baseBorderMask = clamp(outerAlpha - innerAlpha, 0.0, 1.0);

    vec3 finalBorderColor = uBorderColor.rgb;
    float finalBorderAlpha = uBorderColor.a;
    float borderMask = baseBorderMask;

    vec3 finalBackgroundColor = uColor.rgb;
    float finalBackgroundAlpha = uColor.a;

    if (splitBorder == 1.0) {
        vec3 accentBlurple = vec3(97.0 / 255.0, 100.0 / 255.0, 238.0 / 255.0);
        vec3 darkBackground = vec3(75.0 / 255.0, 91.0 / 255.0, 148.0 / 255.0);


        finalBackgroundColor = mix(accentBlurple, darkBackground, normalizedX);
        finalBackgroundAlpha = mix(0.2, 0., normalizedX);

        finalBorderColor = accentBlurple;


        float borderFadeAlpha = smoothstep(0.450, 0.0, normalizedX);
        finalBorderAlpha = uBorderColor.a * borderFadeAlpha;
    }


    vec4 fill = vec4(finalBackgroundColor, finalBackgroundAlpha * innerAlpha);
    vec4 border = vec4(finalBorderColor, finalBorderAlpha * borderMask);

    
    fragColor.rgb = border.rgb * border.a + fill.rgb * (1.0 - border.a);
    fragColor.a   = border.a + fill.a * (1.0 - border.a);
}