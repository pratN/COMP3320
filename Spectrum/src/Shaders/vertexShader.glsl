#version 400 core

in vec3 pos;
in vec2 texCoords;
out vec2 pass_texCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;

void main(void) {

    gl_Position = projectionMatrix * transformationMatrix * vec4(pos,1.0);
    pass_texCoords = texCoords;

}
