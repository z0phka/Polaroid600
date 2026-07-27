#version 330

#moj_import <minecraft:globals.glsl>

uniform sampler2D MainSampler;
uniform sampler2D MainDepthSampler;
uniform sampler2D TranslucentSampler;
uniform sampler2D TranslucentDepthSampler;
uniform sampler2D ItemEntitySampler;
uniform sampler2D ItemEntityDepthSampler;
uniform sampler2D ParticlesSampler;
uniform sampler2D ParticlesDepthSampler;
uniform sampler2D WeatherSampler;
uniform sampler2D WeatherDepthSampler;
uniform sampler2D CloudsSampler;
uniform sampler2D CloudsDepthSampler;

layout(std140) uniform DOFConfig {
    int AF;
    float APERTURE;
    float FOCAL_LENGTH;
};

in vec2 texCoord;

vec4 color_layers[6] = vec4[](vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0));
float depth_layers[6] = float[](0, 0, 0, 0, 0, 0);
int active_layers = 0;

float near = 0.1;
float far = 2048.0;

out vec4 fragColor;

const float GOLDEN_ANGLE = 2.39996323;
const float MAX_BLUR_SIZE = 20.0;
const float RAD_SCALE = 0.5;

const float BLUR_FACTOR = 10;

/*
transparency.fsh
*/

float sceneDepth(vec2 uv) {
    return max(
        texture(MainDepthSampler, uv).r,
        max(texture(TranslucentDepthSampler, uv).r,
        max(texture(ItemEntityDepthSampler, uv).r,
        max(texture(ParticlesDepthSampler, uv).r,
        max(texture(WeatherDepthSampler, uv).r,
            texture(CloudsDepthSampler, uv).r))))
    );
}

void try_insert(vec4 color, float depth) {
    if (color.a == 0.0) {
        return;
    }

    color_layers[active_layers] = color;
    depth_layers[active_layers] = depth;

    int jj = active_layers++;
    int ii = jj - 1;
    while (jj > 0 && depth_layers[jj] < depth_layers[ii]) {
        float depthTemp = depth_layers[ii];
        depth_layers[ii] = depth_layers[jj];
        depth_layers[jj] = depthTemp;

        vec4 colorTemp = color_layers[ii];
        color_layers[ii] = color_layers[jj];
        color_layers[jj] = colorTemp;

        jj = ii--;
    }
}

vec3 blend(vec3 dst, vec4 src) {
    return (dst * (1.0 - src.a)) + src.rgb;
}

vec4 sceneColor(vec2 coord) {
    color_layers[0] = vec4(texture(MainSampler, coord).rgb, 1.0);
    depth_layers[0] = texture(MainDepthSampler, coord).r;
    active_layers = 1;

    try_insert(texture(TranslucentSampler, coord), texture(TranslucentDepthSampler, coord).r);
    try_insert(texture(ItemEntitySampler, coord), texture(ItemEntityDepthSampler, coord).r);
    try_insert(texture(ParticlesSampler, coord), texture(ParticlesDepthSampler, coord).r);
    try_insert(texture(WeatherSampler, coord), texture(WeatherDepthSampler, coord).r);
    try_insert(texture(CloudsSampler, coord), texture(CloudsDepthSampler, coord).r);

    vec3 texelAccum = color_layers[0].rgb;
    for (int ii = 1; ii < active_layers; ++ii) {
        texelAccum = blend(texelAccum, color_layers[ii]);
    }

    return vec4(texelAccum.rgb, 1.0);
}

float linearizeDepth(float depth)
{
    float z = depth * 2.0 - 1.0;
    return (near * far) / (far + near - z * (far - near));
}

float circleOfConfusion(float S1, float S2, float N, float f){
    return abs((f * f * (S2 - S1))/(N * S2 * (S1 - f)));
}

float getBlurSize(float depth, float focusPoint)
{
    float coc = circleOfConfusion(focusPoint, depth,APERTURE, FOCAL_LENGTH);
    if (depth > focusPoint && AF == 0) {
        //coc *= 0.66;
    }

	return coc * MAX_BLUR_SIZE * BLUR_FACTOR;
}

/*
DOF, https://blog.voxagon.se/2018/05/04/bokeh-depth-of-field-in-single-pass.html
*/

vec3 depthOfField(float focusPoint)
{
    vec2 pixelSize = vec2(1)/ScreenSize;
	float centerDepth = linearizeDepth(1 - sceneDepth(texCoord));
	float centerSize = getBlurSize(centerDepth, focusPoint);
	if(true){
	//return vec3(centerSize/MAX_BLUR_SIZE);
	}
	vec3 color = sceneColor(texCoord).rgb;
	float tot = 1.0;
	float radius = RAD_SCALE;
	for (float ang = 0.0; radius<MAX_BLUR_SIZE; ang += GOLDEN_ANGLE)
	{
		vec2 tc = texCoord + vec2(cos(ang), sin(ang)) * pixelSize * radius;
		vec3 sampleColor = sceneColor(tc).rgb;
		float sampleDepth = linearizeDepth(1 - sceneDepth(tc));
		float sampleSize = getBlurSize(sampleDepth, focusPoint);
		if (sampleDepth > centerDepth)
			sampleSize = clamp(sampleSize, 0.0, centerSize*2.0);
		float m = smoothstep(radius-0.5, radius+0.5, sampleSize);
		color += mix(color/tot, sampleColor, m);
		tot += 1.0;   radius += RAD_SCALE/radius;
	}
	return color /= tot;
}


void main() {
    float focusPoint = 0.66;
    if(AF == 1){
        vec2 center = vec2(0.5,0.5);
        float centerDepth = linearizeDepth(1 - sceneDepth(center));
        focusPoint = centerDepth;
    }
    fragColor = vec4(depthOfField(focusPoint), 1);
}