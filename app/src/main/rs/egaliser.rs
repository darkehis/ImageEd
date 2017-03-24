#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)


rs_allocation histo;
rs_allocation cumul;
int taille_image;



void calculHisto(const uchar4* in,uchar4* out,uint32_t x,uint32_t y)
{
    float4 coul = {0.0f,0.0f,0.0f,0.0f};
    coul = rsUnpackColor8888(*in);
    float lum = fmax(coul.r,coul.g);
    lum = fmax(lum,coul.b);
    int ind = floor(lum*255);
    double val = rsGetElementAt_double(histo,ind);
    rsSetElementAt_double(histo,val+1,ind);



}


void egaliser(const uchar4* in,uchar4* out,uint32_t x,uint32_t y)
{

//revoir la conversion de RGB ver HSV


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


    float nV = (float)(rsGetElementAt_double(cumul,ind))/(float)taille_image;
    nV = v;



    float r,g,b;
    float hh = h/60;
    int hE = floor(hh);
    float hD = hh- hE;

    float p = nV*(1.0-s);
    float q = nV*(1.0-s*hD);
    float t = nV*(1.0- s*(1-hD));

    switch(hE)
    {
        case 0:
        r = v;
        g = t;
        b = p;
        break;

        case 1:
        r = q;
        g = v;
        b = p;
        break;

        case 2:
        r = p;
        g = v;
        b = t;
        break;

        case 3:
        r = p;
        g = q;
        b = v;
        break;

        case 4:
        r = t;
        g = p;
        b = v;

        break;

        case 5:
        r = v;
        g = p;
        b = q;



        break;
    }


    float4 nouvCoul = {r,g,b,1.0f};

    *out = rsPackColorTo8888(nouvCoul);

}