#version 400 core

in vec3 pos;
in vec2 texCoords;
out vec2 pass_texCoords;

uniform mat4 transformationMatrix;

void main(void) {

    gl_Position = transformationMatrix * vec4(pos,1.0);
    pass_texCoords = texCoords;

}
