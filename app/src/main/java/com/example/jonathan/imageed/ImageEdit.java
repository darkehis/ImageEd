package com.example.jonathan.imageed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.util.Log;


import java.lang.annotation.ElementType;
import java.security.AccessController;


//TODO refaire le center lors du zoom
//TODO convolution : niels


import static android.graphics.Bitmap.createBitmap;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.renderscript.Allocation.createSized;

/**
 * Created by Jonathan on 20/01/2017.
 *
 * Classe contenant les fonctions de modification du bitmap.
 *
 *
 */



public class ImageEdit {


    /**
     *Fonction Java de grisage de l'image: maintenant inutilisé.
     *
     * @param bmp le Bitmap à modifier.
     * @return
     */

    public static Bitmap griser(Bitmap bmp)
    {
        int coul,r,g,b,gr;
        int[] pix = new int[bmp.getWidth()*bmp.getHeight()];
        Bitmap bmp2 = bmp.copy(bmp.getConfig(),true);
        bmp.getPixels(pix,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

        //on boucle sur l'array de pixels
        for(int i =0;i<pix.length;i++)
        {
            coul = pix[i];
            r = Color.red(coul);
            g = Color.green(coul);
            b = Color.blue(coul);
            gr = (int)(0.3*r + 0.59*g + 0.11*b);
            pix[i] = Color.argb(255,gr,gr,gr);
        }
        bmp2.setPixels(pix,0,bmp2.getWidth(),0,0,bmp2.getWidth(),bmp2.getHeight());

        return bmp2;

    }

    /**  Fonction faisant appel à un renderscript pour le grisage du bitmap source.
     *
     * @param bmp le bitmap à modifier
     * @param context le context de l'aplication
     * @return le bitmap modifié
     */

    public static Bitmap griserScr(Bitmap bmp, Context context)
    {

        //Le nouveau bitmap: le result
        Bitmap bmp2 = bmp.copy(bmp.getConfig(),true);


        //création de l'acces  au contexte renderscript.
        RenderScript RS = RenderScript.create(context);

        //l'allocation du bitmap à modifier
        Allocation allocIn;
        allocIn = Allocation.createFromBitmap(RS, bmp, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);


        //l'allocation du bitmap resultat.
        Allocation allocOut = Allocation.createTyped(RS, allocIn.getType());

        //L'objet du script de grisage.
        ScriptC_grey script = new ScriptC_grey(RS);

        //l'application du script à chaque pixel.
        script.forEach_root(allocIn,allocOut);



        allocOut.copyTo(bmp2);
        return bmp2;

    }



    /**
     * Fonction de zoom de l'image
     *
     * @param bmpPrec le bitmap precedent
     * @param bmpOri le bitmap d'origine
     * @param w la largeur du nouveau bitmap (la taille d'affichage de MonImage)
     * @param h la hauteur du nouveau bitmap (la taille d'affichage de MonImage)
     * @param inter l'intersection de l'ancien et du nouveau rectangle si ceux-ci sont de même taille: evite de tout rezoomer
     * @param context le context de l'application
     * @return la bitmap modifié
     */

    public static Bitmap zoomScr(Bitmap bmpPrec,Bitmap bmpOri, int w, int h, Rect inter ,Context context)
    {

        Log.i("rectangle","on zoom vindieu");
        //Déclaration du bitmap résultat
        Bitmap bmp2 = Bitmap.createBitmap(w,h,bmpOri.getConfig());

        if(inter.left != -2)
        {
            bmp2 = bmpPrec.copy(bmpPrec.getConfig(),true);
        }

        float zoom = (float) (w)/(float)(bmpOri.getWidth());



        RenderScript RS = RenderScript.create(context);
        Allocation allocIn;
        allocIn = Allocation.createFromBitmap(RS, bmp2, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        Allocation allocOut = Allocation.createTyped(RS, allocIn.getType());

        ScriptC_zoom script = new ScriptC_zoom(RS);


        Allocation origine = Allocation.createFromBitmap(RS,bmpOri, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);


        Log.i("zoom",""+ zoom);

        //affectation des differents paramètres du script
        script.set_origin(origine);
        script.set_h_origin(bmpOri.getHeight());
        script.set_w_origin(bmpOri.getWidth());
        script.set_left(inter.left);
        script.set_top(inter.top);
        script.set_right(inter.right);
        script.set_bottom(inter.bottom);
        script.set_zoom(zoom);


        //Application u script à chaque pixel.


        script.forEach_root(allocIn,allocOut);
        allocOut.copyTo(bmp2);

        return bmp2;


    }



    //devenu useless


    public static Bitmap extensionContraste(Bitmap bmp)
    {
        /*int coul;
        float lum;
        int[] lut = new int[256];
        float[] hsv = new float[3];
        float maxV,minV;
        float[] listLum = new float[bmp.getWidth()*bmp.getHeight()];
        int[] pix = new int[bmp.getWidth()*bmp.getHeight()];
        Bitmap bmp2 = bmp.copy(bmp.getConfig(),true);
        bmp.getPixels(pix,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

        for(int i = 0;i<pix.length;i++)
        {
            coul =  pix[i];
            Color.colorToHSV(coul,hsv);
            listLum[i] = hsv[2]*255;

        }
        //Calcul des valeurs min, max
        maxV = minV = listLum[0];
        for(int i = 0;i<listLum.length;i++)
        {
            if(listLum[i]>maxV)
                maxV = listLum[i];
            if(listLum[i]<minV)
                minV = listLum[i];
        }

        Log.i("contraste",Float.toString(minV) + "," + Float.toString(maxV));

        for(int i =0;i<256;i++)
        {
            lut[i] = i;
            float h = 255*((lut[i]-minV)/(maxV-minV));
            lut[i]  = Math.round(h);
            Log.i("contraste",Integer.toString(i) + ":" + Float.toString(lut[i]));
        }
        for(int i = 0;i<pix.length;i++)
        {
            coul = pix[i];
            Color.colorToHSV(coul,hsv);
            lum = lut[(int)Math.floor(hsv[2]*255)];
            hsv[2] = lum/255;
            pix[i] = Color.HSVToColor(255,hsv);
        }
        bmp2.setPixels(pix,0,bmp2.getWidth(),0,0,bmp2.getWidth(),bmp2.getHeight());

        return bmp2;*/
        return changerContraste(bmp,0,255);

    }


    //Devenue aussi useless

    public static Bitmap diminutionContraste(Bitmap bmp)
    {
        int coul,r,g,b,gr;
        float[] hsv = new float[3];
        float maxV,minV;
        float[] lut = new float[256];
        int debLum = 12;
        int finLum = 150;
        float moyGris = 0;
        float[] listLum = new float[bmp.getWidth()*bmp.getHeight()];
        int[] pix = new int[bmp.getWidth()*bmp.getHeight()];
        Bitmap bmp2 = bmp.copy(bmp.getConfig(),true);
        bmp.getPixels(pix,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        //on boucle sur l'array de pixels et on les grises
        for(int i =0;i<pix.length;i++)
        {
            coul =  pix[i];
            Color.colorToHSV(coul,hsv);
            listLum[i] = hsv[2]*255;
        }

        //Calcul des valeurs min, max et moyenne
        maxV = minV = listLum[0];
        for(int i = 0;i<listLum.length;i++)
        {
            moyGris += listLum[i];
            if(listLum[i]>maxV)
                maxV = listLum[i];
            if(listLum[i]<minV)
                minV = listLum[i];
        }
        //TODO: ici varianle unused: a voir pour supprimer
        moyGris = moyGris/listLum.length;
        for(int i =0;i<256;i++)
        {
            lut[i] = i;
            float h = (finLum-debLum)*((lut[i]-minV)/(maxV-minV)) + debLum;
            lut[i]  = Math.round(h);
        }
        for(int i = 0;i<pix.length;i++)
        {
            coul = pix[i];
            Color.colorToHSV(coul,hsv);
            hsv[2] = lut[(int)Math.floor(hsv[2]*255)]/255;
            pix[i] = Color.HSVToColor(255,hsv);
        }
        bmp2.setPixels(pix,0,bmp2.getWidth(),0,0,bmp2.getWidth(),bmp2.getHeight());

        return bmp2;

    }


    //TODO: à passer en RS: changer contraste
    /**
     * Fonction de modification du contraste
     *
     * @param bmp la bitmap à modifier
     * @param min le minimum de constrate demande
     * @param max le maximum demandé
     * @return la bitmap modifié
     */

    public static Bitmap changerContraste(Bitmap bmp,int min,int max)
    {
        float[] hsv = new float[3];
        int coul;
        float maxV,minV;
        float[] lut = new float[256];
        int debLum = min;
        int finLum = max;
        float[] listLum = new float[bmp.getWidth()*bmp.getHeight()];
        int[] pix = new int[bmp.getWidth()*bmp.getHeight()];
        Bitmap bmp2 = bmp.copy(bmp.getConfig(),true);
        bmp.getPixels(pix,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        //on boucle sur l'array de pixels et on les grises
        for(int i =0;i<pix.length;i++)
        {
            coul =  pix[i];
            Color.colorToHSV(coul,hsv);
            listLum[i] = hsv[2]*255;
        }

        //Calcul des valeurs min, max et moyenne
        maxV = minV = listLum[0];
        for(int i = 0;i<listLum.length;i++)
        {

            if(listLum[i]>maxV)
                maxV = listLum[i];
            if(listLum[i]<minV)
                minV = listLum[i];
        }

        for(int i =0;i<256;i++)
        {
            lut[i] = i;
            lut[i] = (int)((finLum-debLum)*((lut[i]-minV)/(maxV-minV)) + debLum);
        }
        for(int i = 0;i<pix.length;i++)
        {
            coul = pix[i];
            Color.colorToHSV(coul,hsv);
            hsv[2] = lut[(int)Math.floor(hsv[2]*255)]/255;
            pix[i] = Color.HSVToColor(255,hsv);
        }
        bmp2.setPixels(pix,0,bmp2.getWidth(),0,0,bmp2.getWidth(),bmp2.getHeight());

        return bmp2;

    }



    //TODO: changer en RS: égaliser

    /**
     * Fonction d'égalisation de l'histogramme du bitmap à modifier
     *
     * @param bmp le Bitmap à modifier
     * @return Le bitmap modifié
     */
    public static Bitmap egaliser(Bitmap bmp)
    {

        int coul;

        //TODO: variables useless: à voir pour supprimer
        float[] hsv = new float[3];
        float[] listLum = new float[bmp.getWidth()*bmp.getHeight()];
        int[] pix = new int[bmp.getWidth()*bmp.getHeight()];
        Bitmap bmp2 = bmp.copy(bmp.getConfig(),true);
        bmp.getPixels(pix,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

        float[] histo = new float[256];
        float[] cumul = new float[256];

        for(int i =0;i<pix.length;i++)
        {
            coul = pix[i];
            Color.colorToHSV(coul,hsv);
            listLum[i] = hsv[2]*255;
            histo[(int)Math.floor(listLum[i])]++;
        }

        //Calcul de l'histrogramme cumulé
        cumul[0] = histo[0];
        for(int i = 1;i< histo.length;i++)
        {
            cumul[i] = cumul[i-1] + histo[i];
        }


        for(int i =0;i<pix.length;i++)
        {
            coul = pix[i];
            Color.colorToHSV(coul,hsv);
            hsv[2] = ((cumul[(int)Math.floor(hsv[2]*255)]*255)/pix.length)/255;
            pix[i] = Color.HSVToColor(255,hsv);
        }

        bmp2.setPixels(pix,0,bmp2.getWidth(),0,0,bmp2.getWidth(),bmp2.getHeight());

        return  bmp2;

    }

    /**Fonction d'appel du script d'égalisation de constraste: A TESTER
     *
     * @param bmp le bitmap à modifier
     * @param context
     * @return
     */

    public static Bitmap egaliserSrc(Bitmap bmp,Context context)
    {
        Bitmap bmp2 = bmp.copy(bmp.getConfig(),true);


        //Classe d'acces à la couche renderscript.
        RenderScript RS = RenderScript.create(context);

        //Creéation du cript.
        ScriptC_egaliser script = new ScriptC_egaliser(RS);


        //Allocation correspondante au bitmap de départ.
        Allocation allocIn;
        allocIn = Allocation.createFromBitmap(RS, bmp2, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);


        //Allocation correspondante au bitmap resultat
        Allocation allocOut = Allocation.createTyped(RS, allocIn.getType());

        //l'histogram et l'histogram cumulé
        double[] histo = new double[256];
        double[] cumul = new double[256];



        //création de l'histogram;
        Allocation histoAll = Allocation.createSized(RS,Element.F64(RS),256);
        histoAll.copy1DRangeFrom(0,256,new double[256]);


        script.set_histo(histoAll);
        script.set_taille_image(bmp.getWidth()*bmp.getHeight());

        script.forEach_calculHisto(allocIn,allocOut);


        //récupération de l'histogram.


        //histoAll.copyTo(histo);
        histoAll.copyTo(histo);


        //calcul de l'histogramme  cumulé.
        cumul[0] = histo[0];
        for(int i = 1;i< histo.length;i++)
        {
            cumul[i] = cumul[i-1] + histo[i];
        }

        Allocation cumulAll = Allocation.createSized(RS,Element.F64(RS),256);

        cumulAll.copy1DRangeFrom(0,256,cumul);

        script.set_cumul(cumulAll);

        script.forEach_egaliser(allocIn,allocOut);


        Log.i("ega","termine");
        allocOut.copyTo(bmp2);

        return bmp2;

    }


    /**Fonction d'application d'une matrice de convolution
     *
     * @param bmp
     * @param matrix
     * @param context
     * @return l'image à laquelle est appliquée la matrice de convolution.
     */


    public static Bitmap convolutionScr(Bitmap bmp, float[][] matrix, Context context)
    {
        //créatioj du bitmap resultat
        Bitmap bmp2 = bmp.copy(bmp.getConfig(),true);




        //taille de la matrice
        int dim = matrix.length;
        //nb de coeff de la matrice.
        int taille = dim*dim;
        //coordonnées du coeficient centrale de la matrice.
        int centre = dim/2;

        //calcul de la somme des coeffiecients, renvoie -1 si un coeff est négatif
        float total = calculTotal(matrix);


        //Transformation de la matrice en tableau de 1 dimension.
        //et normalisation si possible
        float matrix1D[] = new float[dim*dim];

        for(int i =0;i<dim;i++)
        {
            for(int j =0;j<dim;j++)
            {
                matrix1D[(i*dim)+j] = matrix[i][j];
                if(total != -1)
                {
                    matrix[i][j] = matrix[i][j]/total;
                }
            }
        }



        //on travail sur une image en niveaux de gris si le noyau contient des négatifs.
        if(total == -1)
        {
            bmp2 = ImageEdit.griserScr(bmp2,context);
        }


        //Classe d'acces à la couche renderscript.
        RenderScript RS = RenderScript.create(context);

        //Allocation correspondante au bitmap de départ.
        Allocation allocIn;
        allocIn = Allocation.createFromBitmap(RS, bmp2, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        //Allocation intermédiaire au cas ou il faille changer l'intervalle des différents coefficients.
        Allocation allocInter = Allocation.createTyped(RS, Type.createXY(RS,Element.F32_4(RS),bmp2.getWidth(),bmp2.getHeight()));

        //Allocation correspondante au bitmap resultat
        Allocation allocOut = Allocation.createTyped(RS, allocIn.getType());








        ScriptC_convolution script = new ScriptC_convolution(RS);

        //variables dont on a besoin pour faire tourner le script.
        Allocation img = Allocation.createFromBitmap(RS,bmp, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);


        //Allocation correspondante à la matrice noyau.
        Allocation matAll = Allocation.createSized(RS,Element.F32(RS),taille);
        matAll.copy1DRangeFrom(0,taille,matrix1D);

        script.set_matrice2(matAll);

        script.set_img(img);
        script.set_dim(dim);
        script.set_centre(centre);
        script.set_w_img(bmp2.getWidth());
        script.set_h_img(bmp2.getHeight());
        script.set_taille(taille);
        script.set_total(total);


        script.forEach_root(allocIn,allocInter);
        Log.i("convol","ok3.1");
        Log.i("convol","ok3.2");
        Log.i("convol","ok4");


        ScriptC_intervalle scriptInter =  new ScriptC_intervalle(RS);

       Log.i("convol","ok4.1");
        if(total == -1)
        {
            scriptInter.set_initialise(0);
            scriptInter.forEach_calculerMinMax(allocInter,allocInter);
            scriptInter.forEach_root(allocInter,allocInter);
            Log.i("convol","ok4.4");
        }

        Log.i("convol","ok5");
        scriptInter.forEach_toBmp(allocInter,allocOut);
        allocOut.copyTo(bmp2);


        return bmp2;

    }




    //Fonction de zoom en java: devenue useless

    public static Bitmap zoom(Bitmap bSrc, int w, int h)
    {

        //création de l'image zoomée
        Bitmap bmp2 = createBitmap(w,h,bSrc.getConfig());

        //tableau de l'image zoomée
        int[] pix2 = new int[w*h];

        //Log.i("zoom","check 1");
        //récupération du tableau de pixel précédent
        int[] pix = new int[bSrc.getWidth() * bSrc.getHeight()];
        bSrc.getPixels(pix,0,bSrc.getWidth(),0,0,bSrc.getWidth(),bSrc.getHeight());

        //Log.i("zoom","check 2");
        int wSrc = bSrc.getWidth();
        double zoom = (double)w/(double)wSrc;


        //variables
        //les coordonnées du pixel en cours, puis les coordonnés dans l'ancienne image des pixels utilisée pour la création d'un nouveau pixel
        int x,y,x1,x2,x3,x4,y1,y2,y3,y4;


        //la distance du pixel en cours au pixel de base en haut à gauche de celui ci
        double dx,dy,xD,yD;


        //coord des derniers pixels de l'ancienne image directement copié
        //on boucle sur le nouveau tableau de pixels
        for(int i =0;i<pix2.length;i++)
        {
            //calcul des coordonnées du pixel à creer
            x = i%w;
            y = i/w;

            xD = (double)(x)/zoom;
            yD = (double)(y)/zoom;


            //récuperation des 4 pixels nécessaire pour le calcul de la moyenne
            //variables superfétatoires: uniquement pour la lisibilité
            x1 = (int)(xD);
            x2 = x1+1;
            x3 = x1;
            x4 = x1+1;

            y1  = (int)(yD);

            y2 = y1+1;
            y3 = y1;
            y4 = y1+1;

            dx = xD - x1;
            dy = yD - y1;


            //zoom avec interpolation
            //on verifie qu'on ne sort pas de l'image

            //Log.i("zoom","check 4 : (" + x1 + "," + y1 + "),("+ x2 + "," + y2 + "),("+ x3 + "," + y3 + "),("+ x4 + "," + y4 + ")" ) ;
            if((wSrc*y4 + x4)<pix.length)
            {
                pix2[i] = interpol(pix[wSrc*y1 + x1],pix[wSrc*y2 + x2],pix[wSrc*y3 + x3],pix[wSrc*y4 + x4],dx,dy,zoom);
            }


        }
        //Log.i("zoom","check 5");
        bmp2.setPixels(pix2,0,w,0,0,w,h);

        return bmp2;

    }


    //Devenu aussi useless
    public static int interpol(int p1,int p2,int p3,int p4,double dx, double dy, double fZoom)
    {


        int p;
        //distance à chaque pixel
        double d1,d2;

        int r,g,b;

        d1 = dx/fZoom;
        d2 = dy/fZoom;


        r = (int) (((1-d1)*(1-d2)*Color.red(p1) + (1-d1)*(d2)*Color.red(p3) + (1-d2)*(d1)*Color.red(p2) + (d1)*(d2)*Color.red(p4)));
        g = (int) (((1-d1)*(1-d2)*Color.green(p1) + (1-d1)*(d2)*Color.green(p3) + (1-d2)*(d1)*Color.green(p2) + (d1)*(d2)*Color.green(p4)));
        b = (int) (((1-d1)*(1-d2)*Color.blue(p1) + (1-d1)*(d2)*Color.blue(p3) + (1-d2)*(d1)*Color.blue(p2) + (d1)*(d2)*Color.blue(p4)));
        p = Color.argb(255,r,g,b);

        return p;

    }


    public static float[][] getMatMoy(int taille)
    {
        float[][] mat = new float[taille][taille];
        for(int i =0;i<taille;i++)
        {
            for(int j = 0;j<taille;j++)
            {
                mat[i][j] = -1;
            }
        }


        mat[1][1] = 8;


        return mat;
    }


    /**Fonction de calcul de la somme des coeff de la matrice noyau.
     *
     * @param matrix
     * @return la somme des coeff de la matrice.
     */
    private static float calculTotal(float[][] matrix)
    {
        float somme = 0;
        for(int i = 0;i<matrix.length;i++)
        {
            for(int j =0;j<matrix.length;j++)
            {
                if(matrix[i][j]<0)
                    return -1;
                else
                    somme+=matrix[i][j];
            }
        }
        if(somme == 0)
        {
            return 1;

        }
        else
            return somme;

    }




}
