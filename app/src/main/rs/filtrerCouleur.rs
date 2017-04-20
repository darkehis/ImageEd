#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)

int teinte;
int tolerance;

//Script de filtrage d'un teite avec une certaine tolerance.

void root(const uchar4* in,uchar4* out, uint32_t x, uint32_t y)
{

float4 coul = {0.0f,0.0f,0.0f,0.0f};
    coul = rsUnpackColor8888(*in);

    float cMax = max(coul.r,coul.g);
    cMax = max(cMax,coul.b);
    int ind = floor(cMax*255);
    float v = cMax;
    float cMin = min(coul.r,coul.g);
    cMin = min(cMin,coul.b);
    float d = cMax - cMin;
    float h = 0;
    int quotient;

    if(d != 0)
    {

        if(cMax == coul.r)
        {
            h = (coul.g-coul.b)/d;
            //rsDebug("R",h);


        }
        else if(cMax == coul.g)
        {

            h = (coul.b-coul.r)/d+2;
            //rsDebug("G",h);
        }

        else if (cMax == coul.b)
        {
            h =(coul.r-coul.g)/d + 4;
            //rsDebug("B",h);
        }

        h*=60;
        if(h<0)
            h+=360;

    }


    float dh = fabs(h-teinte);

    if(dh < tolerance || dh > 360-tolerance)
    {
        *out = *in;
    }
    else
    {

        float g = 0.3*coul.r + 0.59*coul.g + 0.11*coul.b;
        float4 nouvCoul = {g,g,g,1.0f};

        *out = rsPackColorTo8888(nouvCoul);

    }


}
