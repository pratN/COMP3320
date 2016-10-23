#version 400 core

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;
in vec4 shadowCoords;

out vec4 out_Colour;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D shadowMap;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform vec3 lightColour[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;
uniform vec3 attenuation[4];
uniform int invis;
uniform int pcfCount;
uniform float mapSize;
uniform float totalTexels;
//const int pcfCount = 12;
//const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);
const float levels = 3.0;


void main(void) {

    //float mapSize = 8192.0;
    float texelSize = 1.0 / mapSize;
    float total = 0.0;

    for (int x=-pcfCount; x<=pcfCount; x++ ){
        for(int y=-pcfCount;y<=pcfCount;y++){
                float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x,y) * texelSize).r;
                if(shadowCoords.z > objectNearestLight){
                        total += 1.0;
                    }
        }
    }

    total /= totalTexels;
    float lightFactor = 1.0 - (total * shadowCoords.w);


    vec4 blendMapColour = texture(blendMap, pass_textureCoordinates);

    float backTextureAmount = 1-(blendMapColour.r+blendMapColour.g+blendMapColour.b);
    vec2 tiledCoords = pass_textureCoordinates*40.0;
    vec4 backgroundTextureColour = texture(backgroundTexture,tiledCoords)*backTextureAmount;
    vec4 rTextureColour = texture(rTexture,tiledCoords)*blendMapColour.r;
    vec4 gTextureColour = texture(gTexture,tiledCoords)*blendMapColour.g;
    vec4 bTextureColour = texture(bTexture,tiledCoords)*blendMapColour.b;

    vec4 totalColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0,0,0);
    vec3 totalSpecular = vec3(0,0,0);

    for( int i=0;i<4;i++){
        float distance = length(toLightVector[i]);
        float attFactor = attenuation[i].x + (attenuation[i].y*distance)+(attenuation[i].z*distance*distance);
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDot1 = dot(unitNormal,unitLightVector);
        float brightness = max(nDot1,0.0);
//        float level = floor(brightness * levels);
//        brightness = level / levels;
        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
        float specularFactor = dot(reflectedLightDirection,unitVectorToCamera);
        specularFactor = max(specularFactor,0.0);
        float dampedFactor = pow(specularFactor,shineDamper);
//        level = floor(dampedFactor * levels);
//        dampedFactor = level / levels;
        totalDiffuse = totalDiffuse + (brightness * lightColour[i])/attFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i])/attFactor;
    }

    totalDiffuse = max(totalDiffuse * lightFactor,0.2);
    out_Colour = vec4(totalDiffuse,1.0) * totalColour + vec4 (totalSpecular,1.0);
    out_Colour = mix(vec4(skyColour,1.0),out_Colour,visibility);
}
