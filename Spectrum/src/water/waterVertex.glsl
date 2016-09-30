#version 400 core

in vec2 position;


out vec4 clipSpace;
out vec2 textureCoords;
out vec3 toCameraVector; //vec for water poitning to camera for fresnel effect
out vec3 fromLightVector; //vec for pointing from light to water

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 camPos;
uniform vec3 lightPos;
uniform float tiling;

//const float tiling = 6.0;


void main(void) {

	vec4 worldPosition = modelMatrix * vec4(position.x, 0.0, position.y, 1.0);
	clipSpace = projectionMatrix * viewMatrix * modelMatrix * vec4(position.x, 0.0, position.y, 1.0);
	gl_Position = clipSpace;
	textureCoords = vec2(position.x/2.0 + 0.5, position.y/2.0 + 0.5)*tiling;
	toCameraVector = camPos - worldPosition.xyz;
	fromLightVector = worldPosition.xyz - lightPos;


}