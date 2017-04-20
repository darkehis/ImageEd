#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)



//A faire retenir les nouvelle valeur dans une allocation puis reechelonner de facon sympas

rs_allocation matrice2;


int taille;
int dim;
int centre;
float total;

int w_img;
int h_img;
rs_allocation img;


void root(const uchar4* v_in,float4* v_out, uint32_t x, uint32_t y)
{
    float4 coul = {0.0f,0.0f,0.0f,0.0f};
    int mX;
    int mY;

    //si le pixel est assez loin du bord de l'image( non concerné par les effets de bord)
    if(x>centre && x <w_img-centre && y > centre && y < h_img-centre)
    {
        for(int i =0;i<taille;i++)
        {
                coul += rsGetElementAt_float(matrice2,i)*rsUnpackColor8888(rsGetElementAt_uchar4(img,x+(i%dim)-centre,y+(i/dim) - centre));
        }

        *v_out = coul;
    }
    //Si le pixel est trop prooche du bord on applique la méthode "mirroir"
    //Pixel ur le bord gauche
    else if(x<=centre && x <w_img-centre && y > centre && y < h_img-centre)
    {

        for(int i =0;i<taille;i++)
        {
            mX = (int)(i%dim);
            if(mX<centre)
                mX +=centre;
            coul += rsGetElementAt_float(matrice2,i)*rsUnpackColor8888(rsGetElementAt_uchar4(img,x+ mX -centre,y+(i/dim) - centre));
        }

        *v_out = coul;

    }
    //sur le bord droit
    else if(x>centre && x >=w_img-centre && y > centre && y < h_img-centre)
    {
        for(int i =0;i<taille;i++)
        {
            mX = (int)(i%dim);
            if(mX>centre)
                mX -=centre;
            coul += rsGetElementAt_float(matrice2,i)*rsUnpackColor8888(rsGetElementAt_uchar4(img,x+ mX -centre,y+(i/dim) - centre));
        }

        *v_out = coul;


    }
    //sur le bord supérieur
    else if(x>centre && x <w_img-centre && y <= centre && y < h_img-centre)
    {

         for(int i =0;i<taille;i++)
         {
             mY = (int)(i/dim);
             if(mY<centre)
                 mY +=centre;
             coul += rsGetElementAt_float(matrice2,i)*rsUnpackColor8888(rsGetElementAt_uchar4(img,x+(i%dim)-centre,y+mY - centre));
         }

         *v_out = coul;



    }
    //sur le bord inférieur
    else if(x>centre && x <w_img-centre && y > centre && y >= h_img-centre)
    {
        for(int i =0;i<taille;i++)
        {
             mY = (int)(i/dim);
             if(mY>centre)
                 mY -=centre;
             coul += rsGetElementAt_float(matrice2,i)*rsUnpackColor8888(rsGetElementAt_uchar4(img,x+(i%dim)-centre,y+mY - centre));
        }

        *v_out = coul;

    }
    //sur le bord en haut à gauche
    else if(x<=centre && x <w_img-centre && y <= centre && y < h_img-centre)
    {
        for(int i =0;i<taille;i++)
        {
             mY = (int)(i/dim);
             mX = (int)(i%dim);
             if(mY<centre)
                 mY +=centre;
             if(mX<centre)
                 mX+=centre;
             coul += rsGetElementAt_float(matrice2,i)*rsUnpackColor8888(rsGetElementAt_uchar4(img,x+mX-centre,y+mY - centre));
        }

        *v_out = coul;

    }
    //sur le bord en haut à droite
    else if(x>centre && x >=w_img-centre && y <= centre && y < h_img-centre)
    {
        for(int i =0;i<taille;i++)
            {
                 mY = (int)(i/dim);
                 mX = (int)(i%dim);
                 if(mY<centre)
                     mY +=centre;
                 if(mX>centre)
                     mX-=centre;
                 coul += rsGetElementAt_float(matrice2,i)*rsUnpackColor8888(rsGetElementAt_uchar4(img,x+mX-centre,y+mY - centre));
            }

            *v_out = coul;

    }
    //sur le bord en bas à gauche
    else if(x <=centre && x <w_img-centre && y > centre && y >= h_img-centre)
    {
        for(int i =0;i<taille;i++)
        {
             mY = (int)(i/dim);
             mX = (int)(i%dim);
             if(mY>centre)
                 mY -=centre;
             if(mX<centre)
                 mX+=centre;
             coul += rsGetElementAt_float(matrice2,i)*rsUnpackColor8888(rsGetElementAt_uchar4(img,x+mX-centre,y+mY - centre));
        }

        *v_out = coul;



    }
    //sur le bord en bas à droite
    else if(x>centre && x <w_img-centre && y > centre && y < h_img-centre)
    {
        for(int i =0;i<taille;i++)
        {
             mY = (int)(i/dim);
             mX = (int)(i%dim);
             if(mY>centre)
                 mY -=centre;
             if(mX>centre)
                 mX-=centre;
             coul += rsGetElementAt_float(matrice2,i)*rsUnpackColor8888(rsGetElementAt_uchar4(img,x+mX-centre,y+mY - centre));
        }

        *v_out = coul;

    }
    else
    {
        *v_out = rsUnpackColor8888(*v_in);
    }

}

