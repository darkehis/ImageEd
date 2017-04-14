package com.example.jonathan.imageed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;


/**Activité permettant de choisir le:s modifications à apporter aux images
 *
 *
 */

public class ChoixModif extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Assignation de la layout à l'activité.
        setContentView(R.layout.activity_choix_modif);
        Intent intent = getIntent();

        _appercu = intent.getParcelableExtra("appercu");
        _curAppercu = _appercu.copy(_appercu.getConfig(),true);

        final ImageView img =  (ImageView)findViewById(R.id.img_appercu);
        img.setScaleType(ImageView.ScaleType.CENTER);

        Log.i("appercu","" + img.getMinimumWidth());
        img.setImageBitmap(_curAppercu);


        //Assignation Boutons
        Button bout = (Button) findViewById(R.id.bout_griser);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _curAppercu = ImageEdit.griserScr(_appercu,getApplicationContext());
                img.setImageBitmap(_curAppercu);
                _curModif = MainActivity.GRISER;

            }
        });


        bout = (Button) findViewById(R.id.bout_ega);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _curAppercu = ImageEdit.egaliserSrc(_appercu,getApplicationContext());
                Log.i("ega","ok");
                img.setImageBitmap(_curAppercu);
                _curModif = MainActivity.EGALISER;
            }
        });

        bout = (Button) findViewById(R.id.bout_dim_cont);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _curModif = MainActivity.DIM_CONTRASTE;
            }
        });

        bout = (Button) findViewById(R.id.bout_ext_cont);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _curModif = MainActivity.EXT_DYN_CONTRASTE;
            }
        });


        bout = (Button) findViewById(R.id.bout_annuler);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setImageBitmap(_appercu);
            }
        });


        bout = (Button)findViewById(R.id.bout_conf);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultat = new Intent();
                resultat.putExtra("modif",_curModif);
                setResult(RESULT_OK,resultat);
                finish();
            }
        });


    }


    protected Bitmap _appercu;
    protected Bitmap _curAppercu;
    protected int _curModif;



}
