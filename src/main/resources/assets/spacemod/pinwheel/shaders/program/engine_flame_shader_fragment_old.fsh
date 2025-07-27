//----------\\
// Uniforms \\
//----------\\
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

uniform mat4x4 UViewRot;

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;

uniform float u_yaw;
uniform float u_pitch;

in vec2 texCoord;
out vec4 fragColor;

//
// Constants
//

const float BIG_NUMBER = 999999.99f;

//---------\\
// Structs \\
//---------\\
struct Sphere {
    vec3 centerPosition;
    float sphereRadius;
};

struct Ray {
    vec3 origin;
    vec3 direction;
};
struct Intersection{
    bool hit;
    vec3 position;
};

//------------\\
// Raytracing \\
//------------\\
vec3 viewPosFromDepth(float depth, vec2 uv) {
    float z = depth * 2.0f - 1.0f;
    vec4 positionCS = vec4(uv * 2.0f - 1.0f, z, 1.0f);
    vec4 positionVS = VeilCamera.IProjMat * positionCS;
    positionVS /= positionVS.w;
    return positionVS.xyz;
}
vec3 viewDirFromUv(vec2 uv) {
    return (VeilCamera.IViewMat * vec4(normalize(viewPosFromDepth(1.0f, uv)), 0.0f)).xyz;
}

Intersection calculateRaySphereIntersection(Sphere sphere, Ray ray) {
    Intersection result = Intersection(false,vec3(0.0f));

    vec3 L = ray.origin - sphere.centerPosition;
    float b = dot(L, ray.direction);
    float c = dot(L, L) - sphere.sphereRadius * sphere.sphereRadius;
    float discriminant = b * b - c;

    if (discriminant < 0.0) result.hit = false;

    float sqrtD = sqrt(discriminant);
    float t1 = -b - sqrtD;
    float t2 = -b + sqrtD;

    result.hit = (t1 > 0.0 || t2 > 0.0);
    result.position = t1 * ray.direction + ray.origin;

    return result;
}

vec4 worldToClipSpace(vec4 point){
    return VeilCamera.ProjMat * VeilCamera.ViewMat * point;
}

float worldPosToDepth(vec3 point) {
    vec4 clipPos = worldToClipSpace(vec4(point, 1.0));
    float depth = (clipPos.z / clipPos.w + 1) / 2;
    return depth;
}

float depthSampleToWorldDepth(in float depthSample) {
    float f = depthSample * 2.0 - 1.0;
    return 2.0 * VeilCamera.NearPlane * VeilCamera.FarPlane / (VeilCamera.FarPlane + VeilCamera.NearPlane - f * (VeilCamera.FarPlane - VeilCamera.NearPlane));
}

float worldDepthToDepthSample(in float worldDepth) {
    return 0.5-0.5*(2*VeilCamera.NearPlane*VeilCamera.FarPlane/worldDepth-VeilCamera.FarPlane-VeilCamera.NearPlane)/(VeilCamera.FarPlane-VeilCamera.NearPlane);
}

//------\\
// Main \\
//------\\
void main() {
    vec4 baseColor = texture(DiffuseSampler0, texCoord);
    vec2 fragPos = texCoord * 2.0 - vec2(1.0); // Convert texCoord from [0,1] to [-1,1] for ray generation

    gl_FragDepth = texture(DiffuseDepthSampler, texCoord).r;
    float depth = depthSampleToWorldDepth(gl_FragDepth);
    if ((depth+0.1) >= VeilCamera.FarPlane) depth = BIG_NUMBER;

    // Build and trace ray
    float fovY = 60.0;
    float aspectRatio = 21.0 / 9.0;
    Ray cameraRay = Ray(VeilCamera.CameraPosition,viewDirFromUv(texCoord));

    Sphere sphere = Sphere(vec3(0.0, 0.0, 2.0), 1.0);

    Intersection intersection = calculateRaySphereIntersection(sphere, cameraRay);
    if (intersection.hit) {

        float sphereDepth = distance(VeilCamera.CameraPosition,intersection.position);
        float clipDist = worldPosToDepth(intersection.position);

        if( worldDepthToDepthSample(clipDist) < depth ){
            fragColor = vec4(1.0f,0.0f,0.0f,1.0f);
        }
        else{
            fragColor = baseColor;
        }

    } else {
        fragColor = baseColor;
    }

}
