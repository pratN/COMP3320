#version 400 core

in vec2 pass_texCoords;
out vec4 out_Colour;

uniform sampler2D texSampler;

void main(void) {

    out_Colour = texture(texSampler, pass_texCoords);

}
