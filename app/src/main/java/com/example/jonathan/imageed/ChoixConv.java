package com.example.jonathan.imageed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Button;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;

/**
 * Created by BENOIT on 22/03/2017.
 */

public class ChoixConv extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);





        setContentView(R.layout.activity_choix_conv);

        Button bout1 = (Button) findViewById(R.id.button1);
        bout1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent resultat =  new Intent();
                resultat.putExtra("modif",MainActivity.TEST);
                setResult(RESULT_OK,resultat);
                finish();




            }

        });

        Button bout2 = (Button) findViewById(R.id.button2);
        bout2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {




            }

        });


        Button bout3 = (Button) findViewById(R.id.button3);
        bout3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {




            }

        });

    }

}