#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)

rs_allocation img1;
rs_allocation img2;

void root(const float4* v_in,float4* v_out, uint32_t x, uint32_t y)
{
    float val1 = rsGetElementAt_float4(img1,x,y).S0;
    float val2 = rsGetElementAt_float4(img2,x,y).S0;
    float val3 = sqrt(val1*val1 + val2*val2);
    (*v_out) = (float4){val3,val3,val3,1.0f};

}