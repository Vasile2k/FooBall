#version 330 core

layout(location = 0) out vec4 color;

uniform sampler2D texture;
uniform float active;

in vec2 texCoord;

void main(){
	color = texture2D(texture, texCoord);
	if(active > 0.0){
		color.g = color.g * 0.75;
		color.b = color.b * 0.25;
	}
}