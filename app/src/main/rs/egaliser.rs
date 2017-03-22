#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)


rs_allocation histo;
rs_allocation cumul;
int taille_image;



void calculHisto(const uchar4* in,uchar4* out,uint32_t x,uint32_t y)
{
    //avant tout il faut convertir les couleur rgb en couleur hsv
    float4 coul = {0.0f,0.0f,0.0f,0.0f};
    coul = rsUnpackColor8888(*in);
    int lum = max((int)coul.r,(int)coul.g);
    lum = max(lum,(int)coul.b);
    int val = rsGetElementAt_int(histo,lum);
}


void egaliser(const uchar4* in,uchar4* out,uint32_t x,uint32_t y)
{

//revoir la conversion de RGB ver HSV


    float4 coul = {0.0f,0.0f,0.0f,0.0f};
    coul = rsUnpackColor8888(*in);

    float cMax = fmax(coul.r,coul.g);
    cMax = fmax(cMax,coul.b);
    int lum = (int)cMax;
    float v = ((float)(lum))/((float)(255));
    float cMin = fmin(coul.r,coul.g);
    cMin = fmin(cMin,coul.b);
    float d = cMax - cMin;
    float h = 0;


    if(cMax != 0)
    {

        if(cMax == coul.r)
        {
            h = 60*fmod(floor((float)(coul.g-coul.b)/(float)d),6);


        }
        else if(cMax == coul.g)
        {

            h = 60*(floor((float)(coul.b-coul.r)/(float)d)+2);
        }

        else if (cMax == coul.b)
        {
            h = 60*(floor((float)(coul.r-coul.g)/(float)d)+4);


        }


    }

    float s = 0;
    if(cMax != 0)
    {
        s = ((float)d)/((float)cMax);
    }

    rsGetElementAt(cumul,lum);


    int val = rsGetElementAt(histo,lum);


    int nouvLum = floor(((float)(rsGetElementAt_int(cumul,val)))/taille_image);
    int nV = ((float)nouvLum)/((float)255);

    //recreation de la couleur rgb;


    float r,g,b;
    float c = v*s;
    float m = v-c;

    float X = c*(1 - fabs(fmod(h/60,2)-1));

    int rapport = floor(h/60);
    switch(rapport)
    {
        case 0:
        r = c;
        g = X;
        b = 0;
        break;

        case 1:
        r = X;
        g = c;
        b = 0;
        break;

        case 2:
        r = 0;
        g = c;
        b = x;
        break;

        case 3:
        r = 0;
        g = X;
        b = c;
        break;

        case 4:
        r = X;
        g = 0;
        b = c;

        break;

        case 5:
        r = c;
        g = 0;
        b = X;

        break;
    }

    float4 nouvCoul = {floor((r + m)*255),floor((g+m)*255),floor((b+m)*255),255};

    *out = rsPackColorTo8888(nouvCoul);

}