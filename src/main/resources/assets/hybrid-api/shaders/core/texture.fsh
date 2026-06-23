#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

layout(std140) uniform TextureUniforms {
    float radius;
};

uniform sampler2D Sampler0;

in vec2 texCoord;
in vec4 vertColor;
out vec4 fragColor;

void main() {
    
    float u0 = TextureMat[0].z;
    float v0 = TextureMat[1].z;
    float u1 = TextureMat[2].z;
    float v1 = TextureMat[3].z;

    vec2 localTexCoord = (texCoord - vec2(u0, v0)) / max(vec2(u1, v1) - vec2(u0, v0), vec2(0.0001));

    vec2 centerCoord = localTexCoord * 2.0 - 1.0;


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