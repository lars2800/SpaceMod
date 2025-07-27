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
struct VolumeIntersection{
    bool didhit;
    vec3 positionIn;
    vec3 positionOut;
};
struct Cube {
    vec3 minBounds;
    vec3 maxBounds;
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
VolumeIntersection calculateRayCubeIntersection(Cube cube, Ray ray) {
    VolumeIntersection result = VolumeIntersection(false, vec3(0), vec3(0));

    vec3 tMin = (cube.minBounds - ray.origin) / ray.direction;
    vec3 tMax = (cube.maxBounds - ray.origin) / ray.direction;

    vec3 t1 = min(tMin, tMax);
    vec3 t2 = max(tMin, tMax);

    float tNear = max(max(t1.x, t1.y), t1.z);
    float tFar  = min(min(t2.x, t2.y), t2.z);

    if (tNear > tFar || tFar < 0.0) {
        result.didhit = false;
        return result;
    }

    result.didhit = true;
    result.positionIn = ray.origin + tNear * ray.direction;
    result.positionOut = ray.origin + tFar * ray.direction;

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
    Cube cube = Cube(
        vec3( 0.0f,1.0f,0.0f),
        vec3(-1.0f,-9.0f,1.0f)
    );
    VolumeIntersection intersection = calculateRayCubeIntersection(cube,cameraRay);

    if (intersection.didhit ){
        vec3 resultColor = vec3(0.0);

        vec3 normalizedDirection = normalize( intersection.positionOut - intersection.positionIn );
        float crossDistance = distance( intersection.positionIn, intersection.positionOut );
        float stepDist = 0.01f;

        float t = 0.0f;
        while ( t < crossDistance ){
            vec3 point = intersection.positionIn + ( normalizedDirection * t );
            vec3 color = vec3(1.0f);
            float intensety = 0.0025f;
            resultColor += color * intensety;
            t += stepDist;
        }

        float worldDistance = distance( VeilCamera.CameraPosition, intersection.positionIn );

        if ( worldDistance < depthSampleToWorldDepth(baseDepth) ){
            fragColor = vec4(resultColor+baseColor.xyz,1.0f);
        }


    }
}