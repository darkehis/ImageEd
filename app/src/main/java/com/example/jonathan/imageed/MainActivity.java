package com.example.jonathan.imageed;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import static android.R.attr.dial;
import static android.R.attr.onClick;
import static android.R.attr.x;
import static android.app.Activity.RESULT_OK;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
//import static com.example.jonathan.imageed.R.drawable.test;
import static java.security.AccessController.getContext;


/**L'activité principale de l'application.
 *
 *
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Assignation de la layout à l'activité
        setContentView(R.layout.activity_main);


        //Ajout de la barre d'outil: devenu obsolète
        /*Toolbar barreO = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(barreO);*/


        //Calcul des dimnsion max de l'image à modifier, ainsi que la taille max de l'aperçu: dépend du téléphone
        /*ATTENTION: si l'application plante lors de clic sur le bouton amenant à l'écran de choix de modifications sur l'émulateur:

        il faut modifier la fonction calculTailleMax en conséquence:

        ce bug ne se produit que sur les émulateurs, pas sur les téléphones physique pour une raison inconnue.

        */

        calculTaillesMax();

        //Création de l'image de départ dans une view de type: MonImage
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.test);

        _img = new MonImage(getBaseContext(),bmp);
        final int idImg = View.generateViewId();
        _img.setId(idImg);
        FrameLayout lay  = (FrameLayout) findViewById(R.id.lay_img);


        //Ajout de la view à la layout.
        lay.addView(_img);

        //connexion des différents boutons
        connexionBoutons();

    }

    /**Fonction de gestion des resultats des activités tieces.
     *
     *
     * @param requestCode le code de requête caractérisant les modifications à apporter à l'image.
     * @param resultCode le code resultat: juste une vérification que c'est bien RESULT_OK
     * @param data l'intent
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == MODIF_IMG || requestCode == MODIF_CONV) && resultCode == RESULT_OK)
        {
            int modif =  data.getIntExtra("modif",0);
            switch(modif)
            {
                //Griser l'image
                case GRISER:
                    _img.set_bmp(ImageEdit.griserScr(_img.get_bmp(),getApplicationContext()));
                    break;
                //Egaliser l'hitogramme de l'image.
                case EGALISER:

                    _img.set_bmp(ImageEdit.egaliserSrc(_img.get_bmp(),getApplicationContext()));

                    break;
                //diminuer le contrates de l'image
                case CONTRASTE:
                    _img.set_bmp(ImageEdit.extensionLineaireScr(_img.get_bmp(),data.getFloatExtra("contraste1",1),data.getFloatExtra("contraste2",1),getApplicationContext()));
                    break;
                //étendre dynamiquement le contratste de l'image.
                case LUMINOSITE:

                    _img.set_bmp(ImageEdit.luminosite(_img.get_bmp(),data.getFloatExtra("luminosite",1),getApplicationContext()));

                    break;


                case CHG_TEINTE:

                    _img.set_bmp(ImageEdit.changerTeinteScr(_img.get_bmp(),data.getIntExtra("teinte",1),getApplicationContext()));

                    break;



                case FILTRER_TEINTE:
                    _img.set_bmp(ImageEdit.filtrerTeinte(_img.get_bmp(),data.getIntExtra("teinte2",1),data.getIntExtra("tolerance",1),getApplicationContext()));


                    break;

                case CHG_LUM:

                    break;

                case SOBEL:

                    _img.set_bmp(ImageEdit.sobelSrc(_img.get_bmp(),getApplicationContext()));

                    break;
                case LAPLACIEN:

                    _img.set_bmp(ImageEdit.convolutionScr(_img.get_bmp(),MatriceGen.laplacien(),getApplicationContext()));

                    break;
                case GAUSSIEN:

                    _img.set_bmp(ImageEdit.convolutionScr(_img.get_bmp(),MatriceGen.gaussien(data.getIntExtra("gaussien1",1),data.getFloatExtra("gaussien2",1)),getApplicationContext()));

                    break;

                case MOYENNE:

                    _img.set_bmp(ImageEdit.convolutionScr(_img.get_bmp(),MatriceGen.moyenne(data.getIntExtra("moyenne",1)),getApplicationContext()));

                    break;
                case SEUIL:

                    _img.set_bmp(ImageEdit.seuilScr(_img.get_bmp(),data.getFloatExtra("seuil",1),getApplicationContext()));

                    break;
                case LUMINANCE:

                    _img.set_bmp((ImageEdit.luminanceScr(_img.get_bmp(),data.getFloatExtra("lum1",1),data.getFloatExtra("lum2",2),getApplicationContext())));

                    break;

            }


        }

        //récupération de la photo à partir du chemin fourni par l'activité appareil photo.
        else if (requestCode == PRENDRE_PHOTO && resultCode == RESULT_OK)
        {


            BitmapFactory.Options opt = new BitmapFactory.Options();

            opt.inScaled = false;
            opt.inMutable = true;
            _img.set_bmpBase(ImageEdit.apercu(BitmapFactory.decodeFile(_path,opt),_tailleMax));


        }

        //récupération d'un image à partir de la gallerie du téléphone.
        //code inspiré de : http://stackoverflow.com/questions/28530332/how-to-get-image-from-gallery
        else if (requestCode == GALERIE && resultCode == RESULT_OK)
        {
            Uri uriImg = data.getData();
            String[] colFich = { MediaStore.Images.Media.DATA };

            // obtenir le curseur de choix de fichier.
            Cursor curseur = getContentResolver().query(uriImg, colFich, null, null, null);
            // Initialisation au premier item
            curseur.moveToFirst();

            int index = curseur.getColumnIndex(colFich[0]);
            String imgDecodableString = curseur.getString(index);
            curseur.close();

            //décodage de l'image et création du bitmap.
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inScaled = false;
            opt.inMutable = true;


            _img.set_bmpBase(BitmapFactory.decodeFile(imgDecodableString,opt));

        }


    }


    /**Fonction pour prendre une photo à partir de l'appareil photo du téléphone
     * inspiré de :https://developer.android.com/training/camera/photobasics.html
     *
     */


    private void prendrePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File fichPhoto = null;
            try
            {
                fichPhoto= createFichImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            if(fichPhoto != null)
            {
                Log.i("photo",fichPhoto.getPath());
                Uri photoURI = FileProvider.getUriForFile(getBaseContext(),
                        "com.example.jonathan.imageed.fileprovider",
                        fichPhoto);


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent, PRENDRE_PHOTO);
            }

        }
    }


    /** Fonction pour séléectioner une photo à partir de la gallerie du téléphone.
     *
     *
     */

    private void galerie()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALERIE);


    }



    //Création d'un fichier pour la photo stockée
    private File createFichImage() throws IOException {
        // On creer un fichier pour la photo
        String tps = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String nomFich = "JPEG_" + tps + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                nomFich,  /* prefixe */
                ".jpg",         /* extension */
                storageDir      /* dossier */
        );
        _path =  image.getAbsolutePath();
        return image;
    }



    //Sauvegarde de l'image sur la carte SD
    protected void sauvegarder()
    {

        AlertDialog.Builder fenSauv = new AlertDialog.Builder(this);
        fenSauv.setTitle("Sauvegarde de l'image");
        fenSauv.setMessage("Entrez un nom de fichier:");
        fenSauv.setIcon(android.R.drawable.ic_dialog_alert);
        final EditText nom = new EditText(this);
        fenSauv.setView(nom);

        fenSauv.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nomFich = nom.getText().toString();
                if (!nomFich.equals(""))
                {
                    if(ecrireFichier(nomFich))
                    {
                        Toast.makeText(getApplicationContext(),"Fichier: " + nomFich + ".jpeg" + " sauvegardé.",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Erreur de sauvegarde: vérifiez que le fichier n'existe pas déjà.",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Nom de fichier incorrect.",Toast.LENGTH_LONG).show();
                }




            }
        });

        fenSauv.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


          fenSauv.show();
    }


    //fonction d'écriture lors de la sauvegarde: renvoie false si le fichier existe déjà ou si le nom n'est pas copatible.
    protected boolean ecrireFichier(String nom)
    {
        String racine = Environment.getExternalStorageDirectory().toString();
        File doss = new File(racine + "/ImageEd_image");
        doss.mkdir();
        String nomFich = nom + ".jpg";
        File file = new File (doss,nomFich);

        if(file.exists())
            return  false;

        try {
            FileOutputStream out = new FileOutputStream(file);
            _img.get_bmp().compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    protected void connexionBoutons()
    {
        ImageButton bout;



        //Bouton de modification des filtres par defaut
        bout = (ImageButton) findViewById(R.id.bout_modif);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),ChoixModif.class);
                intent.putExtra("apercu",_img.apercu(_tailleMaxApercu));
                startActivityForResult(intent,MODIF_IMG);

            }
        });


        //bouton pour les filtres peronnalisés


        bout = (ImageButton)findViewById(R.id.bout_mat);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChoixConv.class);
                intent.putExtra("apercu",_img.apercu(_tailleMaxApercu));
                startActivityForResult(intent,MODIF_CONV);
            }
        });

        //bouton annuler

        bout = (ImageButton)findViewById(R.id.bout_ann);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _img.annuler();
            }
        });

        //bouton restaurer

        bout = (ImageButton)findViewById(R.id.bout_rest);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _img.restaurer();
            }
        });


        //boton origine
        bout = (ImageButton)findViewById(R.id.bout_origine);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _img.origine();
            }
        });

        //bouton appareil photo
        bout = (ImageButton)findViewById(R.id.bout_photo);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prendrePhoto();
            }
        });


        //bouton bouton gallerie

        bout = (ImageButton)findViewById(R.id.bout_gal);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galerie();
            }
        });


        //bouton sauvegarder


        bout = (ImageButton) findViewById(R.id.bout_sauv);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sauvegarder();
            }
        });


        //bouton quitter

        /*bout = (ImageButton) findViewById(R.id.bout_quitter);
        finish();*/






    }



    //fonction de réduction des images pour ne pas trvailler sur des images trop grande qui risquerait de faire planter pour des surcherges de mémoire.

    protected void calculTaillesMax()
    {
        DisplayMetrics metric = getResources().getDisplayMetrics();

        //calcul de la taille en pixel de l'aperçu
        _tailleMaxApercu = (int)(190*metric.density);


        //on ne travail que sur des image dont aucune des dimensions ne dépasse la heuteur de l'écran multiplié par un facteur:
        _tailleMax = (int)(metric.heightPixels*1.6);


        /*Attention: suivant les émulateurs utilisés le calcul des tailles max ne fonctionnes pas:

            Il faut alors "décommentairiser" les deux lignes suivantes pour obtenir un réultat qui marche bien que moins optimal

        */


        /*
        _tailleMax = 1500;
        _tailleMaxApercu = 300;
        */

    }




    //l'image view sur laquelle on travaille
    protected MonImage _img;

    //chemin d'acces à la photo s'il y en a
    protected String _path;






    //les code des request code des intents.

    public static final int GRISER = 0;
    public static final int EGALISER = 1;
    public static final int CHG_TEINTE = 2;
    public static final int CONTRASTE = 3;
    public static final int LUMINOSITE = 4;
    public static final int PRENDRE_PHOTO = 5;

    public static final int MODIF_IMG = 6;
    public static final int GALERIE = 7;

    public static final int FILTRER_TEINTE = 8;
    public static final int LUMINANCE = 9;


    public static final int CHG_LUM = 11;

    public static final int MODIF_CONV = 10;
    public static final int SOBEL = 12;
    public static final int LAPLACIEN = 13;
    public static final int GAUSSIEN = 14;
    public static final int MOYENNE= 15;
    public static final int SEUIL = 16;



    public static int _tailleMax;
    public static int _tailleMaxApercu;

}
