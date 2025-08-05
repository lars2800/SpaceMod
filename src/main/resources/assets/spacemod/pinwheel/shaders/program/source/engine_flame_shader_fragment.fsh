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

uniform float u_sig;
uniform float u_diamondMult;
uniform float u_flameMult;
uniform float u_overallMult;
uniform float u_repeat;
uniform float u_offset;

uniform float u_rocket_flame_buffer[512];

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
    vec3 rotation;
};
struct PixelResult{
    float worldDepth;
    vec3 additiveColor;
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
vec3 rotateX(vec3 v, float a) {
    float s = sin(a), c = cos(a);
    return vec3(v.x, c*v.y - s*v.z, s*v.y + c*v.z);
}
vec3 rotateY(vec3 v, float a) {
    float s = sin(a), c = cos(a);
    return vec3(c*v.x + s*v.z, v.y, -s*v.x + c*v.z);
}
vec3 rotateZ(vec3 v, float a) {
    float s = sin(a), c = cos(a);
    return vec3(c*v.x - s*v.y, s*v.x + c*v.y, v.z);
}

VolumeIntersection intersectCube(Ray ray, Cube cube) {
    // 1. Box center & localize ray
    vec3 center = 0.5 * (cube.minBounds + cube.maxBounds);
    vec3 ro = ray.origin - center;
    vec3 rd = ray.direction;

    // 2. Inverse-rotate: Z → Y → X by negative angles
    float ax = radians(-cube.rotation.x);
    float ay = radians(-cube.rotation.y);
    float az = radians(-cube.rotation.z);
    ro = rotateZ(ro, az);
    ro = rotateY(ro, ay);
    ro = rotateX(ro, ax);
    rd = rotateZ(rd, az);
    rd = rotateY(rd, ay);
    rd = rotateX(rd, ax);

    // 3. Slab intersection with local AABB [minBounds-center, maxBounds-center]
    vec3 lb = cube.minBounds - center;
    vec3 ub = cube.maxBounds - center;

    // Avoid division by zero
    vec3 invD = 1.0 / rd;
    vec3 t0s = (lb - ro) * invD;
    vec3 t1s = (ub - ro) * invD;

    vec3 tmin3 = min(t0s, t1s);
    vec3 tmax3 = max(t0s, t1s);

    float tmin = max(max(tmin3.x, tmin3.y), tmin3.z);
    float tmax = min(min(tmax3.x, tmax3.y), tmax3.z);

    VolumeIntersection result;
    result.didhit = (tmax >= max(tmin, 0.0));

    if (!result.didhit) {
        return result;
    }

    // 4. Compute local hit points
    float tin  = max(tmin, 0.0);
    float tout = tmax;
    vec3 pinLocal = ro + rd * tin;
    vec3 poutLocal = ro + rd * tout;

    // 5. Forward-rotate back: X → Y → Z, then add center
    ax = radians(cube.rotation.x);
    ay = radians(cube.rotation.y);
    az = radians(cube.rotation.z);
    pinLocal = rotateX(pinLocal, ax);
    pinLocal = rotateY(pinLocal, ay);
    pinLocal = rotateZ(pinLocal, az);
    poutLocal = rotateX(poutLocal, ax);
    poutLocal = rotateY(poutLocal, ay);
    poutLocal = rotateZ(poutLocal, az);

    result.positionIn  = pinLocal  + center;
    result.positionOut = poutLocal + center;
    return result;
}

//----------\\
// Coloring \\
//----------\\
vec3 getGeneratedCoordinates(vec3 point, Cube cube) {
    // 1. Compute cube center
    vec3 center = 0.5 * (cube.minBounds + cube.maxBounds);

    // 2. Translate point to cube-local origin
    vec3 localPoint = point - center;

    // 3. Inverse-rotate point into cube's local axis-aligned space
    float ax = radians(-cube.rotation.x);
    float ay = radians(-cube.rotation.y);
    float az = radians(-cube.rotation.z);
    localPoint = rotateZ(localPoint, az);
    localPoint = rotateY(localPoint, ay);
    localPoint = rotateX(localPoint, ax);

    // 4. AABB bounds in local space (centered)
    vec3 lb = cube.minBounds - center;
    vec3 ub = cube.maxBounds - center;

    // 5. Normalize localPoint to [0,1] range
    return vec3(
    (localPoint.x - lb.x) / (ub.x - lb.x),
    (localPoint.y - lb.y) / (ub.y - lb.y),
    (localPoint.z - lb.z) / (ub.z - lb.z)
    );
}
float getVolumeModulo(vec3 point, Cube volume){
    vec3 normalizedCoordinates = getGeneratedCoordinates(point,volume);

    float offset = u_offset;
    float repeat = u_repeat;

    float a = mod(( normalizedCoordinates.y + offset ), repeat);
    float b = a / repeat;
    float c = -2.0 * b * b + 2.0 * b + 0.5;

    return c;
}
float getFlameShape(vec3 point, Cube volume) {
    float moduloInput = getVolumeModulo(point, volume);
    vec3 result = getGeneratedCoordinates(point, volume);

    float a = 1.0 / moduloInput;

    result = result * vec3(2.0f, 1.0f, 2.0f) + vec3(-1.0f, -0.2f, -1.0f);
    result = result * vec3(a, 1.0f, a) * 0.3f;

    // Inline quadraticSphereGradient
    float dist2 = dot(result, result);
    float gradient = clamp(dist2, 0.0, 1.0);

    // Inline flameShapeColorRamp
    float pos1 = 0.0;
    vec3 color1 = vec3(0.0, 0.0, 0.0);

    float pos2 = 0.03;
    vec3 color2 = vec3(0.5, 0.5, 0.5);

    float pos3 = 0.1;
    vec3 color3 = vec3(0.0, 0.0, 0.0);

    vec3 color;
    if (gradient <= pos1) {
        color = color1;
    } else if (gradient <= pos2) {
        float f = (gradient - pos1) / (pos2 - pos1);
        color = mix(color1, color2, f);
    } else if (gradient <= pos3) {
        float f = (gradient - pos2) / (pos3 - pos2);
        color = mix(color2, color3, f);
    } else {
        color = color3;
    }

    return color.x;
}
float getDiamondShape(vec3 point, Cube volume) {
    float moduloInput = getVolumeModulo(point, volume);
    vec3 result = getGeneratedCoordinates(point, volume);

    float a = 1.0f / (1.0f-moduloInput);

    result = result * vec3(2.0f, 1.0f, 2.0f) + vec3(-1.0f, -0.2f, -1.0f);
    result = result * vec3(a, 1.0f, a) * 0.3f;

    // Inline quadraticSphereGradient
    float dist2 = dot(result, result);
    float gradient = clamp(dist2, 0.0, 1.0);

    // Inline flameShapeColorRamp
    float pos1 = 0.0;
    vec3 color1 = vec3(0.0, 0.0, 0.0);

    float pos2 = 0.03;
    vec3 color2 = vec3(0.5, 0.5, 0.5);

    float pos3 = 0.1;
    vec3 color3 = vec3(0.0, 0.0, 0.0);

    vec3 color;
    if (gradient <= pos1) {
        color = color1;
    } else if (gradient <= pos2) {
        float f = (gradient - pos1) / (pos2 - pos1);
        color = mix(color1, color2, f);
    } else if (gradient <= pos3) {
        float f = (gradient - pos2) / (pos3 - pos2);
        color = mix(color2, color3, f);
    } else {
        color = color3;
    }

    return color.x;
}
vec3 colorRampTwoPoints(vec3 colorA, float posA, vec3 colorB, float posB, float point) {
    point = clamp((point - posA) / (posB - posA), 0.0, 1.0);
    return mix(colorA, colorB, point);
}
vec3 getFlameColor(vec3 point, Cube cube){
    float rampPoint = getGeneratedCoordinates(point,cube).y;
    return colorRampTwoPoints(vec3(1.0f,1.0f,1.0f),0.25f,vec3(1.0f,0.0f,0.5f),1.0f,rampPoint);
}
float fallOff(vec3 point,Cube cube){
    float a = (1-getGeneratedCoordinates(point,cube).y);
    if ( a <= 0 ){
        a = 0.01f;
    }
    return a * sqrt(a);
}
vec3 getVolumeColor(VolumeIntersection volumeIntersection,Cube volume){
    vec3 resultColor = vec3(0.0);

    vec3 normalizedDirection = normalize( volumeIntersection.positionOut - volumeIntersection.positionIn );
    float crossDistance = distance( volumeIntersection.positionIn, volumeIntersection.positionOut );
    float stepDist = 0.01f;

    float t = 0.0f;
    while ( t < crossDistance ){
        vec3 point = volumeIntersection.positionIn + ( normalizedDirection * t );
        t += stepDist;

        vec3 color = getFlameColor(point,volume);
        float intensety = 0.0125f * ( (getDiamondShape(point,volume) * u_diamondMult) + (getFlameShape(point,volume) * u_flameMult) ) * u_overallMult * fallOff(point,volume);
        resultColor += color * intensety;
        
    }
    return resultColor;
}

PixelResult getPixelResult(Ray cameraRay,Cube volume){
    PixelResult result = PixelResult(BIG_NUMBER,vec3(0.0f));
    VolumeIntersection intersection = intersectCube(cameraRay, volume);
    if (intersection.didhit ){
        result.additiveColor = getVolumeColor(intersection,volume);
        result.worldDepth = distance( VeilCamera.CameraPosition, intersection.positionIn );
    }
    return result;
}

//------\\
// Main \\
//------\\
void main() {
    // Get inputs
    vec4  baseColor = texture(DiffuseSampler0, texCoord);
    float baseDepth = texture(DiffuseDepthSampler, texCoord).r;
    float worldDepth = depthSampleToWorldDepth(baseDepth);

    fragColor = baseColor;
    gl_FragDepth = baseDepth;
    
    // Ray tracing
    vec3 addedColor = vec3(0.0f);

    Ray cameraRay = Ray(VeilCamera.CameraPosition, getWorldRayDir(texCoord));

    Cube cube;
    for(int i = 0; i < 512; i = i + 6) {

        vec3 position;
        vec3 rotation;
        position.x = u_rocket_flame_buffer[i+0];
        position.y = u_rocket_flame_buffer[i+1];
        position.z = u_rocket_flame_buffer[i+2];
        rotation.x = u_rocket_flame_buffer[i+3];
        rotation.y = u_rocket_flame_buffer[i+4];
        rotation.z = u_rocket_flame_buffer[i+5];

        if ( rotation.x == 0 ){
            if ( rotation.y == 0 ){
                if (rotation.x == 0){
                    break;
                }
            }
        }

        cube.minBounds = vec3(  0.0f,0.0f,0.0f) + position;
        cube.maxBounds = vec3(  1.0f,-10.0f,1.0f) + position;
        cube.rotation = rotation;

        PixelResult pixelResult = getPixelResult(cameraRay,cube);
        if ( pixelResult.worldDepth < worldDepth){
            addedColor += pixelResult.additiveColor;
        }
    }

    // Output
    fragColor = vec4( baseColor.xyz + addedColor, 1.0 );

} 