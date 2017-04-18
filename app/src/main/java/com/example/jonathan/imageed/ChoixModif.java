package com.example.jonathan.imageed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;


/**Activité permettant de choisir le:s modifications à apporter aux images
 *
 *
 */

public class ChoixModif extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //initialisation
        _teinte = 1;
        _tolerance = 1;
        _teinte2 = 1;
        _minLum = 1;
        _maxLum = 100;

        //Assignation de la layout à l'activité.
        setContentView(R.layout.activity_choix_modif);
        final Intent intent = getIntent();

        _appercu = intent.getParcelableExtra("appercu");
        _curAppercu = _appercu.copy(_appercu.getConfig(),true);


        final ImageView img =  (ImageView)findViewById(R.id.img_appercu);
        img.setScaleType(ImageView.ScaleType.CENTER);

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
                _curModif = MainActivity.TEST;


                float matrice[][] = MatriceGen.laplacien();
                _curAppercu = ImageEdit.convolutionScr(_appercu,matrice,getApplicationContext());
                img.setImageBitmap(_curAppercu);
            }
        });


        bout = (Button)findViewById(R.id.bout_teinte);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _curModif = MainActivity.CHG_TEINTE;
                _curAppercu = ImageEdit.sobelSrc(_appercu,getApplicationContext());
                img.setImageBitmap(_curAppercu);

            }
        });


        SeekBar bar = (SeekBar) findViewById(R.id.bar_chg_teinte);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    _curModif = MainActivity.CHG_TEINTE;
                    _teinte = progress;
                    _curAppercu = ImageEdit.changerTeinteScr(_appercu,_teinte,getApplicationContext());
                    img.setImageBitmap(_curAppercu);
                    float[] coul = new float[3];
                    coul[0] = _teinte-1;
                    coul[1] = 1;
                    coul[2] = 1;
                    SeekBar bar2 = (SeekBar)findViewById(R.id.bar_chg_teinte);
                    bar2.getProgressDrawable().setColorFilter(Color.HSVToColor(255,coul), PorterDuff.Mode.SRC_IN);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        bar = (SeekBar)findViewById(R.id.bar_filtrer_teinte);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    _curModif = MainActivity.FILTRER_TEINTE;
                    _teinte2 = progress;
                    _curAppercu = ImageEdit.filtrerTeinte(_appercu, _teinte2, _tolerance, getApplicationContext());
                    img.setImageBitmap(_curAppercu);

                    float[] coul = new float[3];
                    coul[0] = _teinte2-1;
                    coul[1] = 1;
                    coul[2] = 1;
                    SeekBar bar2 = (SeekBar)findViewById(R.id.bar_filtrer_teinte);
                    bar2.getProgressDrawable().setColorFilter(Color.HSVToColor(255,coul), PorterDuff.Mode.SRC_IN);


                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bar = (SeekBar)findViewById(R.id.bar_tolerance);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    _curModif = MainActivity.FILTRER_TEINTE;
                    _tolerance = progress;
                    _curAppercu = ImageEdit.filtrerTeinte(_appercu, _teinte2, _tolerance, getApplicationContext());
                    img.setImageBitmap(_curAppercu);


                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        bar = (SeekBar) findViewById(R.id.bar_lum_min);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                _minLum = progress;
                SeekBar bar2 = (SeekBar)findViewById(R.id.bar_lum_max);
                bar2.setProgress(Math.max(_minLum,bar2.getProgress()));
                _maxLum = bar2.getProgress();

                _curAppercu = ImageEdit.chgLum(_appercu,(float)(_minLum)/100.f,(float)(_maxLum)/100.f,getApplicationContext());
                img.setImageBitmap(_curAppercu);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        bar = (SeekBar) findViewById(R.id.bar_lum_max);
        bar.setProgress(_maxLum);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                _maxLum = progress;
                SeekBar bar2 = (SeekBar)findViewById(R.id.bar_lum_min);
                bar2.setProgress(Math.min(_maxLum,bar2.getProgress()));
                _minLum = bar2.getProgress();

                _curAppercu = ImageEdit.chgLum(_appercu,(float)(_minLum)/100.f,(float)(_maxLum)/100.f,getApplicationContext());
                img.setImageBitmap(_curAppercu);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                resultat.putExtra("teinte",_teinte);
                resultat.putExtra("teinte2",_teinte2);
                resultat.putExtra("tolerance",_tolerance);
                setResult(RESULT_OK,resultat);
                finish();
            }
        });


    }


    protected Bitmap _appercu;
    protected Bitmap _curAppercu;
    protected int _teinte;
    protected int _teinte2;
    protected int _minLum;
    protected int _maxLum;
    protected int _curModif;
    protected int _tolerance;



}
