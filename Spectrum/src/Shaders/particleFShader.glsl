#version 140

out vec4 out_colour;

in vec2 textureCoords1;
in vec2 textureCoords2;
in float blend;

uniform sampler2D particleTexture;
uniform int colourMode;

void main(void){

    vec4 colour1 = texture(particleTexture, textureCoords1);
    vec4 colour2 = texture(particleTexture, textureCoords2);
    vec4 finalColour = mix(colour1,colour2,blend);

    bvec4 colourTest = greaterThanEqual(finalColour, vec4(0.5,0.5,0.5,0));
    //change to be separate checks for other colours
    //RED
    if(colourMode==1){
        if(colourTest.r == false || (colourTest.g == true || colourTest.b==true)){
            discard;
        }
    }
    //GREEN
    if(colourMode==2){
        if(colourTest.g == false || (colourTest.r == true || colourTest.b==true)){
            discard;
        }
    }
    //BLUE
    if(colourMode==3){
        if(colourTest.b == false || (colourTest.g == true || colourTest.r==true)){
            discard;
        }
    }
    //YELLOW
    if(colourMode==4){
        if(colourTest.r == true || (colourTest.g == false || colourTest.r==false)){
            discard;
        }
    }
    //PURPLE
    if(colourMode==5){
         if(colourTest.g == true || (colourTest.r == false || colourTest.b==false)){
             discard;
         }
    }
    //CYAN
    if(colourMode==6){
         if(colourTest.b == true || (colourTest.g == false || colourTest.r==false)){
             discard;
         }
    }

	out_colour = finalColour;

}