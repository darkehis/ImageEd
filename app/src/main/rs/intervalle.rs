#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)



//On ne gere que les tableaux de niveaix de gris
float minV;
float maxV;
float maxN;
float minN;
int initialise;

//les différente fonction d'extension linéaire sur chacune des coposant h s et v, normalisées et non normalisées
void interNo(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = ((*in).S0 - minV)/(maxV-minV);

    (*out) = (float4){val,val,val,1.0f};
}

void interNo0(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = ((*in).S0 - minV)/(maxV-minV);

    (*out) = (*in);
    (*out).S0 = val;
}

void interNo1(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = ((*in).S1 - minV)/(maxV-minV);

    (*out) = (*in);
    (*out).S1 = val;
}

void interNo2(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = ((*in).S2 - minV)/(maxV-minV);

    (*out) = (*in);
    (*out).S2 = val;
}

void inter(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = ((*in).S0 - minV)/(maxV-minV)*(maxN-minN) + minN;

    (*out) = (float4){val,val,val,1.0f};
}

void inter0(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = ((*in).S0 - minV)/(maxV-minV)*(maxN-minN) + minN;

    (*out) = (*in);
    (*out).S0 = val;
}

void inter1(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = ((*in).S1 - minV)/(maxV-minV)*(maxN-minN) + minN;

    (*out) = (*in);
    (*out).S1 = val;
}

void inter2(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = ((*in).S2 - minV)/(maxV-minV)*(maxN-minN) + minN;

    (*out) = (*in);
    (*out).S2 = val;
}


//Calcul des min et max pour chacune des différents composantes

void calculerMinMax0(const float4* in,float4* out,uint32_t x,uint32_t y)
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




void calculerMinMax1(const float4* in,float4* out,uint32_t x,uint32_t y)
{
    if(initialise == 0)
    {
        maxV = (*in).S1;
        minV = (*in).S1;
        initialise = 1;
    }
    else
    {
        if((*in).S1> maxV)
        {
            maxV = (*in).S1;
        }
        if((*in).S1<minV)
        {
            minV = (*in).S1;
        }
    }
}

void calculerMinMax2(const float4* in,float4* out,uint32_t x,uint32_t y)
{
    if(initialise == 0)
    {
        maxV = (*in).S2;
        minV = (*in).S2;
        initialise = 1;
    }
    else
    {
        if((*in).S2> maxV)
        {
            maxV = (*in).S2;
        }
        if((*in).S2<minV)
        {
            minV = (*in).S2;
        }
    }
}


void borner0(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = (*in).S0;
    val = max(min(val,maxN),minN);

    (*out) = (*in);

    (*out).S0  = val;

}

void borner1(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = (*in).S1;
    val = max(min(val,maxN),minN);

    (*out) = (*in);

    (*out).S1  = val;



}


void borner2(const float4* in,float4* out, uint32_t x,uint32_t y)
{
    float val = (*in).S2;
    val = max(min(val,maxN),minN);

    (*out) = (*in);

    (*out).S2  = val;

}

//a voir

void toBmp(const float4* in,uchar4* out,uint32_t x,uint32_t y)
{
    (*out) = rsPackColorTo8888(*in);
}


