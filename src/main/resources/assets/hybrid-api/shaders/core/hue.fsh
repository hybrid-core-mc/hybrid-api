#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

out vec4 fragColor;

void main() {
    vec4 green = vec4(0.0, 1.0, 0.0, 1.0);

    if (ColorModulator.a == 0.0) {
        discard;
    }

    fragColor = green * ColorModulator;
}