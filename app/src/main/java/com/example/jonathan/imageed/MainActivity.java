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
import static com.example.jonathan.imageed.R.drawable.test;
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


        //Ajout de la barre d'outil
        /*Toolbar barreO = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(barreO);*/

        //Création de l'image de départ dans une view de type: MonImage
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.test);
       // Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.test2);

        Log.i("taille","" + bmp.getWidth() + "," + bmp.getHeight());
        _img = new MonImage(getBaseContext(),bmp);
        final int idImg = View.generateViewId();
        _img.setId(idImg);
        FrameLayout lay  = (FrameLayout) findViewById(R.id.lay_img);


        //Ajout de la view à la layout.
        lay.addView(_img);

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
                case DIM_CONTRASTE:
                    _img.set_bmp(ImageEdit.diminutionContraste(_img.get_bmp()));
                    break;
                //étendre dynamiquement le contratste de l'image.
                case EXT_DYN_CONTRASTE:
                    _img.set_bmp(ImageEdit.extensionContraste(_img.get_bmp()));
                    break;
                //test des nouvelles modification n'ayant pas encore de boutons attitrés

                case TEST:


                    float matrice[][] = MatriceGen.laplacien();


                    _img.set_bmp(ImageEdit.convolutionScr(_img.get_bmp(),matrice,getApplicationContext()));

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

            }


        }

        //récupération de la photo à partir du chemin fourni par l'activité appareil photo.
        else if (requestCode == PRENDRE_PHOTO && resultCode == RESULT_OK)
        {


            BitmapFactory.Options opt = new BitmapFactory.Options();

            opt.inScaled = false;
            opt.inMutable = true;
            _img.set_bmpBase(ImageEdit.appercu(BitmapFactory.decodeFile(_path,opt),1500));


        }

        //récupération d'un image à partir de la gallerie du téléphone.
        //code inspiré de : http://stackoverflow.com/questions/28530332/how-to-get-image-from-gallery
        else if (requestCode == GALLERIE && resultCode == RESULT_OK)
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

    private void gallerie()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERIE);


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


               /* .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })*/

          fenSauv.show();
    }


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




    //l'image view sur laquelle on travaille
    protected MonImage _img;

    //chemin d'acces à la photo s'il y en a
    protected String _path;


    public boolean onCreateOptionsMenu(Menu menu) {

        //Création d'un MenuInflater qui va permettre d'instancier un Menu XML en un objet Menu
        MenuInflater inflater = getMenuInflater();
        //Instanciation du menu XML spécifier en un objet Menu
        inflater.inflate(R.menu.settings, menu);

        //Il n'est pas possible de modifier l'icône d'en-tête du sous menu via le fichier XML on le fait donc en JAVA
        //menu.getItem(0).getSubMenu().setHeaderIcon(R.drawable.test);

        return true;
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
                intent.putExtra("appercu",_img.appercu(300));
                startActivityForResult(intent,MODIF_IMG);

            }
        });


        //bouton pour les filtres peronnalisés


        bout = (ImageButton)findViewById(R.id.bout_mat);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChoixConv.class);
                intent.putExtra("appercu",_img.appercu(300));
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
                gallerie();
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

    //Méthode qui se déclenchera au clic sur un item
    public boolean onOptionsItemSelected(MenuItem item) {
        //On regarde quel item a été cliqué grâce à son id et on déclenche une action
        Intent intent;
        switch (item.getItemId()) {

            case R.id.modifier:
                return true;

            case R.id.filtres:
                intent = new Intent(getBaseContext(),ChoixModif.class);
                intent.putExtra("appercu",_img.appercu(300));
                startActivityForResult(intent,MODIF_IMG);
                return true;

            case R.id.convolutions:
                intent = new Intent(getBaseContext(),ChoixConv.class);
                startActivityForResult(intent,MODIF_CONV);
                return true;

            case R.id.undo:
                _img.annuler();
                return true;
            case R.id.restaurer:
                _img.restaurer();
                return true;

            case R.id.galerie:
                gallerie();
                return true;

            case R.id.photos:
                prendrePhoto();
                return true;

            case R.id.sauvegarder:
                sauvegarder();
                return true;

            case R.id.réinitialiser:
                _img.origine();
                return true;

            case R.id.quitter:
                //Pour fermer l'application il suffit de faire finish()
                finish();
                return true;
        }
        return false;}


    //les code des request code des intents.

    public static final int GRISER = 0;
    public static final int EGALISER = 1;
    public static final int CHG_TEINTE = 2;
    public static final int DIM_CONTRASTE = 3;
    public static final int EXT_DYN_CONTRASTE = 4;
    public static final int PRENDRE_PHOTO = 5;

    public static final int MODIF_IMG = 6;
    public static final int GALLERIE = 7;

    public static final int FILTRER_TEINTE = 8;

    public static final int TEST = 9;


    public static final int CHG_LUM = 11;

    public static final int MODIF_CONV = 10;
    public static final int SOBEL = 12;
    public static final int LAPLACIEN = 13;
    public static final int GAUSSIEN = 14;
    public static final int MOYENNE= 15;
    public static final int SEUIL = 16;
}
