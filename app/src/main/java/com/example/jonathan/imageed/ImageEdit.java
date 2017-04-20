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





import static android.graphics.Bitmap.createBitmap;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.renderscript.Allocation.createSized;
//import static com.example.jonathan.imageed.R.drawable.test;

/**
 * Created by Jonathan on 20/01/2017.
 *
 * Classe contenant les fonctions de modification du bitmap.
 *
 *
 */



public class ImageEdit {


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


        //copie de l'allocation dans le bitmap résultat
        allocOut.copyTo(bmp2);

        script.destroy();
        RS.finish();
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

        //affectation des differents paramètres du script
        script.set_origin(origine);
        script.set_h_origin(bmpOri.getHeight());
        script.set_w_origin(bmpOri.getWidth());
        script.set_left(inter.left);
        script.set_top(inter.top);
        script.set_right(inter.right);
        script.set_bottom(inter.bottom);
        script.set_zoom(zoom);


        //Application du script à chaque pixel.


        script.forEach_root(allocIn,allocOut);
        allocOut.copyTo(bmp2);


        script.destroy();
        RS.finish();

        return bmp2;


    }

    /**
     * Fonction qui fait une extension linéaire de la saturation de l'image entre min max
     *
     *
     * @param bmp le bitmap à modifier
     * @param min la saturation min
     * @param max la saturation max
     * @param context le context de l'application
     * @return Bitmap avec une saturation entre min et max
     */



    public static Bitmap saturationSrc(Bitmap bmp, float min,float max,Context context)
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

        //Allocation intermédiaire pour le travail sur des float entre 0 et
        Allocation allocInter = Allocation.createTyped(RS, Type.createXY(RS,Element.F32_4(RS),bmp2.getWidth(),bmp2.getHeight()));

        //L'objet du script de grisage.
        ScriptC_HSV_RGB script = new ScriptC_HSV_RGB(RS);

        ScriptC_intervalle scriptInter = new ScriptC_intervalle(RS);



        //passage en HSV
        script.forEach_toHSV(allocIn,allocInter);

        //initialisaation du min et du max dans le script
        scriptInter.set_maxN(max);
        scriptInter.set_minN(min);

        //calcul du min et du max de la saturation pour le bitmap d'origine
        scriptInter.forEach_calculerMinMax1(allocInter,allocInter);


        //calcul de la nouvelle saturation
        scriptInter.forEach_inter1(allocInter,allocInter);





        //repassage en RGB
        script.forEach_toRGB(allocInter,allocOut);





        allocOut.copyTo(bmp2);

        script.destroy();
        scriptInter.destroy();
        RS.finish();

        return bmp2;




    }

    /**Fonction qui modifie la luminosité de l'image (ajout d'un même complement à chaque composante de l'image
     *
     * @param bmp Le bitmap à modifier
     * @param comp Le complement à apporter :entre 0 et 1
     * @param context Le context de l'application
     * @return Le bitmap modifié avec plus ou moins de luminosité
     */

    public static Bitmap luminosite(Bitmap bmp, float comp,Context context)
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


        ScriptC_luminosite script = new ScriptC_luminosite(RS);
        script.set_comp(comp);
        script.forEach_root(allocIn,allocOut);

        allocOut.copyTo(bmp2);

        script.destroy();
        RS.finish();
        return bmp2;



    }

    /**Fonction d'extension linéaire de la luminance
     *
     * @param bmp Le bitmap à modifier
     * @param min la luminance min
     * @param max la luminance max
     * @param context le context de l'application
     * @return Le bitmap modifié: dont l'histogramme de luminance est "étalé" entre min et max
     */

    public static Bitmap luminanceScr(Bitmap bmp, float min,float max,Context context)
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

        //Allocation intermédiaire
        Allocation allocInter = Allocation.createTyped(RS, Type.createXY(RS,Element.F32_4(RS),bmp2.getWidth(),bmp2.getHeight()));


        ScriptC_HSV_RGB script = new ScriptC_HSV_RGB(RS);

        ScriptC_intervalle scriptInter = new ScriptC_intervalle(RS);



        //passage de RGB à HSV
        script.forEach_toHSV(allocIn,allocInter);


        //initialisation du min et du max de la nouvelle luminance.
        scriptInter.set_maxN(max);
        scriptInter.set_minN(min);

        //calcul du min et du max de la luminance d'origine
        scriptInter.forEach_calculerMinMax2(allocInter,allocInter);


        //calcul des nouvelles luminances
        scriptInter.forEach_inter2(allocInter,allocInter);





        //repassage en RGB
        script.forEach_toRGB(allocInter,allocOut);





        allocOut.copyTo(bmp2);

        script.destroy();
        scriptInter.destroy();
        RS.finish();

        return bmp2;

    }


    /**Fonction d'extension linéaire de la dynamique
     *
     *
     * @param bmp Le Bitmap à modifier
     * @param min le niveau de gris min
     * @param max le niveau de gris max
     * @param context le Context de l'application
     * @return Le bitmap modifié: grisé dont l'histogramme s'étale entre 0 et 1 (0 et 255 pour les composantes RGB)
     */

    public static Bitmap extensionLineaireScr(Bitmap bmp, float min,float max, Context context)
    {

        Bitmap bmp2 = bmp.copy(bmp.getConfig(),true);

        bmp2 = griserScr(bmp2,context);


        //Classe d'acces à la couche renderscript.
        RenderScript RS = RenderScript.create(context);

        //Creéation du cript.
        ScriptC_HSV_RGB script = new ScriptC_HSV_RGB(RS);


        //Allocation correspondante au bitmap de départ.
        Allocation allocIn = Allocation.createFromBitmap(RS, bmp2, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);


        //Allocation intermédiaire au cas ou il faille changer l'intervalle des différents coefficients.
        Allocation allocInter = Allocation.createTyped(RS, Type.createXY(RS,Element.F32_4(RS),bmp2.getWidth(),bmp2.getHeight()));


        //Allocation correspondante au bitmap resultat
        Allocation allocOut = Allocation.createTyped(RS, allocIn.getType());


        script.forEach_toHSV(allocIn,allocInter);


        ScriptC_intervalle scriptInter = new ScriptC_intervalle(RS);

        scriptInter.set_minN(min);
        scriptInter.set_maxN(max);
        scriptInter.forEach_calculerMinMax2(allocInter,allocInter);
        scriptInter.forEach_inter2(allocInter,allocInter);

        script.forEach_toRGB(allocInter,allocOut);




        allocOut.copyTo(bmp2);

        script.destroy();
        scriptInter.destroy();
        RS.finish();

        return bmp2;



    }


    /**Fonction de seuillage de l'image en niveau de gris
     *
     *
     *
     * @param bmp Le Bitmap à modifier
     * @param seuil Le seuil entre 0 et 1
     * @param context Le context de l'application
     * @return Le bitmap grisé et seuillé
     */

    public static  Bitmap seuilScr(Bitmap bmp,float seuil, Context context)
    {
        Bitmap bmp2 = griserScr(bmp,context);

        //Classe d'acces à la couche renderscript.
        RenderScript RS = RenderScript.create(context);

        //Creéation du cript.
        ScriptC_seuiller script = new ScriptC_seuiller(RS);


        //Allocation correspondante au bitmap de départ.
        Allocation allocIn;

        allocIn = Allocation.createFromBitmap(RS, bmp2, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        //Allocation correspondante au bitmap resultat
        Allocation allocOut = Allocation.createTyped(RS, allocIn.getType());

        script.set_seuil(seuil);

        script.forEach_root(allocIn,allocOut);

        allocOut.copyTo(bmp2);

        script.destroy();
        RS.finish();

        return bmp2;

    }

    /**Fonction de détection de contours de type Sobel
     *
     *
     * @param bmp Le Bitmap à modifier
     * @param context
     * @return Le Bitmap Sobelisé
     */
    public static Bitmap sobelSrc(Bitmap bmp, Context context)
    {
        Bitmap bmp2 = griserScr(bmp,context);

        //Classe d'acces à la couche renderscript.
        RenderScript RS = RenderScript.create(context);

        //Creéation du cript.
        ScriptC_convolution script = new ScriptC_convolution(RS);





        //Transformation de la matrice en tableau de 1 dimension.

        float[][] matrix1 = MatriceGen.sobel1();
        //taille de la matrice
        int dim = matrix1.length;

        float[] matrix1D1 = MatriceGen.lineariser(matrix1);



        float[] matrix1D2 = MatriceGen.lineariser(MatriceGen.sobel2());




        //nb de coeff de la matrice.
        int taille = dim*dim;
        //coordonnées du coeficient centrale de la matrice.
        int centre = dim/2;

        //calcul de la somme des coeffiecients, renvoie -1 si un coeff est négatif
        float total = calculTotal(matrix1);


        //Allocation correspondante au bitmap de départ.
        Allocation allocIn;

        allocIn = Allocation.createFromBitmap(RS, bmp2, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        //Allocation correspondante au bitmap resultat
        Allocation allocOut = Allocation.createTyped(RS, allocIn.getType());

        //Allocation intermédiaire apres application du premier filtre de Sobel
        Allocation allocInter1 = Allocation.createTyped(RS, Type.createXY(RS,Element.F32_4(RS),bmp2.getWidth(),bmp2.getHeight()));

        //Allocation intermédiaire apres application 2ème filtre sobel
        Allocation allocInter2 = Allocation.createTyped(RS, Type.createXY(RS,Element.F32_4(RS),bmp2.getWidth(),bmp2.getHeight()));

        //Allocation intermédiaire3 :Sobel des 2 présédent
        Allocation allocInter3 = Allocation.createTyped(RS, Type.createXY(RS,Element.F32_4(RS),bmp2.getWidth(),bmp2.getHeight()));

        //Allocation correspondante à la matrice noyau.
        Allocation matAll = Allocation.createSized(RS,Element.F32(RS),taille);
        matAll.copy1DRangeFrom(0,taille,matrix1D1);

        //variables dont on a besoin pour faire tourner le script.
        Allocation img = Allocation.createFromBitmap(RS,bmp2, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);


        //initialisation des variables
        script.set_matrice2(matAll);

        script.set_img(img);
        script.set_dim(dim);
        script.set_centre(centre);
        script.set_w_img(bmp2.getWidth());
        script.set_h_img(bmp2.getHeight());
        script.set_taille(taille);
        script.set_total(total);


        //application du premier filtre de convolution sobel
        script.forEach_root(allocIn,allocInter1);


        matAll.copy1DRangeFrom(0,taille,matrix1D2);
        script.set_matrice2(matAll);


        //application du 2ème filtre de convolution sobel
        script.forEach_root(allocIn,allocInter2);

        ScriptC_sobeliser scriptSob = new ScriptC_sobeliser(RS);

        scriptSob.set_img1(allocInter1);
        scriptSob.set_img2(allocInter2);

        //"Sommage des 2"

        scriptSob.forEach_root(allocInter3,allocInter3);




        ScriptC_intervalle scriptInter =  new ScriptC_intervalle(RS);


        if(total == -1)
        {
            scriptInter.set_initialise(0);
            scriptInter.forEach_calculerMinMax0(allocInter3,allocInter3);
            scriptInter.forEach_interNo(allocInter3,allocInter3);
        }

        scriptInter.forEach_toBmp(allocInter3,allocOut);


        allocOut.copyTo(bmp2);

        script.destroy();
        scriptInter.destroy();
        RS.finish();

        return bmp2;




    }

    /**Fonction d'appel du script d'égalisation de constraste
     *
     * @param bmp le bitmap à modifier
     * @param context le context de l'application
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


        //récupération de l'histogramme.

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
        if(total != -1)
        {
            matrix = MatriceGen.normaliser(matrix);
        }

        float matrix1D[] = MatriceGen.lineariser(matrix);




        //on travail sur une image en niveaux de gris si le noyau contient des négatifs.
        Log.i("lapla","" + total);
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



        ScriptC_intervalle scriptInter =  new ScriptC_intervalle(RS);


        if(total == -1)
        {
            scriptInter.set_initialise(0);
            scriptInter.forEach_calculerMinMax2(allocInter,allocInter);
            scriptInter.forEach_interNo2(allocInter,allocInter);

        }

        scriptInter.forEach_toBmp(allocInter,allocOut);

        allocOut.copyTo(bmp2);



        script.destroy();
        scriptInter.destroy();
        RS.finish();
        if(total == -1)
            bmp2 = ImageEdit.griserScr(bmp2,context);
        return bmp2;

    }

    /**Fonction de changement de teinte de l'image
     *
     *
     *
     * @param bmp Le bitmap à modifier
     * @param teinte La nouvelle teinte à appliquer
     * @param context Le context de l'application
     * @return Le bitmap modifié avec la nouvelle teinte
     */



    public static Bitmap changerTeinteScr(Bitmap bmp, int teinte, Context context)
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

        //Le script
        ScriptC_teinter script = new ScriptC_teinter(RS);

        script.set_teinte(teinte);

        //l'application du script à chaque pixel.
        script.forEach_root(allocIn,allocOut);



        allocOut.copyTo(bmp2);

        script.destroy();
        RS.finish();
        return bmp2;


    }

    /**Fonction de grisage de l'image à l'exception d'une certaine teintte avec une certaine tolerance.
     *
     *
     *
     * @param bmp Le bitmap à modifier
     * @param teinte La teinte à garder
     * @param tolerance La tolerance
     * @param context Le context de l'application
     * @return Le bitmap grisé auf les pixel dont la teinte est proche de la teinte demandée.
     */

    public static Bitmap filtrerTeinte(Bitmap bmp, int teinte, int tolerance,Context context)
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
        ScriptC_filtrerCouleur script = new ScriptC_filtrerCouleur(RS);

        script.set_teinte(teinte);

        script.set_tolerance(tolerance);

        //l'application du script à chaque pixel.
        script.forEach_root(allocIn,allocOut);



        allocOut.copyTo(bmp2);

        script.destroy();
        RS.finish();

        return bmp2;




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


    /**Fonction de réduction de l'image pour obtenir soit un appercu pour le choix des modification, soit pour ne pas travailler sur une image trop grance.
     *
     *
     *
     * @param bmp Le Bitmap à modifier
     * @param tailleM La taille max qu'aucune des 2 dimension ne peut dépasser
     * @return Le Bitmap réduit
     */

    public static Bitmap apercu(Bitmap bmp,int tailleM)
    {
        int maxDim,fact,nW,nH;
        //l'apercu fera au plus tailleM px de long/large;
        if(bmp.getHeight()<tailleM && bmp.getWidth()<tailleM)
        {
            return bmp;
        }
        else
        {
            int w = bmp.getWidth();
            int h = bmp.getHeight();
            maxDim = Math.max(w,h);
            fact = (maxDim/tailleM)+1;
            nW = w/fact;
            nH = h/fact;
            int[] pix1 = new int[w*h];
            int[] pix2 = new int[nW*nH];

            bmp.getPixels(pix1,0,w,0,0,w,h);
            for(int i =0;i<pix2.length;i++)
            {
                pix2[i] = pix1[fact*((i/nW)*w + (i%nW))];
            }
            return Bitmap.createBitmap(pix2,nW,nH, Bitmap.Config.ARGB_8888);

        }

    }


}
