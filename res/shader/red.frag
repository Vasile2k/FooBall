#version 330 core

layout(location = 0) out vec4 color;

uniform sampler2D texture;

in vec2 texCoord;

void main(){
	color = texture2D(texture, texCoord);
}