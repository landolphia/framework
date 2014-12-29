#version 130

out vec4 outputColor;

uniform float fragLoopDuration;
uniform float time;

const vec4 c1 = vec4(0.8f, 0.9f, 0.4f, 1.0f);
const vec4 c2 = vec4(0.2f, 0.5f, 0.6f, 1.0f);

void main()
{
	float timeScale = 3.14159f * 2.0f / fragLoopDuration;
	float curTime = mod(time, fragLoopDuration);
	float curLerp = cos(curTime * timeScale);

	outputColor = mix(c1, c2, curLerp);
}
