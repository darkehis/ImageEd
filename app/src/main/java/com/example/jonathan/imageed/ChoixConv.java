package com.example.jonathan.imageed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.widget.Button;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by BENOIT on 22/03/2017.
 */

public class ChoixConv extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        _gaussien1 = 1;
        _gaussien2 = 1;

        setContentView(R.layout.activity_choix_conv);


        final Intent intent = getIntent();

        _apercu = intent.getParcelableExtra("apercu");
        _curApercu = _apercu.copy(_apercu.getConfig(),true);

        final ImageView img =  (ImageView)findViewById(R.id.img_apercu2);
        img.setScaleType(ImageView.ScaleType.CENTER);

        img.setImageBitmap(_curApercu);

        Button bout = (Button) findViewById(R.id.bout_sobel);
        bout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                        _curApercu = ImageEdit.sobelSrc(_apercu,getApplicationContext());
                        img.setImageBitmap(_curApercu);
                        _curModif = MainActivity.SOBEL;


            }

        });

        bout = (Button) findViewById(R.id.bout_laplacien);
        bout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                _curApercu = ImageEdit.convolutionScr(_apercu,MatriceGen.laplacien(),getApplicationContext());
                img.setImageBitmap(_curApercu);
                _curModif = MainActivity.LAPLACIEN;


            }

        });


        bout = (Button)findViewById(R.id.bout_conf2);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultat = new Intent();
                resultat.putExtra("modif",_curModif);
                resultat.putExtra("gaussien1",_gaussien1);
                resultat.putExtra("gaussien2",_gaussien2);
                resultat.putExtra("moyenne",_moyenne);
                setResult(RESULT_OK,resultat);
                finish();
            }
        });

        final TextView textView6 = (TextView) findViewById(R.id.textgauss);

        SeekBar bar = (SeekBar) findViewById(R.id.bar_gaussien1);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

                if(fromUser)
                {
                    _curModif = MainActivity.GAUSSIEN;
                    _gaussien1 = 2*progress+1;
                    _curApercu = ImageEdit.convolutionScr(_apercu,MatriceGen.gaussien(_gaussien1,_gaussien2),getApplicationContext());
                    textView6.setText("Gaussien : " + _gaussien1 + " x " + _gaussien1);
                    img.setImageBitmap(_curApercu);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView textView7 = (TextView) findViewById(R.id.textSigma);

        bar = (SeekBar) findViewById(R.id.bar_gaussien2);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (progress <= 0) {
                    progress = 1;}

                float progressValue = (float) progress/10;
                if(fromUser)
                {
                    _curModif = MainActivity.GAUSSIEN;
                    _gaussien2 = progressValue;
                    _curApercu = ImageEdit.convolutionScr(_apercu,MatriceGen.gaussien(_gaussien1,_gaussien2),getApplicationContext());
                    textView7.setText("Sigma : " + _gaussien2 + "/" + seekBar.getMax()/10);
                    img.setImageBitmap(_curApercu);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView textView5 = (TextView) findViewById(R.id.textmoy);

        bar = (SeekBar) findViewById(R.id.bar_moyenne);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {


                if(fromUser)
                {
                    _curModif = MainActivity.MOYENNE;
                    _moyenne = 2*progress+1;
                    _curApercu = ImageEdit.convolutionScr(_apercu,MatriceGen.moyenne(_moyenne),getApplicationContext());
                    textView5.setText("Moyenne : " + _moyenne + " x " + _moyenne);
                    img.setImageBitmap(_curApercu);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bout = (Button) findViewById(R.id.bout_annuler2);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekBar bar2 = (SeekBar)findViewById(R.id.bar_gaussien1);
                bar2.setProgress(1);
                textView6.setText("Gaussien : 1 x 1" );
                bar2 = (SeekBar)findViewById(R.id.bar_gaussien2);
                bar2.setProgress(0);
                textView7.setText("Sigma : 0/5" );
                bar2 = (SeekBar)findViewById(R.id.bar_moyenne);
                bar2.setProgress(1);
                textView5.setText("Moyenne : 1 x 1" );
                img.setImageBitmap(_apercu);
            }
        });

    }

    protected int _moyenne;
    protected float _gaussien2;
    protected int _gaussien1;
    protected Bitmap _apercu;
    protected Bitmap _curApercu;
    protected int _curModif;
}