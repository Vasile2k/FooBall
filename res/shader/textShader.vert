#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoordinate;
layout(location = 2) in vec3 normal;

uniform float active;
uniform vec2 pos;
uniform mat4 viewProjMatrix;
uniform mat4 modelMatrix;

out vec2 texCoord;

void main(){
	// Texture is not mapped correctly in OpenGL system so fix it there
	// TODO: maybe do it better in Texture class
	texCoord = vec2(textureCoordinate.x, 1.0 - textureCoordinate.y);
	vec4 position4 = vec4(position, 1.0);
	gl_Position = viewProjMatrix * modelMatrix * position4;
}