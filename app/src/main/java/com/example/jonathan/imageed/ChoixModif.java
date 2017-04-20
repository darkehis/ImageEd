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
import android.widget.TextView;

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
        /*_teinte = 1;
        _tolerance = 1;
        _teinte2 = 1;
        _minLum = 1;
        _maxLum = 100;*/

        //Assignation de la layout à l'activité.
        setContentView(R.layout.activity_choix_modif);
        final Intent intent = getIntent();

        _apercu = intent.getParcelableExtra("apercu");
        _curApercu = _apercu.copy(_apercu.getConfig(),true);


        final ImageView img =  (ImageView)findViewById(R.id.img_apercu);
        img.setScaleType(ImageView.ScaleType.CENTER);

        img.setImageBitmap(_curApercu);


        //Assignation Boutons
        Button bout = (Button) findViewById(R.id.bout_griser);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _curApercu = ImageEdit.griserScr(_apercu,getApplicationContext());
                img.setImageBitmap(_curApercu);
                _curModif = MainActivity.GRISER;

            }
        });


        bout = (Button) findViewById(R.id.bout_ega);

        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _curApercu = ImageEdit.egaliserSrc(_apercu,getApplicationContext());
                img.setImageBitmap(_curApercu);
                _curModif = MainActivity.EGALISER;
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
                    _curApercu = ImageEdit.changerTeinteScr(_apercu,_teinte,getApplicationContext());
                    img.setImageBitmap(_curApercu);
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

        final TextView textView10 = (TextView) findViewById(R.id.textContr);

        bar = (SeekBar)findViewById(R.id.bar_contr1);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                    _minCont = (float) progress/100;
                    SeekBar bar2 = (SeekBar)findViewById(R.id.bar_contr2);
                    bar2.setProgress(Math.max(progress,bar2.getProgress()));
                    _maxCont = (float) bar2.getProgress()/100;

                    _curApercu = ImageEdit.extensionLineaireScr(_apercu,_minCont,_maxCont,getApplicationContext());

                    textView10.setText("Contraste : " + _minCont + "/" + _maxCont);
                    img.setImageBitmap(_curApercu);



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bar = (SeekBar)findViewById(R.id.bar_contr2);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                    _maxCont = (float) progress/100;
                    SeekBar bar2 = (SeekBar)findViewById(R.id.bar_contr1);
                    bar2.setProgress(Math.min(progress,bar2.getProgress()));
                    _minCont = (float) bar2.getProgress()/100;

                    _curApercu = ImageEdit.extensionLineaireScr(_apercu,_minCont,_maxCont,getApplicationContext());

                    textView10.setText("Contraste : " + _minCont + "/" + _maxCont);
                    img.setImageBitmap(_curApercu);



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView textView11 = (TextView) findViewById(R.id.textSat1);

        bar = (SeekBar)findViewById(R.id.bar_sat1);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                _maxCont = (float) progress/100;
                SeekBar bar2 = (SeekBar)findViewById(R.id.bar_contr1);
                bar2.setProgress(Math.min(progress,bar2.getProgress()));
                _minCont = (float) bar2.getProgress()/100;

                _curApercu = ImageEdit.extensionLineaireScr(_apercu,_minCont,_maxCont,getApplicationContext());

                textView10.setText("Contraste : " + _minCont + "/" + _maxCont);
                img.setImageBitmap(_curApercu);



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView textView12 = (TextView) findViewById(R.id.textSat2);

        bar = (SeekBar)findViewById(R.id.bar_sat2);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                _maxCont = (float) progress/100;
                SeekBar bar2 = (SeekBar)findViewById(R.id.bar_contr1);
                bar2.setProgress(Math.min(progress,bar2.getProgress()));
                _minCont = (float) bar2.getProgress()/100;

                _curApercu = ImageEdit.extensionLineaireScr(_apercu,_minCont,_maxCont,getApplicationContext());

                textView10.setText("Contraste : " + _minCont + "/" + _maxCont);
                img.setImageBitmap(_curApercu);



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView textView = (TextView) findViewById(R.id.textSeuil);


        bar = (SeekBar)findViewById(R.id.bar_seuil);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float progressValue = (float) progress/20;
                if (fromUser) {
                    _curModif = MainActivity.SEUIL;
                    _seuil = progressValue;
                    _curApercu = ImageEdit.seuilScr(_apercu,_seuil, getApplicationContext());
                    textView.setText("Seuil : " + _seuil + "/1" );
                    img.setImageBitmap(_curApercu);

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

        final TextView textView4 = (TextView) findViewById(R.id.textfiltreteinte);
        bar = (SeekBar)findViewById(R.id.bar_filtrer_teinte);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    _curModif = MainActivity.FILTRER_TEINTE;
                    _teinte2 = progress;
                    _curApercu = ImageEdit.filtrerTeinte(_apercu, _teinte2, _tolerance, getApplicationContext());

                    img.setImageBitmap(_curApercu);

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

        final TextView textView3 = (TextView) findViewById(R.id.texttole);

        bar = (SeekBar)findViewById(R.id.bar_tolerance);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    _curModif = MainActivity.FILTRER_TEINTE;
                    _tolerance = progress;
                    _curApercu = ImageEdit.filtrerTeinte(_apercu, _teinte2, _tolerance, getApplicationContext());
                    textView3.setText("Tolérance : " + _tolerance + "/" + seekBar.getMax());
                    if (_tolerance <= 1) {
                        textView4.setText("Filtrer " + _tolerance + " teinte");
                    }
                    else textView4.setText("Filtrer " + _tolerance + " teintes");
                    img.setImageBitmap(_curApercu);


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

        final TextView textView2 = (TextView) findViewById(R.id.textLum);
        bar = (SeekBar) findViewById(R.id.bar_lum_min);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                _minLum = progress;
                SeekBar bar2 = (SeekBar)findViewById(R.id.bar_lum_max);
                bar2.setProgress(Math.max(_minLum,bar2.getProgress()));
                _maxLum = bar2.getProgress();

                _curApercu = ImageEdit.chgLum(_apercu,(float)(_minLum)/100.f,(float)(_maxLum)/100.f,getApplicationContext());
                textView2.setText("Luminosité : " + _minLum + "/" + _maxLum);
                img.setImageBitmap(_curApercu);

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

                _curApercu = ImageEdit.chgLum(_apercu,(float)(_minLum)/100.f,(float)(_maxLum)/100.f,getApplicationContext());

                textView2.setText("Luminosité : " + _minLum + "/" + _maxLum);
                img.setImageBitmap(_curApercu);

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
                SeekBar bar2 = (SeekBar)findViewById(R.id.bar_seuil);
                bar2.setProgress(0);
                textView.setText("Seuil : 0/1" );
                bar2 = (SeekBar)findViewById(R.id.bar_sat1);
                bar2.setProgress(0);
                bar2 = (SeekBar)findViewById(R.id.bar_sat2);
                bar2.setProgress(0);
                bar2 = (SeekBar)findViewById(R.id.bar_chg_teinte);
                bar2.setProgress(0);
                bar2 = (SeekBar)findViewById(R.id.bar_filtrer_teinte);
                bar2.setProgress(0);
                textView4.setText("Filtrer 0 teinte" );
                bar2 = (SeekBar)findViewById(R.id.bar_tolerance);
                bar2.setProgress(0);
                textView3.setText("Tolérance : 0/360" );
                bar2 = (SeekBar)findViewById(R.id.bar_lum_min);
                bar2.setProgress(0);
                bar2 = (SeekBar)findViewById(R.id.bar_lum_max);
                bar2.setProgress(100);
                textView2.setText("Luminosité : 0/100" );
                img.setImageBitmap(_apercu);
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
                resultat.putExtra("seuil",_seuil);
                setResult(RESULT_OK,resultat);
                finish();
            }
        });


    }

    protected float _minCont;
    protected float _maxCont;
    protected float _seuil;
    protected Bitmap _apercu;
    protected Bitmap _curApercu;
    protected int _teinte;
    protected int _teinte2;
    protected int _minLum;
    protected int _maxLum;
    protected int _curModif;
    protected int _tolerance;



}
