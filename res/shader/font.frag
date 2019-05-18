#version 120

varying vec4 color;

uniform sampler2D texture;

varying vec2 texCoord;

void main(){
	gl_FragColor = vec4(color.rgb, texture2D(texture, texCoord).a);
}