package com.example.jonathan.imageed;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class ChoixModif extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_modif);


        //Assignation Boutons
        Button bout = (Button) findViewById(R.id.bout_griser);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result =  new Intent();
                result.putExtra("modif",MainActivity.GRISER);
                setResult(RESULT_OK,result);
                finish();
            }
        });

        //commentaire

        bout = (Button) findViewById(R.id.bout_ega);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result =  new Intent();
                result.putExtra("modif",MainActivity.EGALISER);
                setResult(RESULT_OK,result);
                finish();
            }
        });

        bout = (Button) findViewById(R.id.bout_dim_cont);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result =  new Intent();
                result.putExtra("modif",MainActivity.DIM_CONTRASTE);
                setResult(RESULT_OK,result);
                finish();
            }
        });

        bout = (Button) findViewById(R.id.bout_ext_cont);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result =  new Intent();
                result.putExtra("modif",MainActivity.EXT_DYN_CONTRASTE);
                setResult(RESULT_OK,result);
                finish();
            }
        });

        bout = (Button) findViewById(R.id.bout_teinte);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekBar barre = (SeekBar) findViewById(R.id.bar_teinte);
                float t = (barre.getProgress()*360)/100;
                Intent result  = new Intent();
                result.putExtra("modif",MainActivity.CHG_TEINTE);
                result.putExtra("teinte",t);
            }
        });
    }



}
