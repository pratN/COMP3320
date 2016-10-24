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
uniform vec3 attenuation[4];
uniform int colourMode;

const float levels = 3.0;

void main(void) {
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    for(int i=0;i<4;i++){
        float distance = length(toLightVector[i]);
        float attFactor = attenuation[i].x + (attenuation[i].y*distance)+(attenuation[i].z*distance*distance);
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDot1 = dot(unitNormal,unitLightVector);
        float brightness = max(nDot1,0.0);
        //for cell shading
//      float level = floor(brightness * levels);
//      brightness = level / levels;
        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
        float specularFactor = dot(reflectedLightDirection,unitVectorToCamera);
        specularFactor = max(specularFactor,0.0);
        float dampedFactor = pow(specularFactor,shineDamper);
//      level = floor(dampedFactor * levels);
//      dampedFactor = level / levels;
        totalDiffuse = totalDiffuse + (brightness * lightColour[i])/attFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i])/attFactor;

    }
    totalDiffuse = max(totalDiffuse,0.2);

    vec4 textureColour = texture(modelTexture, pass_textureCoordinates);
    if(textureColour.a<0.5){
        discard;
    }

    vec4 final_colour = vec4(totalDiffuse,1.0) * textureColour + vec4 (totalSpecular,1.0);
    final_colour = mix(vec4(skyColour,1.0),final_colour,visibility);
    bvec4 colourTest = greaterThanEqual(final_colour, vec4(0.4,0.4,0.4,0));

    //change to be separate checks for other colours
    //RED
    if(colourMode==1){
        if(colourTest.r == false || colourTest.g == true || colourTest.b==true){
            discard;
        }
    }
    //GREEN
    if(colourMode==2){
        if(colourTest.g == false || colourTest.r == true || colourTest.b==true){
            discard;
        }
    }
    //BLUE
    if(colourMode==3){
        if(colourTest.b == false || colourTest.g == true || colourTest.r==true){
            discard;
        }
    }
    //CYAN
    if(colourMode==4){
        if(colourTest.r == true || colourTest.g == false || colourTest.b==false){
        discard;
                }
        }
    //YELLOW
    if(colourMode==6){
        if(colourTest.b == true || colourTest.g == false || colourTest.r==false){
            discard;
        }
    }
    //PURPLE
    if(colourMode==5){
         if(colourTest.g == true || colourTest.r == false || colourTest.b==false){
             discard;
         }
    }
    out_Colour = final_colour;

}

