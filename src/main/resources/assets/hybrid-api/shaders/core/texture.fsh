#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

uniform sampler2D Sampler0;

in vec2 texCoord;
in vec4 vertColor;
out vec4 fragColor;

void main() {

    vec2 centerCoord = texCoord * 2.0 - 1.0;

    float radius = .59;

    vec2 q = abs(centerCoord) - vec2(1.0 - radius);
    float dist = length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;


    float edgeEdge = fwidth(dist);
    float alphaMask = 1.0 - smoothstep(-edgeEdge, edgeEdge, dist);

    if (alphaMask < 0.01) discard;

    vec4 tex = texture(Sampler0, texCoord);
    if (tex.a < 0.01) discard;

    fragColor = tex * vertColor * ColorModulator;

    fragColor.a *= alphaMask;
}