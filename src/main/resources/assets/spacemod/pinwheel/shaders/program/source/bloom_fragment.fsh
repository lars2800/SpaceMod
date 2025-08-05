layout(std140) uniform CameraMatrices {
    mat4 ProjMat;
    mat4 IProjMat;
    mat4 ViewMat;
    mat4 IViewMat;
    mat3 IViewRotMat;
    vec3 CameraPosition;
    float NearPlane;
    float FarPlane;
} VeilCamera;

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform float u_blurOffset;

in vec2 texCoord;
out vec4 fragColor;

float clip01(float alpha){
    return max( min( alpha,0.0f ), 1.0f );
}

vec3 clip01vec(vec3 alpha){
    float clipX = max( min( alpha.x,1.0f ), 0.0f );
    float clipY = max( min( alpha.y,1.0f ), 0.0f );
    float clipZ = max( min( alpha.z,1.0f ), 0.0f );
    vec3 clip = vec3(clipX,clipY,clipZ);

    return clip;
}

vec3 bloomCutOut(vec3 color,float cutOut){
    return clip01vec( color - vec3(cutOut) );
}

vec3 blurify(vec2 textureCoordinates, float horizontalWidth, float verticalHeight) {


    // 3x3 Gaussian kernel weights
    float kernel[9];
    kernel[0] = 1.0 / 16.0;
    kernel[1] = 2.0 / 16.0;
    kernel[2] = 1.0 / 16.0;
    kernel[3] = 2.0 / 16.0;
    kernel[4] = 4.0 / 16.0;
    kernel[5] = 2.0 / 16.0;
    kernel[6] = 1.0 / 16.0;
    kernel[7] = 2.0 / 16.0;
    kernel[8] = 1.0 / 16.0;

    // Offsets for a 3x3 kernel
    vec2 offsets[9];
    offsets[0] = vec2(-horizontalWidth,  verticalHeight);
    offsets[1] = vec2(0.0,              verticalHeight);
    offsets[2] = vec2(horizontalWidth,  verticalHeight);
    offsets[3] = vec2(-horizontalWidth, 0.0);
    offsets[4] = vec2(0.0,              0.0);
    offsets[5] = vec2(horizontalWidth,  0.0);
    offsets[6] = vec2(-horizontalWidth, -verticalHeight);
    offsets[7] = vec2(0.0,             -verticalHeight);
    offsets[8] = vec2(horizontalWidth, -verticalHeight);

    vec3 color = vec3(0.0);
    for (int i = 0; i < 9; i++) {
        vec3 colorSample = texture(DiffuseSampler0, textureCoordinates + offsets[i]).rgb;
        color += bloomCutOut(colorSample,0.8f) * kernel[i];
    }

    return color;
}


void main() {
    vec4  baseColor = texture(DiffuseSampler0, texCoord);
    float baseDepth = texture(DiffuseDepthSampler, texCoord).r;

    fragColor = vec4( blurify( texCoord, u_blurOffset / 2560.0f, u_blurOffset / 1440.0f ) + baseColor.xyz, 1.0f );
}