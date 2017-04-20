package com.example.jonathan.imageed;

/**
 * Created by Jonathan on 15/04/2017.
 */

//Clase de génération des matrices de convolution à la volée, aini que de différentes opération sur les matrices.

public class MatriceGen {


    //génration d'une matrice  pour le filtre Laplacien
    public static float[][] laplacien()
    {
        float[][] mat = new float[3][3];
        for(int i =0;i<3;i++)
        {
            for(int j = 0;j<3;j++)
            {
                mat[i][j] = -1;
            }
        }


        mat[1][1] = 8;


        return mat;

    }

    //Première matrice pour le filtre Sobel

    public static float[][] sobel1()
    {
        float[][] mat = new float[3][3];
        mat[0][0] = -1;
        mat[0][1] = 0;
        mat[0][2] = 1;
        mat[1][0] = -2;
        mat[1][1] = 0;
        mat[1][2] = 2;
        mat[2][0] = -1;
        mat[2][1] = 0;
        mat[2][2] = 1;

        return mat;

    }
    //Seconde matrice pour le filtre Sobel
    public static float[][] sobel2()
    {
        float[][] mat = new float[3][3];
        mat[0][0] = -1;
        mat[0][1] = -2;
        mat[0][2] = -1;
        mat[1][0] = 0;
        mat[1][1] = 0;
        mat[1][2] = 0;
        mat[2][0] = 1;
        mat[2][1] = 2;
        mat[2][2] = 1;
        return mat;


    }

    //Matrice pour le flou moyennant
    public static float[][] moyenne(int taille)
    {
        float[][] mat = new float[taille][taille];
        for(int i =0;i<taille;i++)
        {
            for(int j = 0;j<taille;j++)
            {
                mat[i][j] = 1;
            }
        }

        return normaliser(mat);
    }

    //Matrice pour le flou gaussien

    public static float[][] gaussien(int taille,float sigma)
    {
        float[][] mat = new float[taille][taille];
        int centre = (int)(taille/2);
        int x,y;
        for(int i =0;i<taille;i++)
        {
            for(int j = 0;j<taille;j++)
            {
                x = i-centre;
                y = j-centre;
                mat[i][j] = (1/(float)(Math.PI*sigma*sigma))*(float)Math.exp((-1)*(x*x+y*y)/(2*sigma*sigma));
            }
        }

        return normaliser(mat);

    }

    //Fonction pour faire d'une matrice en 2 dimensions une matrice en 1 dimension

    protected static float[] lineariser(float[][] mat)
    {
        float[] mat2 = new float[mat.length*mat.length];
        for(int i =0;i<mat.length;i++)
        {
            for(int j =0;j<mat.length;j++)
            {
                mat2[j*mat.length + i] = mat[i][j];
            }
        }

        return mat2;

    }

    //Normalisation des coefficient de la matrice.
    protected static float[][] normaliser(float[][] mat)
    {
        float total = 0;
        for(int i =0;i<mat.length;i++)
        {
            for(int j = 0 ;j<mat.length;j++)
            {
                total+=mat[i][j];
            }
        }
        if(total !=0)
        {
            for(int i =0;i<mat.length;i++)
            {
                for(int j = 0 ;j<mat.length;j++)
                {
                    mat[i][j] /=total;
                }
            }

        }
        return mat;

    }
}
