#version 120

varying vec4 color;
varying vec2 texCoord;

void main(){
	// Set position
	gl_Position = gl_Vertex;
	// Pass-trough others
	color = gl_Color;
	texCoord = gl_MultiTexCoord0.st;
}