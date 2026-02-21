#version 150

layout(std140) uniform Uniforms {
    vec4 hueBounds;
    vec4 currentColor;
};

in vec2 uv;
out vec4 fragColor;

const float PI = 3.14159265;

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0/3.0, 1.0/3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 rgb2hsv(vec3 c){
    vec4 K = vec4(0.0, -1.0/3.0, 2.0/3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz),
    vec4(c.gb, K.xy),
    step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r),
    vec4(c.r, p.yzx),
    step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1e-10;
    return vec3(abs(q.z + (q.w - q.y)/(6.0*d+e)),
    d/(q.x+e),
    q.x);
}

void main()
{
    vec2 local = uv - hueBounds.xy;
    vec2 size  = hueBounds.zw;

    vec2 p = local / size;
    p -= 0.5;
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


    float extraOffset = 0.0001;

    float hue = fract(
    0.0 +
    angle / (2.0 * PI)+
    extraOffset
    );

    vec3 ringColor = hsv2rgb(vec3(hue, 1.0, 1.0));

    vec3 hsvCurrent = rgb2hsv(currentColor.rgb);
    float selectedHue = hsvCurrent.x + extraOffset;

    float selectedAngle =
    (selectedHue) * (2.0 * PI);

    float markerRadius = innerRadius + thickness * 0.5;

    vec2 markerDir = vec2(cos(selectedAngle), sin(selectedAngle));
    vec2 markerPos = markerDir * markerRadius;

    vec2 dir  = normalize(markerPos);
    vec2 perp = vec2(-dir.y, dir.x);

    vec2 rel = p - markerPos;

    float radial   = dot(rel, -dir);
    float sideways = dot(rel, perp);

    vec2 markerSize = vec2(thickness * 0.5, 0.0139);

    vec2 box = abs(vec2(radial, sideways)) - markerSize;
    float rectDist = length(max(box, 0.0)) + min(max(box.x, box.y), 0.0);

    float rectAA = fwidth(rectDist);
    float marker = 1.0 - smoothstep(0.0, rectAA, rectDist);

    float px = 1.0 / size.y;

    float coreWidth   = px * 0.5;
    float feather     = px * 5.0;

    float outward = max(rectDist, 0.0);

    float core = 1.0 - smoothstep(0.0, coreWidth, outward);

    float fadeDist = max(outward - coreWidth, 0.0);
    float t = clamp(fadeDist / feather, 0.0, 1.0);

    float fade = pow(1.0 - t, 3.0);

    float blackFade = max(core, fade);

    vec3 finalColor = ringColor;

    finalColor = mix(finalColor, vec3(0.0), blackFade);
    finalColor = mix(finalColor, vec3(1.0), marker);

    float finalAlpha = max(ring, max(marker, blackFade));

    fragColor = vec4(finalColor * finalAlpha, finalAlpha);
}