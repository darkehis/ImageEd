#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)


float comp;


void root(const uchar4* in,uchar4* out, uint32_t x, uint32_t y)
{
    float4 coul  = rsUnpackColor8888(*in);
    coul.S0 = fmin(fmax(coul.S0+comp,0),1);
    coul.S1 = fmin(fmax(coul.S1+comp,0),1);
    coul.S2 = fmin(fmax(coul.S2+comp,0),1);
    coul.S3 = 1.0f;
    (*out) = rsPackColorTo8888(coul);
}