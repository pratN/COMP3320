#version 400 core

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Colour;

uniform sampler2D modelTexture;
uniform vec3 lightColour[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;


void main(void) {
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    for(int i=0;i<4;i++){
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDot1 = dot(unitNormal,unitLightVector);
        float brightness = max(nDot1,0.0);
        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
        float specularFactor = dot(reflectedLightDirection,unitVectorToCamera);
        specularFactor = max(specularFactor,0.0);
        float dampedFactor = pow(specularFactor,shineDamper);
                totalDiffuse = totalDiffuse + brightness * lightColour[i];
                totalSpecular = totalSpecular + dampedFactor * reflectivity * lightColour[i];

    }
    totalDiffuse = max(totalDiffuse,0.2);

    vec4 textureColour = texture(modelTexture, pass_textureCoordinates);
    if(textureColour.a<0.5){
        discard;
    }

    out_Colour = vec4(totalDiffuse,1.0) * textureColour + vec4 (totalSpecular,1.0);
    out_Colour = mix(vec4(skyColour,1.0),out_Colour,visibility);
}
