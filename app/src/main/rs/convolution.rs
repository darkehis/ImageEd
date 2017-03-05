#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)



float matrice[];
rs_allocation matrice2;
int taille;
int dim;
int centre;
float total;

int w_img;
int h_img;
rs_allocation img;

void root(const uchar4* v_in, uchar4* v_out, uint32_t x, uint32_t y)
{
    float4 coul = {0.0f,0.0f,0.0f,0.0f};
    float4 coul2 = {0.0f,0.0f,0.0f,0.0f};

    if(x>centre && x <w_img-centre && y > centre && y < h_img-centre)
    {
        for(int i =0;i<taille;i++)
        {
                coul += rsGetElementAt_float(matrice2,i)*rsUnpackColor8888(rsGetElementAt_uchar4(img,x+(i%dim)-centre,y+(i/dim) - centre));

        }
    }
    else
    {
        coul = rsUnpackColor8888(*v_in);
    }


    coul.a = 255;
    //coul = rsUnpackColor8888(*v_in);
    *v_out = rsPackColorTo8888(coul);

}