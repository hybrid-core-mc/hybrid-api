#version 150 core

layout(std140) uniform TriangleUniforms {
    vec4 uCoords;  
    vec4 uColor;   
    vec4 uParams;  
};

in vec2 f_Position;
out vec4 fragColor;

void main() {
    vec2 size = uCoords.zw;
    float edgeSoftness = uParams.x;

    vec2 p = f_Position - (size * 0.5);
    float aspect = size.x / size.y;

    p.x *= aspect;
    
    float angle = uParams.z;
    float c = cos(angle);
    float s = sin(angle);
    mat2 rot = mat2(c, -s, s, c);
    p = rot * p;

    p.x /= aspect;
    p /= min(size.x, size.y);
    p *= 0.9;
    
    float r = 0.22;
    const float k = sqrt(3.0);

    vec2 vertexColor = vec2(0.0, r * 2.0);      
    vec2 vertexWhite = vec2(-k * r, -r);        
    vec2 vertexBlack = vec2(k * r, -r);         

    
    float denom = (vertexWhite.y - vertexBlack.y) * (vertexColor.x - vertexBlack.x) +
    (vertexBlack.x - vertexWhite.x) * (vertexColor.y - vertexBlack.y);

    float wColor = ((vertexWhite.y - vertexBlack.y) * (p.x - vertexBlack.x) +
    (vertexBlack.x - vertexWhite.x) * (p.y - vertexBlack.y)) / denom;

    float wWhite = ((vertexBlack.y - vertexColor.y) * (p.x - vertexBlack.x) +
    (vertexColor.x - vertexBlack.x) * (p.y - vertexBlack.y)) / denom;

    float wBlack = 1.0 - wColor - wWhite;

    
    wColor = clamp(wColor, 0.0, 1.0);
    wWhite = clamp(wWhite, 0.0, 1.0);
    wBlack = clamp(wBlack, 0.0, 1.0);

    
    vec3 mixedRGB = (wColor * uColor.rgb) + (wWhite * vec3(1.0)) + (wBlack * vec3(0.0));

    
    p.x = abs(p.x) - k * r;
    p.y = p.y + r;

    if (p.x + k * p.y > 0.0) {
        p = vec2(p.x - k * p.y, -k * p.x - p.y) * 0.5;
    }

    p.x -= clamp(p.x, -2.0 * k * r, 0.0);

    float d = -length(p) * sign(p.y);

    
    float pixelDistance = d * min(size.x, size.y);
    float alpha = smoothstep(edgeSoftness, -edgeSoftness, pixelDistance);
    
    
    vec4 triangleColor = vec4(mixedRGB, uColor.a * alpha);
    vec4 backgroundColor = vec4(0.0, 0.0, 0.0, 0.0);

    fragColor = mix(backgroundColor, triangleColor, alpha);
}