#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;
in vec3 fromLightVector;

out vec4 out_Colour;

uniform sampler2D reflectTexture;
uniform sampler2D refractTexture;
uniform sampler2D dudvMap; //for water ripple effect
uniform sampler2D normMap; //for water normal lighting effect
uniform sampler2D depthMap;
uniform vec3 lightColour;

uniform float rippleMove; //ripple offset will change over time to make water look like its moving
uniform float underWater;


const float rippleStrength = 0.01;
const float shine = 30.0;
const float lightReflect = 0.5;


void main(void) {

    vec2 normDeviceSpace = (clipSpace.xy/clipSpace.w)/2.0+0.5; //to convert from normal xy coord system to only considering pos xy
    vec2 refractTexCoords = vec2(normDeviceSpace.x, normDeviceSpace.y);
    vec2 reflectTexCoords = vec2(normDeviceSpace.x, -normDeviceSpace.y);

    float near = 0.1; //must be same as near and far planes in master renderer
    float far = 1000.0;
    float depth = texture(depthMap, refractTexCoords).r; //only need red component as that were depth info is stored
    //(gives # between 0-1 that reps depth value but need to convert to distance
    float floorDistance = 2.0*near*far/(far+near-(2.0*depth-1.0)*(far-near)); //calc distance from camera to floor of water

    depth = gl_FragCoord.z;
    float waterDistance = 2.0*near*far/(far+near-(2.0*depth-1.0)*(far-near));

    float waterDepth = floorDistance - waterDistance;


    vec2 distortedTexCoords = texture(dudvMap, vec2(textureCoords.x + rippleMove, textureCoords.y)).rg*0.1;
    distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+rippleMove);
    vec2 waterDistortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * rippleStrength;

    refractTexCoords += waterDistortion;
    refractTexCoords = clamp(refractTexCoords,0.001,0.999);//stops any glitching by going below 0


    reflectTexCoords += waterDistortion;
    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);



	vec4 reflectColour = texture(reflectTexture,reflectTexCoords);
	vec4 refractColour = texture(refractTexture,refractTexCoords);

	vec3 viewVector = normalize(toCameraVector);
	float refractiveFactor = dot(viewVector,vec3(0.0,underWater,0.0));//the fresnel effetc gets the dot prod of vector pointing from water to camera and the normal of water

    vec4 normMapColour = texture(normMap, distortedTexCoords); //sample norm map use same coords for distort
    vec3 normal = vec3(normMapColour.r*2.0-1.0, normMapColour.b, normMapColour.g*2.0-1.0);
    //using the rgb to get the xyz norms but have to convert x and z norms to allow for negatives
    normal = normalize(normal);

    //specular lighting for water, reflecting light off water and taking dot prod of light vec and view vec
    //closer they are the more light will go to the camera
    vec3 reflectedLight = reflect(normalize(fromLightVector), normal);
    float specular = max(dot(reflectedLight, viewVector), 0.0);
    specular = pow(specular, shine);
    vec3 specularHighlights = lightColour * specular * lightReflect;

    if(underWater==-1)
     out_Colour = mix(refractColour,refractColour,refractiveFactor)+vec4(specularHighlights,0.0); //textures mixed based on fresnel effect

    else
	    out_Colour = mix(reflectColour,refractColour,refractiveFactor)+vec4(specularHighlights,0.0); //textures mixed based on fresnel effect
	//out_Colour = mix(out_Colour, vec4(0.0, 0.1, 0.1, 1.0),0.05); //add a really light tint of blue to the water
    out_Colour.a = clamp(waterDepth/5.0, 0.0, 1.0);

}