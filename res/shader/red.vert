#version 330 core

layout(location = 0) in vec3 position;

uniform vec2 pos;

out vec2 texCoord;

void main(){
	texCoord = position.xy;
	gl_Position.xy = position.xy + pos;
	gl_Position.z = position.z;
	gl_Position.w = 1.0;
}