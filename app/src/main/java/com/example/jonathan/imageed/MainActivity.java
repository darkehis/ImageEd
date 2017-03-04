package com.example.jonathan.imageed;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.test);
        _img = new MonImage(getBaseContext(),bmp);
        final int idImg = View.generateViewId();
        _img.setId(idImg);
        FrameLayout lay  = (FrameLayout) findViewById(R.id.lay_img);
        lay.addView(_img);


        Button bout = (Button) findViewById(R.id.bout_mod);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(),ChoixModif.class);
                startActivityForResult(intent,MODIF_IMG);
            }


        });

        bout = (Button) findViewById(R.id.bout_ori);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _img.origine();
            }
        });

        bout = (Button) findViewById(R.id.bout_phot);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prendrePhoto();
            }
        });

        bout = (Button) findViewById(R.id.bout_gal);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallerie();

            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MODIF_IMG && resultCode == RESULT_OK)
        {
            int modif =  data.getIntExtra("modif",0);
            Log.i("bli",Integer.toString(modif));
            switch(modif)
            {
                case GRISER:
                    _img.set_bmp(ImageEdit.griserScr(_img.get_bmp(),getApplicationContext()));
                    //_img.set_bmp(ImageEdit.griser(_img.get_bmp()));
                    break;
                case EGALISER:
                    _img.set_bmp(ImageEdit.egaliser(_img.get_bmp()));
                    break;
                case DIM_CONTRASTE:
                    _img.set_bmp(ImageEdit.diminutionContraste(_img.get_bmp()));
                    break;
                case TEST:
                    float matrice[][] = new float[3][3];
                    for(int i =0;i<3;i++)
                    {
                        for(int j = 0;j<3;j++)
                        {
                            matrice[i][j] = 1;
                        }
                    }
                    /*matrice[0][0] = -1;
                    matrice[0][1] = -1;
                    matrice[0][2] = -1;
                    matrice[1][0] = -1;
                    matrice[1][1] = 8;
                    matrice[1][2] = -1;
                    matrice[2][0] = -1;
                    matrice[2][1] = -1;
                    matrice[2][2] = -1;*/
                    _img.set_bmp(ImageEdit.convolutionScr(_img.get_bmp(),matrice,getApplicationContext()));
                    break;
            }


        }
        else if (requestCode == PRENDRE_PHOTO && resultCode == RESULT_OK)
        {


            //TODO: voir pour supprimer
            BitmapFactory.Options opt = new BitmapFactory.Options();

            opt.inScaled = false;
            opt.inMutable = true;
            _img.set_bmpBase(BitmapFactory.decodeFile(_path,opt));


        }

        //commentaire
        else if (requestCode == GALLERIE && resultCode == RESULT_OK)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            //ImageView imgView = (ImageView) findViewById(R.id.imgView);
            // Set the Image in ImageView after decoding the String
            //imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inScaled = false;
            opt.inMutable = true;


            _img.set_bmpBase(BitmapFactory.decodeFile(imgDecodableString,opt));

        }


    }


    /**Fonction pour prendre une photo à partir de l'appareil photo du téléphone
     *
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.i("photo","creation fichier");
        File image = File.createTempFile(
                imageFileName,  /* prefixe */
                ".jpg",         /* extension */
                storageDir      /* dossier */
        );
        Log.i("photo","creation fichier ok");
        _path =  image.getAbsolutePath();
        return image;
    }

    //l'image view sur laquelle on travaille
    protected MonImage _img;

    //chemin d'acces à la photo s'il y en a
    protected String _path;

    public static final int GRISER = 0;
    public static final int EGALISER = 1;
    public static final int CHG_TEINTE = 2;
    public static final int DIM_CONTRASTE = 3;
    public static final int EXT_DYN_CONTRASTE = 4;
    public static final int PRENDRE_PHOTO = 5;
    public static final int GALLERIE = 7;

    public static final int TEST = 8;
    public static final int MODIF_IMG = 6;
}
