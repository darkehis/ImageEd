#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)

float seuil;

void root(const uchar4* v_in,uchar4* v_out, uint32_t x, uint32_t y)
{
    float4 coul = rsUnpackColor8888(*v_in);
    if(coul.S0>seuil)
    {
        coul = (float4){1.0f,1.0f,1.0f,1.0f};
    }
    else
    {

        coul = (float4){0.0f,0.0f,0.0f,1.0f};
    }

    (*v_out ) =  rsPackColorTo8888(coul);

}



