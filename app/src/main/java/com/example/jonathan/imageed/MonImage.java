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

import static android.R.attr.right;
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
        _bmpBase = ImageEdit.apercu(bmp,1500); //le bitmap de base:

        //creation du tableau de bitmap à retenir pour pouvoir annuler les modifications

        _bmp = new Bitmap[_nbBmp];
        _numCurBmp = 0;
        _nbRestau = 0;


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

                        _ancRect = _rect;
                        _rect = new Rect((int)(_cX - nW/2),(int)(_cY - nH/2),(int)(_cX + nW/2),(int)(_cY + nH/2));


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

        _rect = new Rect(0,0,_w,_h);
        _ancRect = new Rect(-1,-1,0,0);

        majRect();



        //initialisation du timer.
        _lastClick = 0;
    }

    public void majRect()
    {
        //d'abord on check les bord du rectangle pour qu'il ne dépasse pas


        if(_rect.width()>_bmpBase.getWidth())
        {
            float rap =  (float)(_h)/(float)(_w);
            _rect = new Rect(_rect.left,_rect.top,_bmpBase.getWidth() + _rect.left,(int)(rap*_bmpBase.getWidth()) + _rect.top);
        }
        if(_rect.height()>_bmpBase.getHeight())
        {
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


        Bitmap bmpPrec = Bitmap.createBitmap(_w,_h, Bitmap.Config.ARGB_8888);

        //Si l'on doit appliquer un zoom
        if(_rect.width() != _w)
        {

            Bitmap bmpOri = Bitmap.createBitmap(_bmp[_numCurBmp],_rect.left,_rect.top,_rect.width(),_rect.height());
            //on calcul le facteur de zoom: basé sur la largeure
            _fZoom = (float)(_w)/(float)(_rect.width());

            //check voir si on ne fait que scroller: ne pas tout rezoomer
            //
            Rect recDest = new Rect();
            Rect inter = new Rect();
            //si l'on doit changer de facteur de zoom on recalcul tout
            if(_rect.width()  != _ancRect.width()|| true)
            {
                recDest.left = -2;
                recDest.top = -2;
                recDest.right = -1;
                recDest.bottom = -1;

                _curBmp = ImageEdit.zoomScr(bmpPrec,bmpOri,_w,_h, recDest,_context);
            }
            //sinon on ne realcul que la bande à rezoomer.
           /* else
            {
                //on prend l'intersection des 2 rectangles: l'ancien et le nouveau
                boolean b = inter.setIntersect(_rect,_ancRect);



                //le rectangle à copier du bitmap précédent
                Rect recCop = new Rect();
                recCop.left = inter.left - _ancRect.left;
                recCop.top = inter.top - _ancRect.top;
                recCop.right = recCop.left + (int)Math.floor(inter.width() * _fZoom);
                recCop.bottom = recCop.top + (int)Math.floor(inter.height() * _fZoom);


                //le rectangle de destination
                recDest.left = inter.left - _rect.left;
                recDest.top = inter.top - _rect.top;
                recDest.right = recDest.left + recCop.width();
                recDest.bottom = recDest.top + recCop.height();


                Log.i("rectangle","ancien:" + recCop.left + "," + recCop.top + "," + recCop.right + "," + recCop.bottom + ": nouveau:" + recDest.left + "," + recDest.top + "," + recDest.right + "," + recDest.bottom + ": inter:" + inter.left + "," + inter.top + "," + inter.right + "," + inter.bottom);

                //test
                bmpPrec = Bitmap.createBitmap(_w,_h, Bitmap.Config.ARGB_8888);
                Canvas can = new Canvas(bmpPrec);
                can.drawBitmap(_curBmp,recCop,recDest,null);
                //_curBmp = bmpPrec;
                _curBmp = ImageEdit.zoomScr(bmpPrec,bmpOri,_w,_h, recDest,_context);
                //test

            }*/


            /*Le fait de ne recalculer que la bande nouvelle ne fonctionne pas car pour des questions d'arrondis dans l'utilisation du
            script RS: cela donne un effet décalé à l'image zoomée.



            */


        }
        else
        {

            _curBmp = Bitmap.createBitmap(_bmp[_numCurBmp],_rect.left,_rect.top,_rect.width(),_rect.height());

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


    //calcul de distance netre 2 points;
    protected double distance(float x1,float y1,float x2, float y2)
    {
        double dist = Math.sqrt( ((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)));
        return dist;
    }


    /**
     *
     * @return Un appercu du bitmap pour pouvoir prévisualiser les modifications
     */
    public Bitmap apercu(int tailleM)
    {
        return ImageEdit.apercu(_bmp[_numCurBmp],tailleM);


    }


    //fonction d'annulation et de restauration
    public boolean annuler()
    {
        if(_numCurBmp>0)
        {
            _numCurBmp--;
            _nbRestau++;
            majRect();
            return true;
        }
        else
        {
            return false;
        }

    }


    public boolean restaurer()
    {
        if(_nbRestau>0)
        {
            _numCurBmp++;
            _nbRestau--;
            majRect();
            return true;
        }
        else
        {
            return false;
        }

    }




    //getters et setters

    public Bitmap get_bmp() {
        return _bmp[_numCurBmp];
    }

    public void set_bmp(Bitmap bmp) {
        Bitmap bmp2 = ImageEdit.apercu(bmp,1500);
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
        this._bmp[_numCurBmp] = bmp2;
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

    //le nombr;e de restauration possible
    protected int _nbRestau;




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
    

}
