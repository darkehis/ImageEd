package com.example.jonathan.imageed;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

;

import android.widget.ImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import static android.R.attr.x;
import static android.R.attr.y;
import static android.R.id.list;
import static android.R.id.message;
import static android.graphics.Bitmap.createBitmap;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by Jonathan on 12/01/2017.
 */

public class MonImage extends ImageView {


    public MonImage(Context context, Bitmap bmp) {
        super(context);

        _context = context;

        //initialisation variables
        setScaleType(ScaleType.CENTER);
        _bmpBase = bmp; //le bitmap de base:

        //creation du tableau de bitmap à retenir pour pouvoir annuler les modifications

        _bmp = new Bitmap[_nbBmp];
        _numCurBmp = 0;


        _bmp[_numCurBmp] = _bmpBase.copy(_bmpBase.getConfig(),true);




        //raz();




        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MonImage img = (MonImage) v;
                //un seul point de touch: on scroll ou on raz le zoom
                if(event.getPointerCount() == 1)
                {
                    if(event.getActionMasked() == MotionEvent.ACTION_DOWN)
                    {
                        _xS1 = event.getX();
                        _yS1 = event.getY();
                        long time = System.currentTimeMillis();
                        //check du double click
                        if(time - _lastClick <500)
                        {
                            raz();
                        }
                        _lastClick = time;
                        //auquel cas on raz le zoom

                    }
                    else if(event.getActionMasked() == MotionEvent.ACTION_UP)
                    {

                        //v.scrollTo(0,0);

                    }
                    else if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
                    {
                        double dx,dy;
                        dx = _xS1 - event.getX();
                        dy = _yS1 - event.getY();
                        if(Math.abs(dx) > 10 || Math.abs(dy) > 10)
                        {
                            int x,y;
                            x = (int) (dx + _rect.left);
                            y = (int) (dy + _rect.top);
                            _xS1 = event.getX();
                            _yS1 = event.getY();

                            //x = checkBound(x,0,_bmp.getWidth() - _rect.width());
                            //y = checkBound(y,0,_bmp.getHeight() - _rect.height());

                            int h = _rect.height();
                            int w = _rect.width();

                            _ancRect = _rect;
                            _rect = new Rect(x,y,x+w,y+h);

                            majRect();
                        }

                    }

                }
                //2 points de touch: on zoom
                else if(event.getPointerCount() == 2)
                {
                    if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
                    {

                        _xS2 = event.getX(1);
                        _yS2 = event.getY(1);
                        _cX = ((_xS1 + _xS2)/2)/_fZoom + _rect.left;
                        _cY = ((_yS1 + _yS2) / 2)/_fZoom + _rect.top;
                        //calcul de la distance de départ entre les 2 points
                        _distanceS = distance(event.getX(0),event.getX(1),event.getY(0),event.getY(1));

                    }
                    if(event.getActionMasked() == MotionEvent.ACTION_MOVE)
                    {
                        //calcul de la nouvelle distance
                        _distanceF = distance(event.getX(0),event.getX(1),event.getY(0),event.getY(1));

                        //calcul du facteur de zoom: le rapport entre la longueur de départ et finale.

                        //on calcul le rapport entre les 2 distance qui sera le facteur de zoom
                        double zoom  = (double)_distanceF/_distanceS;

                        _distanceS = _distanceF;

                        int nW,nH;
                        nW = (int)(_rect.width()/zoom);
                        nH = (int)(_rect.height()/zoom);

                        _rect = new Rect((int)(_cX - nW/2),(int)(_cY - nH/2),(int)(_cX + nW/2),(int)(_cY + nH/2));

                        //Log.i("zoom","on a:" + _rect.left + "," + _rect.top + "," + _rect.right + ","  + _rect.bottom);

                        majRect();



                    }
                    if(event.getActionMasked() == MotionEvent.ACTION_POINTER_UP)
                    {


                    }

                }

                return true;
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!_isInitialised)
        {
            initialisation();
            _isInitialised = true;
        }
    }

    public void initialisation()
    {
        //initialisation des dims de l'image
        _w = getWidth();
        _h = getHeight();
        //initialisation du rcatangle d'affichage
        //_rect = new Rect(0,0,(int)(_w/2),(int)(_h/2));

        _rect = new Rect(0,0,_w,_h);

        majRect();



        //initialisation du timer.
        _lastClick = 0;
    }

    public void majRect()
    {
        //d'abord on check les bord du rectangle pour qu'il ne dépasse pas
        //Log.i("rect","ok" + _rect.left + "," + _rect.top + ":" + _bmp.getWidth() + "," + _bmp.getHeight());


        //commentaire
        //Log.i("maj_rect","on maj");
        if(_rect.width()>_bmpBase.getWidth())
        {
            //Log.i("maj_rect","trop large");
            float rap =  (float)(_h)/(float)(_w);
            _rect = new Rect(_rect.left,_rect.top,_bmpBase.getWidth() + _rect.left,(int)(rap*_bmpBase.getWidth()) + _rect.top);
        }
        if(_rect.height()>_bmpBase.getHeight())
        {
            //Log.i("maj_rect","trop long");
            float rap = (float)(_w)/(float)(_h);
            _rect = new Rect(_rect.left,_rect.top,(int)(rap*_bmpBase.getHeight()) + _rect.left,_bmpBase.getHeight() + _rect.top);
        }
        if(_rect.left<0)
        {
            _rect = new Rect(0, _rect.top, _rect.right - _rect.left, _rect.bottom);
        }
        if(_rect.top<0)
        {
            _rect = new Rect(_rect.left,0,_rect.right,_rect.bottom - _rect.top);
        }
        if(_rect.right > _bmpBase.getWidth())
        {
            _rect = new Rect(_rect.left + (_bmpBase.getWidth() - _rect.right),_rect.top,_bmpBase.getWidth(),_rect.bottom);
        }
        if(_rect.bottom > _bmpBase.getHeight())
        {
            _rect = new Rect(_rect.left,_rect.top + (_bmpBase.getHeight() - _rect.bottom),_rect.right,_bmpBase.getHeight());
        }


        Bitmap bmpOri = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);

        if(_curBmp != null)
        {
            bmpOri = _curBmp.copy(_curBmp.getConfig(),true);
        }



        _curBmp = Bitmap.createBitmap(_bmp[_numCurBmp],_rect.left,_rect.top,_rect.width(),_rect.height());

        if(_rect.width() != _w )
        {
            _fZoom = (float)(_w)/(float)(_rect.width());
            //check voir si on ne fait que scroller: ne pas tout rezoomer
            Rect inter = new Rect();
            if(_rect.width()  != _ancRect.width())
            {
                inter.left = -2;
                inter.top = -2;
                inter.right = -1;
                inter.bottom = -1;
            }
            else
            {
                inter.left = Math.max(_rect.left,_ancRect.left) - _rect.left;
                inter.top = Math.max(_rect.top,_ancRect.top) - _rect.top;
                inter.right = Math.min(_rect.right,_ancRect.right) - _rect.left;
                inter.bottom  = Math.min(_rect.bottom,_ancRect.bottom) - _rect.top;

            }
            _curBmp = ImageEdit.zoomScr(bmpOri,_curBmp,_w,_h, inter,_context);

        }

        setImageBitmap(_curBmp);
    }

    //réinitialisation de l'afficahge au bitmap de base
    public void raz()
    {
        _rect = new Rect(0,0,_w,_h);
        majRect();
    }

    public void origine()
    {
        if(_numCurBmp == (_nbBmp-1))
        {
            for(int i =0;i<_numCurBmp;i++)
            {
                _bmp[i] = _bmp[i+1];
            }
        }
        _bmp[_numCurBmp] = _bmpBase.copy(_bmpBase.getConfig(),true);
        raz();
    }


    protected void zoom(double fZoom,float cx,float cy)
    {

        Log.i("bli","on zoome  de " + Double.toString(fZoom));

        /*setScaleX((float)fZoom);
        setScaleY((float) fZoom);*/


        //taille de la view: attention doit etre drawed au moins une fois sinon ca plante: valeur 0.
        //taille de l'image zoomée
        int nW = this.getWidth();
        int nH = this.getHeight();

        //Log.i("bli", Integer.toString(nW));



        //tableau de l'image zoomée
        int[] pix2 = new int[nW*nH];


        //création de l'image zoomée
        Bitmap bmp2 = createBitmap(nW,nH,_bmp[_numCurBmp].getConfig());

        //Taille du "bout" du bitmap de base sur lequel on va travailler : coma oon traitre pas toute l'image.
        int w = (int)Math.floor(nW/fZoom);
        int h = (int)Math.floor(nH/fZoom);
        Log.i("bli","c'est nickel1");

        //tableau du bout de bitmap de base
        int[] pix1 = new int[w*h];

        //on recreer un bitmap qui est la partie du bitmap de base sur laquelle on va zoomer.
        int posX,posY;
        posX = getScrollX();
        posY = getScrollY();
        //Log.i("bli","on a :" + Integer.toString(posX) + "," + Integer.toString(posY));

        posX =(int) Math.floor(_cX /*- w/2*/);
        posY = (int)Math.floor(_cY - h/2);

        //TODO: refaire les checks de coordonnes-

        posX = checkBound(posX,0,_curBmp.getWidth() - w);
        posY = checkBound(posY,0,_curBmp.getHeight() - h);

        Log.i("bli","on a " + Integer.toString(posX) + "," + Integer.toString(posY));
        Bitmap bmp3 = createBitmap(_bmp[_numCurBmp],posX,posY,w,h);


        //on recupere les pixels de l'image à zoomer
        bmp3.getPixels(pix1,0,w,0,0,w,h);

        //variables
        //les coordonnées du pixel en cours, puis les coordonnés dans l'ancienne image des pixels utilisée pour la création d'un nouveau pixel
        int x,y,x1,x2,x3,x4,y1,y2,y3,y4;


        //la distance du pixel en cours au pixel de base en haut à gauche de celui ci
        double dx,dy;


        //coord des derniers pixels de l'ancienne image directement copié
        //on boucle sur le nouveau tableau de pixels
        for(int i =0;i<pix2.length;i++)
        {
            //calcul des coordonnées du pixel à creer
            x = i%nW;
            y = i/nW;


            //récuperation des 4 pixels nécessaire pour le calcul de la moyenne
            //variables superfétatoires: uniquement pour la lisibilité
            x1 = (int) Math.floor(x/fZoom);
            x2 = x1+1;
            x3 = x1;
            x4 = x1+1;

            y1  = (int) Math.floor(y/fZoom);

            y2 = y1+1;
            y3 = y1;
            y4 = y1+1;

            dx = x/fZoom - x1;
            dy = y/fZoom - y1;

            //pour l'instant zoom bourrin
           // pix2[i] = pix1[_bmp.getWidth()*y1 + x1];

            //zoom avec interpolation
            //on verifie qu'on ne sort pas de l'image
            if(x4<w && y4<h)
            {
                if(i == 0)
                    pix2[i] = moyennePix(pix1[w*y1 + x1],pix1[w*y2 + x2],pix1[w*y3 + x3],pix1[w*y4 + x4],fZoom,dx,dy,true);
                else
                    pix2[i] = moyennePix(pix1[w*y1 + x1],pix1[w*y2 + x2],pix1[w*y3 + x3],pix1[w*y4 + x4],fZoom,dx,dy,false);

            }


        }

        bmp2.setPixels(pix2,0,nW,0,0,nW,nH);


        _curBmp = bmp2;
        setImageBitmap(bmp2);
        scrollTo(0,0);
        _isZoom = true;
        //setImageBitmap(bmp3);

        //setScaleType(ScaleType.CENTER);

    }


    //calcul de distance netre 2 points;
    protected double distance(float x1,float y1,float x2, float y2)
    {
        double dist = Math.sqrt( ((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)));
        return dist;
    }


    //calcul de la moyenne des differents composantes des pixels
    protected int moyennePix(int p1,int p2,int p3, int p4,double fZoom, double dx,double dy,boolean v)
    {

        int p;
        //distance à chaque pixel
        double d1,d2;

        int r,g,b;

        d1 = dx/fZoom;
        d2 = dy/fZoom;

        /*if(v)
            Log.i("bli",Double.toString(d1) + "," +Double.toString(d2) + "," +Double.toString(d3) + "," +Double.toString(d4) + "," +Double.toString(dT));
*/
        r = (int) (Math.floor((1-d1)*(1-d2)*Color.red(p1) + (1-d1)*(d2)*Color.red(p3) + (1-d2)*(d1)*Color.red(p2) + (d1)*(d2)*Color.red(p4)));
        g = (int) (Math.floor((1-d1)*(1-d2)*Color.green(p1) + (1-d1)*(d2)*Color.green(p3) + (1-d2)*(d1)*Color.green(p2) + (d1)*(d2)*Color.green(p4)));
        b = (int) (Math.floor((1-d1)*(1-d2)*Color.blue(p1) + (1-d1)*(d2)*Color.blue(p3) + (1-d2)*(d1)*Color.blue(p2) + (d1)*(d2)*Color.blue(p4)));
        p = Color.argb(255,r,g,b);

        return p;

    }


    //fonction d'annulation et de restauration
    public boolean annuler()
    {
        if(_numCurBmp>0)
        {
            _numCurBmp--;
        }
        majRect();
        if(_numCurBmp>0)
        {
            return true;
        }
        else
        {
            return false;
        }

    }


    public boolean restaurer()
    {
        if(_numCurBmp<_nbBmp-1)
        {
            _numCurBmp++;
        }
        majRect();
        if(_numCurBmp<_nbBmp-1)
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    protected int checkBound(int n,int min,int max)
    {
        int diff = 0;


        if(n<min)
        {
            diff = n-min;
            n = min;

        }
        else if(n> max)
        {
            diff =  n-max;
            n = max;
        }

        return n;
    }

    protected double checkBound(double n, double min, double max)
    {
        double diff = 0;

        if(n<min)
        {
            diff = n-min;
            n = min;
        }
        else if(n> max)
        {
            diff =  n-max;
            n = max;
        }

        return n;
    }

    //fonctions de modification de l'image



    //getters et setters

    public Bitmap get_bmp() {
        return _bmp[_numCurBmp];
    }

    public void set_bmp(Bitmap _bmp) {
        if(_numCurBmp == _nbBmp-1)
        {
            for(int i = 0;i<_numCurBmp;i++)
            {
                this._bmp[i] = this._bmp[i+1];
            }
        }
        else
        {
            _numCurBmp++;

        }
        this._bmp[_numCurBmp] = _bmp;
        raz();
    }




    public Bitmap get_bmpBase() {
        return _bmpBase;
    }

    public void set_bmpBase(Bitmap _bmpBase) {
        this._bmpBase = _bmpBase;
        origine();
    }


    //le contexte de l'application
    protected Context _context;

    //le bitmap de base
    protected Bitmap _bmpBase;
    //le bitmap modifié
    protected Bitmap[] _bmp;
    //le bitmap qu'on affiche
    protected Bitmap _curBmp;

    //le numero du bitmap que l'on affiche
    protected int _numCurBmp;

    //le nombre de bitmap maximum retenus
    protected final int _nbBmp = 10;




    //la taille de l'image
    protected int _w;
    protected int _h;

    //le rectangle que l'on affiche
    protected Rect _rect;
    protected Rect _ancRect;

    protected boolean _isInitialised = false;

    //le facteur de zoom: pas forcement le vrai
    protected float _fZoom = 1;

    //timestamp du sernier click
    protected long _lastClick;
    //coord des dernier touchs
    protected float _xS1;
    protected float _yS1;
    //2eme touch
    protected float _xS2;
    protected float _yS2;
    //coord du centre de zoom
    protected float _cX;
    protected float _cY;
    //distance de départ et de fin entre les 2 touch
    protected double _distanceS;
    protected double _distanceF;
    //TODO voir i ca sert toujours
    protected boolean _isZoom;


}
