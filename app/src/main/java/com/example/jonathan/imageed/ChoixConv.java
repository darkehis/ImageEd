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


        final Intent intent = getIntent();

        _appercu = intent.getParcelableExtra("appercu");
        _curAppercu = _appercu.copy(_appercu.getConfig(),true);

        final ImageView img =  (ImageView)findViewById(R.id.img_appercu2);
        img.setScaleType(ImageView.ScaleType.CENTER);

        img.setImageBitmap(_curAppercu);

        Button bout = (Button) findViewById(R.id.bout_sobel);
        bout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                        _curAppercu = ImageEdit.sobelSrc(_appercu,getApplicationContext());
                        img.setImageBitmap(_curAppercu);
                        _curModif = MainActivity.SOBEL;


            }

        });

        bout = (Button) findViewById(R.id.bout_laplacien);
        bout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                _curAppercu = ImageEdit.convolutionScr(_appercu,MatriceGen.laplacien(),getApplicationContext());
                img.setImageBitmap(_curAppercu);
                _curModif = MainActivity.LAPLACIEN;


            }

        });

        bout = (Button) findViewById(R.id.bout_annuler2);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setImageBitmap(_appercu);
            }
        });


        bout = (Button)findViewById(R.id.bout_conf2);

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