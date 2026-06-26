#version 330
layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform FontUniforms {
    vec4 u_EffectParams;
};
uniform sampler2D Sampler0;
in vec2 texCoord;
in vec4 vertColor;
in float localY;
out vec4 fragColor;

void main() {
    float dist = texture(Sampler0, texCoord).r;
    float edgeWidth = fwidth(dist) * 0.7;
    float time = u_EffectParams.x;
    float innerTextAlpha = smoothstep(0.5 - edgeWidth, 0.5 + edgeWidth, dist);
    float glowAlpha = smoothstep(0.38 - edgeWidth, 0.5 + edgeWidth, dist);
    float smoothFadeGlow = pow(glowAlpha, 2.0) * 0.65;
    float finalVisibility = max(innerTextAlpha, smoothFadeGlow);
    vec3 targetRGB;
    if (abs(u_EffectParams.y - 2.0) < 0.01) {
        float waveSpeed = 3.0;
        float waveFrequency = 0.1;
        float wave = sin(gl_FragCoord.y * waveFrequency - time * waveSpeed);
        float gradientFactor = wave * 0.5 + 0.5;
        vec3 whiteColor = vec3(1.0, 1.0, 1.0);
        vec3 pinkColor  = vec3(1.0, 0.4, 0.7);
        vec3 coreTextColor = mix(whiteColor, pinkColor, gradientFactor);
        float outlineEdge = smoothstep(0.43 - edgeWidth, 0.48 + edgeWidth, dist);
        vec3 outlineColor = vec3(0.8, 1.0, 0.4);
        targetRGB = mix(outlineColor, coreTextColor, innerTextAlpha);
        finalVisibility = max(outlineEdge, smoothFadeGlow);
    }
    else if (abs(u_EffectParams.y - 3.0) < 0.01) {
        float speed1 = 2.0;
        float speed2 = 3.5;
        float wave1 = sin(gl_FragCoord.x * 0.04 + gl_FragCoord.y * 0.02 + time * speed1);
        float wave2 = cos(gl_FragCoord.y * 0.05 - gl_FragCoord.x * 0.01 + time * speed2);
        float oceanPattern = (wave1 * 0.5 + wave2 * 0.5) * 0.5 + 0.5;
        float specularCrests = pow(oceanPattern, 4.0);
        vec3 deepOceanBlue  = vec3(0.0, 0.1, 0.4);
        vec3 vibrantAqua    = vec3(0.0, 0.5, 0.85);
        vec3 seafoamWhite   = vec3(0.7, 0.95, 1.0);
        vec3 waterBase = mix(deepOceanBlue, vibrantAqua, oceanPattern);
        waterBase *= vertColor.rgb;
        targetRGB = mix(waterBase, seafoamWhite, specularCrests);
    } else if (abs(u_EffectParams.y - 4.0) < 0.01) {
        vec2 normal = vec2(dFdx(dist), dFdy(dist));
        float reflection = sin((normal.x + normal.y) * 15.0 + time * 4.0);
        float highlight = pow(max(0.0, reflection), 6.0);
        vec3 darkGold  = vec3(0.3, 0.2, 0.0);
        vec3 brightGold = vec3(1.0, 0.8, 0.3);
        vec3 whiteGlint = vec3(1.0, 1.0, 0.9);
        vec3 goldBase = mix(darkGold, brightGold, reflection * 0.5 + 0.5);
        targetRGB = mix(goldBase, whiteGlint, highlight);
    } else if (abs(u_EffectParams.y - 5.0) < 0.01) {
        float heatPattern = sin(gl_FragCoord.x * 0.02 + time * 1.5) * cos(gl_FragCoord.y * 0.03 - time * 2.0);
        float heatFactor = clamp(heatPattern * 0.5 + 0.5, 0.0, 1.0);
        vec3 cooledRock = vec3(0.08, 0.05, 0.05);
        vec3 lavaOrange = vec3(1.0, 0.3, 0.0);
        vec3 superHotYellow = vec3(1.0, 0.9, 0.2);
        vec3 magma = mix(lavaOrange, superHotYellow, pow(heatFactor, 4.0));
        targetRGB = mix(cooledRock, magma, smoothstep(0.3, 0.7, heatFactor));
    } else if (abs(u_EffectParams.y - 6.0) < 0.01) {
        vec2 uv = gl_FragCoord.xy * 0.005;
        float angle = time * 0.2;
        vec2 rotatedUV;
        rotatedUV.x = uv.x * cos(angle) - uv.y * sin(angle);
        rotatedUV.y = uv.x * sin(angle) + uv.y * cos(angle);
        float swirl = sin(rotatedUV.x * 4.0 + rotatedUV.y * 4.0);
        float starSpecks = pow(fract(sin(dot(gl_FragCoord.xy, vec2(12.9898, 78.233))) * 43758.5453), 40.0);
        vec3 deepSpace = vec3(0.05, 0.0, 0.15);
        vec3 nebulaPurple = vec3(0.5, 0.0, 0.8);
        vec3 cosmicPink = vec3(1.0, 0.2, 0.6);
        vec3 spaceBackground = mix(deepSpace, nebulaPurple, swirl * 0.5 + 0.5);
        targetRGB = mix(spaceBackground, cosmicPink, starSpecks * 2.0);
        finalVisibility = max(finalVisibility, starSpecks * innerTextAlpha);
    } else if (abs(u_EffectParams.y - 7.0) < 0.01) {
        float heatWave = sin(gl_FragCoord.x * 0.01 + time * 2.0) * cos(gl_FragCoord.y * 0.01 + time * 1.5);
        float heat = clamp(heatWave * 0.5 + 0.5, 0.0, 1.0);
        vec3 deepPurple  = vec3(0.15, 0.0, 0.35);
        vec3 neonPink    = vec3(1.0, 0.1, 0.65);
        vec3 whiteHot    = vec3(1.0, 1.0, 1.0);
        if (heat < 0.5) {
            targetRGB = mix(deepPurple, neonPink, heat / 0.5);
        } else {
            targetRGB = mix(neonPink, whiteHot, (heat - 0.5) / 0.5);
        }
        float peakHeat = smoothstep(0.7, 1.0, heat);
        finalVisibility = max(finalVisibility, peakHeat * innerTextAlpha);
    }  else if (abs(u_EffectParams.y - 8.0) < 0.01) {
        vec2 uv = gl_FragCoord.xy;
        vec3 deepNight = vec3(0.01, 0.01, 0.04) * vertColor.rgb;
        float speed1 = time * 0.5;
        float speed2 = time * -.2;
        float speed3 = time * 0.8;
        float waveA = sin(uv.x * 0.004 + speed1) * cos(uv.y * 0.002 + speed2);
        float waveB = cos(uv.x * 0.008 + speed2) * sin(uv.y * 0.003 - speed3);
        float waveC = sin(uv.x * 0.012 - speed3) * cos(uv.y * 0.001 + speed1);
        float waveD = cos(uv.x * 0.002 + speed1 * 0.5) * sin(uv.y * 0.004);
        vec4 ribbons = vec4(waveA, waveB, waveC, waveD) * 0.5 + 0.5;
        vec4 curtains = pow(ribbons, vec4(4.0, 5.0, 6.0, 3.5));
        vec3 auroraGreen  = vec3(0.0, 1.0, 0.3);
        vec3 auroraTeal   = vec3(0.0, 0.5, 0.8);
        vec3 auroraViolet = vec3(0.7, 0.1, 1.0);
        vec3 finalAurora = vec3(0.0);
        finalAurora += mix(auroraTeal, auroraGreen, curtains.y) * curtains.x;
        finalAurora += mix(auroraViolet, auroraGreen, curtains.z) * curtains.y;
        finalAurora += mix(auroraTeal, auroraViolet, curtains.x) * curtains.z;
        finalAurora += mix(auroraGreen, auroraViolet, curtains.w) * curtains.w;
        finalAurora *= 1.4;
        float totalIntensity = clamp(curtains.x + curtains.y + curtains.z + curtains.w, 0.0, 1.0);
        targetRGB = mix(deepNight, finalAurora, totalIntensity);
        finalVisibility = max(finalVisibility, totalIntensity * 0.6 * innerTextAlpha);
    }
    else {
        targetRGB = vertColor.rgb;
    }
    vec4 combinedColor = vec4(targetRGB, vertColor.a * finalVisibility);
    if (combinedColor.a < 0.01) discard;
    fragColor = combinedColor * ColorModulator;
}