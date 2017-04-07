package com.example.jonathan.imageed;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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


        //Assignation Boutons
        Button bout = (Button) findViewById(R.id.bout_griser);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultat =  new Intent();
                resultat.putExtra("modif",MainActivity.GRISER);
                setResult(RESULT_OK,resultat);
                finish();
            }
        });


        bout = (Button) findViewById(R.id.bout_ega);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultat =  new Intent();
                //ICI test
                resultat.putExtra("modif",MainActivity.TEST);
                setResult(RESULT_OK,resultat);
                finish();
            }
        });

        bout = (Button) findViewById(R.id.bout_dim_cont);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultat =  new Intent();
                resultat.putExtra("modif",MainActivity.DIM_CONTRASTE);
                setResult(RESULT_OK,resultat);
                finish();
            }
        });

        bout = (Button) findViewById(R.id.bout_ext_cont);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultat =  new Intent();
                resultat.putExtra("modif",MainActivity.EXT_DYN_CONTRASTE);
                setResult(RESULT_OK,resultat);
                finish();
            }
        });


    }



}
