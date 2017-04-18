#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)



//On ne gere que les tableaux de niveaix de gris
float minV;
float maxV;
float maxN;
float minN;
int initialise;


void root(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = ((*in).S0 - minV)/(maxV-minV);

    (*out) = (float4){val,val,val,1.0f};


}



void calculerMinMax(const float4* in,float4* out,uint32_t x,uint32_t y)
{
    if(initialise == 0)
    {
        maxV = (*in).S0;
        minV = (*in).S0;
        initialise = 1;
    }
    else
    {
        if((*in).S0> maxV)
        {
            maxV = (*in).S0;
        }
        if((*in).S0<minV)
        {
            minV = (*in).S0;
        }
    }
}


void toBmp(const float4* in,uchar4* out,uint32_t x,uint32_t y)
{

    (*out) = rsPackColorTo8888(*in);
}


