#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoordinate;
layout(location = 2) in vec3 normal;

uniform vec2 pos;

out vec2 texCoord;

void main(){
	texCoord = textureCoordinate;
	gl_Position.xy = position.xy + pos;
	gl_Position.z = position.z;
	gl_Position.w = 1.0;
}