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
                    _curAppercu = ImageEdit.convolutionScr(_appercu,MatriceGen.gaussien(_gaussien1,_gaussien2),getApplicationContext());
                    textView6.setText("Gaussien : " + _gaussien1 + " x " + _gaussien1);
                    img.setImageBitmap(_curAppercu);

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
                    _curAppercu = ImageEdit.convolutionScr(_appercu,MatriceGen.gaussien(_gaussien1,_gaussien2),getApplicationContext());
                    textView7.setText("Sigma : " + _gaussien2 + "/" + seekBar.getMax()/10);
                    img.setImageBitmap(_curAppercu);

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
                if (progress <= 0) {
                    progress = 1;}

                if(fromUser)
                {
                    _curModif = MainActivity.MOYENNE;
                    _moyenne = progress;
                    _curAppercu = ImageEdit.convolutionScr(_appercu,MatriceGen.moyenne(_moyenne),getApplicationContext());
                    textView5.setText("Moyenne : " + _moyenne + " x " + _moyenne);
                    img.setImageBitmap(_curAppercu);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    protected int _moyenne;
    protected float _gaussien2;
    protected int _gaussien1;
    protected Bitmap _appercu;
    protected Bitmap _curAppercu;
    protected int _curModif;
}