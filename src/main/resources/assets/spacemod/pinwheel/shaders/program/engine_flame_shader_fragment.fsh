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

in vec2 texCoord;
out vec4 fragColor;

//-----------\\
// Constants \\
//-----------\\
const float BIG_NUMBER = 999999.99f;

//---------\\
// Structs \\
//---------\\
struct Ray{
    vec3 origin;
    vec3 direction;
};
struct SolidRayIntersection{
    bool didhit;
    vec3 surfacePosition;
};
struct Sphere {
    vec3 centerPosition;
    float sphereRadius;
};

//---------\\
// Helpers \\
//---------\\

// From https://github.com/lars2800/MC-spacemod/blob/master/src/main/resources/assets/spacemod/pinwheel/shaders/include/helpers.glsl
// And: https://github.com/GuyApooye/Hubble/blob/edb29e45154bbe6f14350c913f111a72b24e3bb5/common/src/main/resources/assets/hubble/pinwheel/shaders/program/post/planet.fsh // Thank you!
float depthSampleToWorldDepth(in float depthSample) {
    float f = depthSample * 2.0 - 1.0;
    return 2.0 * VeilCamera.NearPlane * VeilCamera.FarPlane / (VeilCamera.FarPlane + VeilCamera.NearPlane - f * (VeilCamera.FarPlane - VeilCamera.NearPlane));
}
float worldDepthToDepthSample(in float worldDepth) {
    return 0.5-0.5*(2*VeilCamera.NearPlane*VeilCamera.FarPlane/worldDepth-VeilCamera.FarPlane-VeilCamera.NearPlane)/(VeilCamera.FarPlane-VeilCamera.NearPlane);
}
vec3 viewPosFromDepth(float depth, vec2 uv) {
    float z = depth * 2.0f - 1.0f;
    vec4 positionCS = vec4(uv * 2.0f - 1.0f, z, 1.0f);
    vec4 positionVS = VeilCamera.IProjMat * positionCS;
    positionVS /= positionVS.w;
    return positionVS.xyz;
}
vec3 viewToWorldSpaceDirection(vec3 direction) {
    return (VeilCamera.IViewMat * vec4(direction, 0.0f)).xyz;
}
vec3 viewToWorldSpace(vec3 positionVS) {
    return VeilCamera.CameraPosition + (VeilCamera.IViewMat * vec4(positionVS, 1.0f)).xyz;
}
vec3 viewToPlayerSpace(vec3 positionVS) {
    return (VeilCamera.IViewMat * vec4(positionVS, 1.0f)).xyz;
}
vec3 playerSpaceToWorldSpace(vec3 positionPS) {
    return positionPS + VeilCamera.CameraPosition;
}
vec3 worldToViewSpaceDirection(vec3 viewSpace) {
    return (VeilCamera.ViewMat * vec4(viewSpace, 0.0f)).xyz;
}
vec3 getWorldRayDir(vec2 uv) {
    vec4 clip = vec4(uv * 2.0 - 1.0, 1.0, 1.0);
    vec4 view = VeilCamera.IProjMat * clip;
    view /= view.w;
    vec4 world = VeilCamera.IViewMat * view;
    return normalize(world.xyz - VeilCamera.CameraPosition);
}

//-------------\\
// Ray tracing \\
//-------------\\
SolidRayIntersection calculateRaySphereIntersection(Sphere sphere, Ray ray) {
    SolidRayIntersection result = SolidRayIntersection(false,vec3(0));

    vec3 L = ray.origin - sphere.centerPosition;
    float b = dot(L, ray.direction);
    float c = dot(L, L) - sphere.sphereRadius * sphere.sphereRadius;
    float discriminant = b * b - c;

    if (discriminant < 0.0) result.didhit = false;

    float sqrtD = sqrt(discriminant);
    float t1 = -b - sqrtD;
    float t2 = -b + sqrtD;

    result.didhit = (t1 > 0.0 || t2 > 0.0);

    float t = (t1 > 0.0) ? t1 : t2;
    if (t <= 0.0) return result; // no valid intersection in front of camera
    result.surfacePosition = ray.origin + ray.direction * t;

    return result;
}

//------\\
// Main \\
//------\\
void main() {
    // Get inputs
    vec4  baseColor = texture(DiffuseSampler0, texCoord);
    float baseDepth = texture(DiffuseDepthSampler, texCoord).r;

    fragColor = baseColor;
    gl_FragDepth = baseDepth;
    
    // Ray trace
    Ray cameraRay = Ray(VeilCamera.CameraPosition, getWorldRayDir(texCoord));
    Sphere sphere = Sphere(vec3(0.0), 1.0);
    SolidRayIntersection intersection = calculateRaySphereIntersection(sphere,cameraRay);

    if ( intersection.didhit ){
        vec4 hitPointWS = vec4(intersection.surfacePosition,1.0f);
        vec4 hitPointVS = VeilCamera.ViewMat * (hitPointWS);
        vec4 hitPointCS = VeilCamera.ProjMat * hitPointVS;
        vec3 hitPointDepthCS = hitPointCS.xyz / hitPointCS.w;
        fragColor = vec4(vec3(hitPointDepthCS.z),1.0f);
    }
}