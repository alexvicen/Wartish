package com.vicen.webel.components.wartish.hilos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.vicen.webel.components.wartish.R;
import com.vicen.webel.components.wartish.activities.JuegoPrincipal;

import java.util.Random;


public class HiloAtaqueEnemigos extends AsyncTask<Void,Integer,Void>{

    private JuegoPrincipal activity;
    private ImageView iv1,iv2,iv3,ivPer;
    private float metaX,metaY,aumX1,x1,y1,x2,aumX3,x3,y3;
    private ImageView flecha1;
    private ImageView flecha2;
    private ImageView flecha3;
    private int movimiento1,movimiento2,movimiento3;
    private AnimationDrawable animacion1,animacion2,animacion3;
    private Random r = new Random();

    public HiloAtaqueEnemigos(JuegoPrincipal activity, ImageView iv1, ImageView iv2, ImageView iv3, ImageView ivPer) {
        this.activity = activity;
        this.iv1 = iv1;
        this.iv2 = iv2;
        this.iv3 = iv3;
        this.ivPer = ivPer;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (activity.isEncendido()) {
            try {
                Thread.sleep(2000);
                publishProgress(0);
                Thread.sleep(900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (activity.isAtaque()) {
                try {
                    publishProgress(1);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(2);

        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        switch (values[0]){
            case 0:
                if (activity.getEsqueleto1().getVida()>0) {
                    flecha1 = new ImageView(activity);
                    flecha1.setX(iv1.getX());
                    flecha1.setY(iv1.getY());
                    if (r.nextInt(20) == 10) {
                        flecha1.setImageResource(R.drawable.esqueletoflechaespecial);
                        flecha1.setTag("f1especial");
                    } else {
                        flecha1.setImageResource(R.drawable.esqueletoflecha);
                        flecha1.setTag("f1");
                    }
                    activity.getRl().addView(flecha1);
                    flecha1.setVisibility(View.INVISIBLE);
                    x1=flecha1.getX();
                    y1=flecha1.getY();
                    aumX1=(iv1.getX()-ivPer.getX())/(ivPer.getY()-iv1.getY());
                    movimiento1 = R.drawable.ataque_esqueleto;
                    iv1.setBackgroundResource(0);
                    iv1.setBackgroundResource(movimiento1);
                    animacion1 = (AnimationDrawable) iv1.getBackground();
                    animacion1.start();
                }else if (activity.isEnem1()){
                    movimiento1 = R.drawable.golpe_esqueleto;
                    iv1.setBackgroundResource(0);
                    iv1.setBackgroundResource(movimiento1);
                    animacion1 = (AnimationDrawable) iv1.getBackground();
                    animacion1.start();
                    activity.setEnem1(false);
                }
                if (activity.getEsqueleto2().getVida()>0) {
                    flecha2 = new ImageView(activity);
                    flecha2.setX(iv2.getX());
                    flecha2.setY(iv2.getY());
                    if (r.nextInt(20) == 10) {
                        flecha2.setImageResource(R.drawable.esqueletoflechaespecial);
                        flecha2.setTag("f2especial");
                    } else {
                        flecha2.setImageResource(R.drawable.esqueletoflecha);
                        flecha2.setTag("f2");
                    }
                    activity.getRl().addView(flecha2);
                    flecha2.setVisibility(View.INVISIBLE);
                    x2=flecha2.getX();
                    movimiento2 = R.drawable.ataque_esqueleto;
                    iv2.setBackgroundResource(0);
                    iv2.setBackgroundResource(movimiento2);
                    animacion2 = (AnimationDrawable) iv2.getBackground();
                    animacion2.start();
                }else if (activity.isEnem2()){
                    movimiento2 = R.drawable.golpe_esqueleto;
                    iv2.setBackgroundResource(0);
                    iv2.setBackgroundResource(movimiento2);
                    animacion2 = (AnimationDrawable) iv2.getBackground();
                    animacion2.start();
                    activity.setEnem2(false);
                }
                if (activity.getEsqueleto3().getVida()>0) {
                    flecha3 = new ImageView(activity);
                    flecha3.setX(iv3.getX());
                    flecha3.setY(iv3.getY());
                    if (r.nextInt(20) == 10) {
                        flecha3.setImageResource(R.drawable.esqueletoflechaespecial);
                        flecha3.setTag("f3especial");
                    } else {
                        flecha3.setImageResource(R.drawable.esqueletoflecha);
                        flecha3.setTag("f3");
                    }
                    activity.getRl().addView(flecha3);
                    flecha3.setVisibility(View.INVISIBLE);
                    x3=flecha3.getX();
                    y3=flecha3.getY();
                    aumX3=(iv3.getX()-ivPer.getX())/(ivPer.getY()-iv3.getY());
                    movimiento3 = R.drawable.ataque_esqueleto;
                    iv3.setBackgroundResource(0);
                    iv3.setBackgroundResource(movimiento3);
                    animacion3 = (AnimationDrawable) iv3.getBackground();
                    animacion3.start();
                }else if (activity.isEnem3()){
                    movimiento3 = R.drawable.golpe_esqueleto;
                    iv3.setBackgroundResource(0);
                    iv3.setBackgroundResource(movimiento3);
                    animacion3 = (AnimationDrawable) iv3.getBackground();
                    animacion3.start();
                    activity.setEnem3(false);
                }
                break;
            case 1:
                if (flecha1!=null) {
                    flecha1.setVisibility(View.VISIBLE);
                    if (flecha1.getX()>ivPer.getX()){
                        x1+=aumX1;
                        y1-=1;
                        flecha1.setX(x1);
                        flecha1.setY(y1);
                    }else{
                        activity.setAtaque(false);
                    }
                }
                if (flecha2!=null) {
                    flecha2.setVisibility(View.VISIBLE);
                    if (flecha2.getX()>ivPer.getX()){
                        x2+=aumX1;
                        flecha2.setX(x2);
                    }else{
                        if (activity.getEsqueleto1().getVida()<=0&&activity.getEsqueleto3().getVida()<=0){
                            activity.setAtaquePer(false);
                        }
                    }
                }
                if (flecha3!=null) {
                    flecha3.setVisibility(View.VISIBLE);
                    if (flecha3.getX()>ivPer.getX()){
                        x3-=aumX3;
                        y3+=1;
                        flecha3.setX(x3);
                        flecha3.setY(y3);
                    }else{
                        activity.setAtaque(false);
                    }
                }

                break;
            case 2:
                if (activity.getEsqueleto1().getVida()>0||flecha1!=null) {
                    if (flecha1.getTag() == "f1") {
                        activity.bajarVida(activity.getEsqueleto1());
                    } else if (flecha1.getTag() == "f1especial") {
                        activity.bajarVidaCritico(activity.getEsqueleto1());
                    }
                    activity.getRl().removeView(flecha1);
                }
                if (activity.getEsqueleto2().getVida()>0||flecha2!=null) {
                    if (flecha2.getTag() == "f2") {
                        activity.bajarVida(activity.getEsqueleto2());
                    } else if (flecha2.getTag() == "f2especial") {
                        activity.bajarVidaCritico(activity.getEsqueleto2());
                    }
                    activity.getRl().removeView(flecha2);
                }
                if (activity.getEsqueleto3().getVida()>0||flecha3!=null) {
                    if (flecha3.getTag() == "f3") {
                        activity.bajarVida(activity.getEsqueleto3());
                    } else if (flecha3.getTag() == "f3especial") {
                        activity.bajarVidaCritico(activity.getEsqueleto3());
                    }
                    activity.getRl().removeView(flecha3);
                }
                flecha1=null;
                flecha2=null;
                flecha3=null;
                if (activity.getEsqueleto1().getVida()>0||activity.getEsqueleto2().getVida()>0||activity.getEsqueleto3().getVida()>0) {
                    activity.setAtaque(true);
                }
                break;
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent in = new Intent();
        int[]array={activity.getEsqueleto1().getNivel(),activity.getEsqueleto2().getNivel(),activity.getEsqueleto3().getNivel()};
        in.putExtra("nivel",array);
        activity.setResult(Activity.RESULT_OK,in);
        activity.finish();
    }
}
