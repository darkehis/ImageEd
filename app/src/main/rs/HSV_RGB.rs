#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)


float4 static HSVtoRGB(float4 coul);

float4 static RGBtoHSV(float4 coul);


void toHSV(const uchar4* in,float4* out,uint32_t x,uint32_t y)
{
    float4 coul = {0.0f,0.0f,0.0f,0.0f};
    coul  = rsUnpackColor8888(*in);
    (*out) = RGBtoHSV(coul);
}

void toRGB(const float4* in,uchar4* out,uint32_t x,uint32_t y)
{
    float4 coul = {0.0f,0.0f,0.0f,0.0f};
    coul  = *in;
    coul = HSVtoRGB(coul);
    *out = rsPackColorTo8888(coul);
}

float4 static RGBtoHSV(float4 coul)
{
    float cMax = max(coul.r,coul.g);
    cMax = max(cMax,coul.b);
    float v = cMax;

    float cMin = min(coul.r,coul.g);
    cMin = min(cMin,coul.b);

    float d = cMax - cMin;
    float h = 0;


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



     float s = 0;
         if(cMax != 0)
         {
             s = d/cMax;
         }


    return (float4){h,s,v,1.0f};

}



float4 static HSVtoRGB(float4 coul)
{

    float r,g,b;
    float hh = coul.S0/60;
    int hE = floor(hh);
    float hD = hh- hE;

    float p = coul.S2*(1.0-coul.S1);
    float q = coul.S2*(1.0-coul.S1*hD);
    float t = coul.S2*(1.0- coul.S1*(1-hD));

    switch(hE)
    {
        case 0:
        r = coul.S2;
        g = t;
        b = p;
        break;

        case 1:
        r = q;
        g = coul.S2;
        b = p;
        break;

        case 2:
        r = p;
        g = coul.S2;
        b = t;
        break;

        case 3:
        r = p;
        g = q;
        b = coul.S2;
        break;

        case 4:
        r = t;
        g = p;
        b = coul.S2;

        break;

        case 5:
        r = coul.S2;
        g = p;
        b = q;



        break;
    }


    return (float4){r,g,b,1.0f};




}



