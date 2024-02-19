#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
precision PRECISION int;
#else
    #define PRECISION
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture0;
uniform float u_amount;
uniform float u_speed;
uniform float u_time;
        
// change these values to 0.0 to turn off individual effects
float vertJerkOpt = 1.0;
float vertMovementOpt = 1.0;
float bottomStaticOpt = 1.0;
float scalinesOpt = 2.0;
float rgbOffsetOpt = 1.0;
float horzFuzzOpt = 4.0;

// Noise generation functions borrowed from:
// https://github.com/ashima/webgl-noise/blob/master/src/noise2D.glsl

vec3 mod289(vec3 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
    return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)
{
    const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                        0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                        -0.577350269189626,  // -1.0 + 2.0 * C.x
                        0.024390243902439); // 1.0 / 41.0
    // First corner
    vec2 i  = floor(v + dot(v, C.yy) );
    vec2 x0 = v -   i + dot(i, C.xx);

    // Other corners
    vec2 i1;
    //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
    //i1.y = 1.0 - i1.x;
    i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    // x0 = x0 - 0.0 + 0.0 * C.xx ;
    // x1 = x0 - i1 + 1.0 * C.xx ;
    // x2 = x0 - 1.0 + 2.0 * C.xx ;
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;

    // Permutations
    i = mod289(i); // Avoid truncation effects in permutation
    vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
                      + i.x + vec3(0.0, i1.x, 1.0 ));

    vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
    m = m*m ;
    m = m*m ;

    // Gradients: 41 points uniformly over a line, mapped onto a diamond.
    // The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;

    // Normalise gradients implicitly by scaling m
    // Approximation of: m *= inversesqrt( a0*a0 + h*h );
    m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

    // Compute final noise value at P
    vec3 g;
    g.x  = a0.x  * x0.x  + h.x  * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}

float fnoise(vec2 v) {
    return fract(sin(dot(v, vec2(12.9898, 78.233))) * 43758.5453) * 0.55;
}

float staticV(vec2 uv) {
    float staticHeight = fnoise(vec2(9.0,u_time*1.2+3.0))*0.3+5.0;
    float staticAmount = fnoise(vec2(1.0,u_time*1.2-6.0))*0.1+0.3;
    float staticStrength = fnoise(vec2(-9.75,u_time*0.6-3.0))*2.0+2.0;
    return (1.0-step(fnoise(vec2(5.0*pow(u_time,2.0)+pow(uv.x*7.0,1.2),pow((mod(u_time,100.0)+100.0)*uv.y*0.3+3.0,staticHeight))),staticAmount))*staticStrength;
}

void main()
{

    vec2 uv = v_texCoords; 

    float fuzzOffset = fnoise(vec2(u_time*15.0,uv.y*80.0))*0.003;
    float largeFuzzOffset = fnoise(vec2(u_time*1.0,uv.y*25.0))*0.004;

    float vertMovementOn = (1.0-step(snoise(vec2(u_time*0.2,8.0)),0.4))*vertMovementOpt;
    float vertJerk = (1.0-step(fnoise(vec2(u_time*1.5,5.0)),0.6))*vertJerkOpt;
    float vertJerk2 = (1.0-step(fnoise(vec2(u_time*5.5,5.0)),0.2))*vertJerkOpt;
    float yOffset = abs(sin(u_time)*4.0)*vertMovementOn+vertJerk*vertJerk2*0.3;
    float y = mod(uv.y+yOffset,1.0);

    float xOffset = (fuzzOffset + largeFuzzOffset) * horzFuzzOpt;

    float staticVal = 0.0;

    for (float y = -1.0; y <= 1.0; y += 1.0) {
        float maxDist = 5.0/200.0;
        float dist = y/200.0;
        staticVal += staticV(vec2(uv.x,uv.y+dist))*(maxDist-abs(dist))*1.5;
    }

    staticVal *= bottomStaticOpt;

    float red 	=   texture2D(	u_texture0, 	vec2(uv.x + xOffset -0.01*rgbOffsetOpt,y)).r+staticVal;
    float green = 	texture2D(	u_texture0, 	vec2(uv.x + xOffset,	  y)).g+staticVal;
    float blue 	=	texture2D(	u_texture0, 	vec2(uv.x + xOffset +0.01*rgbOffsetOpt,y)).b+staticVal;

    vec3 color = vec3(red,green,blue);
    float scanline = sin(uv.y*800.0)*0.04*scalinesOpt;
    color -= scanline;



    gl_FragColor = vec4(color,1.0);
}
