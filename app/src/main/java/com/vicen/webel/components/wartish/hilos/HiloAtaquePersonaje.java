package com.vicen.webel.components.wartish.hilos;

import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.vicen.webel.components.wartish.R;
import com.vicen.webel.components.wartish.activities.JuegoPrincipal;

import java.util.Random;


public class HiloAtaquePersonaje extends AsyncTask<Void,Integer,Void>{
    private JuegoPrincipal activity;
    private ImageView iv1,iv2,iv3,ivPer;
    private float metaX1,metaX3,x,x1,x2,x3,y,y1,y3,aumX1,aumX3,vel;
    private ImageView flechaPer1;
    private ImageView flechaPer2;
    private ImageView flechaPer3;
    private int movimientoPer;
    private AnimationDrawable animacionPer;
    private Random r = new Random();
    private ImageView v1 ;
    private ImageView v2;
    private ImageView v3;

    public HiloAtaquePersonaje(JuegoPrincipal activity, ImageView iv1, ImageView iv2, ImageView iv3, ImageView ivPer) {
        this.activity = activity;
        this.iv1 = iv1;
        this.iv2 = iv2;
        this.iv3 = iv3;
        this.ivPer = ivPer;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (activity.isEncendido()) {
            try {
                if (activity.isAtaqueEsp()) {
                    publishProgress(3);
                } else {
                    publishProgress(0);
                }
                Thread.sleep(900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (activity.isAtaqueEsp()) {
                try {
                    publishProgress(4);
                    Thread.sleep(50);
                    publishProgress(5);
                    Thread.sleep(50);
                    publishProgress(6);
                    Thread.sleep(50);
                    publishProgress(7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!activity.isAtaqueEsp()) {
                while (activity.isAtaquePer()) {
                    try {
                        publishProgress(1);
                        Thread.sleep(7);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                publishProgress(2);
                activity.setAtaquePer(true);
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.getBtnEspecial().setVisibility(View.INVISIBLE);
        activity.getBtnSimple().setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        switch (values[0]){
            case 0:
                if(activity.getEsqueleto1().getVida()>0){
                    flechaPer1 = new ImageView(activity);
                    flechaPer1.setX(ivPer.getX());
                    flechaPer1.setY(ivPer.getY());
                    if (r.nextInt(20)<5){
                        flechaPer1.setImageResource(R.drawable.personajeflechaespecial);
                        flechaPer1.setTag("fp1especial");
                    }else {
                        flechaPer1.setImageResource(R.drawable.personajeflecha);
                        flechaPer1.setTag("fp1");
                    }
                    activity.getRl().addView(flechaPer1);
                    flechaPer1.setVisibility(View.INVISIBLE);
                }

                if(activity.getEsqueleto2().getVida()>0){
                    flechaPer2 = new ImageView(activity);
                    flechaPer2.setX(ivPer.getX());
                    flechaPer2.setY(ivPer.getY());
                    if (r.nextInt(20)<5){
                        flechaPer2.setImageResource(R.drawable.personajeflechaespecial);
                        flechaPer2.setTag("fp2especial");
                    }else {
                        flechaPer2.setImageResource(R.drawable.personajeflecha);
                        flechaPer2.setTag("fp2");
                    }
                    activity.getRl().addView(flechaPer2);
                    flechaPer2.setVisibility(View.INVISIBLE);
                }

                if(activity.getEsqueleto3().getVida()>0){
                    flechaPer3 = new ImageView(activity);
                    flechaPer3.setX(ivPer.getX());
                    flechaPer3.setY(ivPer.getY());
                    if (r.nextInt(20)==10){
                        flechaPer3.setImageResource(R.drawable.personajeflechaespecial);
                        flechaPer3.setTag("fp3especial");
                    }else {
                        flechaPer3.setImageResource(R.drawable.personajeflecha);
                        flechaPer3.setTag("fp3");
                    }
                    activity.getRl().addView(flechaPer3);
                    flechaPer3.setVisibility(View.INVISIBLE);
                }

                vel=activity.getPersonaje().getVelocidad()/2;
                x=ivPer.getX();
                y=ivPer.getY();
                x1=x;
                x2=x;
                x3=x;
                y1=y;
                y3=y;

                aumX1=(iv1.getX()-ivPer.getX())/(iv1.getY()-ivPer.getY());
                aumX3=(iv3.getX()-ivPer.getX())/(ivPer.getY()-iv3.getY());
                movimientoPer = R.drawable.ataque_chico;
                ivPer.setBackgroundResource(0);
                ivPer.setBackgroundResource(movimientoPer);
                animacionPer = (AnimationDrawable) ivPer.getBackground();
                animacionPer.start();

                break;
            case 1:
                if (activity.getEsqueleto1().getVida()>0){
                    if (flechaPer1.getX()<iv1.getX()){
                        flechaPer1.setVisibility(View.VISIBLE);
                        x1+=aumX1;
                        y1+=1;
                        flechaPer1.setX(x1);
                        flechaPer1.setY(y1);
                    }else{
                        activity.setAtaquePer(false);
                    }
                }
                if (activity.getEsqueleto2().getVida()>0){
                    if (flechaPer2.getX()<iv2.getX()){
                        flechaPer2.setVisibility(View.VISIBLE);
                        x2+=aumX1;
                        flechaPer2.setX(x2);
                    }else{
                        if (activity.getEsqueleto1().getVida()<=0&&activity.getEsqueleto3().getVida()<=0){
                            activity.setAtaquePer(false);
                        }
                    }
                }
                if (activity.getEsqueleto3().getVida()>0){
                    if (flechaPer3.getX()<iv3.getX()){
                        flechaPer3.setVisibility(View.VISIBLE);
                        x3+=aumX3;
                        y3-=1;
                        flechaPer3.setX(x3);
                        flechaPer3.setY(y3);
                    }else{
                        activity.setAtaquePer(false);
                    }
                }

                break;
            case 2:
                if(activity.getEsqueleto1().getVida()>0) {
                    if (flechaPer1.getTag() == "fp1") {
                        activity.bajarVidaEnemigo(activity.getEsqueleto1());
                    } else if (flechaPer1.getTag() == "fp1especial") {
                        activity.bajarVidaEnemigoCritico(activity.getEsqueleto1());
                    }
                    activity.getRl().removeView(flechaPer1);
                }
                if(activity.getEsqueleto2().getVida()>0) {
                    if (flechaPer2.getTag() == "fp2") {
                        activity.bajarVidaEnemigo(activity.getEsqueleto2());
                    } else if (flechaPer2.getTag() == "fp2especial") {
                        activity.bajarVidaEnemigoCritico(activity.getEsqueleto2());
                    }
                    activity.getRl().removeView(flechaPer2);
                }
                if(activity.getEsqueleto3().getVida()>0) {
                    if (flechaPer3.getTag() == "fp3") {
                        activity.bajarVidaEnemigo(activity.getEsqueleto3());
                    } else if (flechaPer3.getTag() == "fp3especial") {
                        activity.bajarVidaEnemigoCritico(activity.getEsqueleto3());
                    }
                    activity.getRl().removeView(flechaPer3);
                }
                if (activity.getEsqueleto1().getVida()<=0&&activity.getEsqueleto2().getVida()<=0&&activity.getEsqueleto3().getVida()<=0){
                    activity.setEncendido(false);
                    activity.setAtaquePer(false);
                }
                break;
            case 3:
                movimientoPer = R.drawable.ataque_especial_chico;
                ivPer.setBackgroundResource(0);
                ivPer.setBackgroundResource(movimientoPer);
                animacionPer = (AnimationDrawable) ivPer.getBackground();
                animacionPer.start();
                break;
            case 4:
                if(activity.getEsqueleto1().getVida()>0){
                    v1 = new ImageView(activity);
                    v1.setX(iv1.getX()+50);
                    v1.setY(iv1.getY()-300);
                    v1.setBackgroundResource(R.drawable.rayo);
                    v1.setAlpha(0.5f);
                    activity.getRl().addView(v1);
                }
                if(activity.getEsqueleto2().getVida()>0){
                    v2 = new ImageView(activity);
                    v2.setX(iv2.getX()+50);
                    v2.setY(iv2.getY()-300);
                    v2.setBackgroundResource(R.drawable.rayo);
                    activity.getRl().addView(v2);
                }
                if(activity.getEsqueleto3().getVida()>0){
                    v3 = new ImageView(activity);
                    v3.setX(iv3.getX()+50);
                    v3.setY(iv3.getY()-300);
                    v3.setBackgroundResource(R.drawable.rayo);
                    v3.setAlpha(0.5f);
                    activity.getRl().addView(v3);
                }
                break;
            case 5:
                if(activity.getEsqueleto1().getVida()>0){
                    v1.setAlpha(1f);
                }
                if(activity.getEsqueleto2().getVida()>0){
                    v2.setAlpha(0.5f);
                }
                if(activity.getEsqueleto3().getVida()>0){
                    v3.setAlpha(1f);
                }
                break;
            case 6:
                if(activity.getEsqueleto1().getVida()>0){
                    v1.setAlpha(0.5f);
                }
                if(activity.getEsqueleto2().getVida()>0){
                    v2.setAlpha(1f);
                }
                if(activity.getEsqueleto3().getVida()>0){
                v3.setAlpha(0.5f);
                }
                break;
            case 7:
                if(activity.getEsqueleto1().getVida()>0){
                    activity.getRl().removeView(v1);
                    activity.bajarVidaEnemigoEspecial(activity.getEsqueleto1());
                }else{
                    activity.setAtaquePer(false);
                }
                if(activity.getEsqueleto2().getVida()>0){
                    activity.getRl().removeView(v2);
                    activity.bajarVidaEnemigoEspecial(activity.getEsqueleto2());
                }
                if(activity.getEsqueleto3().getVida()>0){
                    activity.getRl().removeView(v3);
                    activity.bajarVidaEnemigoEspecial(activity.getEsqueleto3());
                }
                if (activity.getEsqueleto1().getVida()<=0&&activity.getEsqueleto2().getVida()<=0&&activity.getEsqueleto3().getVida()<=0){
                    activity.setEncendido(false);
                    activity.setAtaquePer(false);
                }
                break;
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.getBtnEspecial().setVisibility(View.VISIBLE);
        activity.getBtnSimple().setVisibility(View.VISIBLE);
        if(activity.getEsqueleto1().getVida()>0) {
            activity.getRl().removeView(flechaPer1);
        }
        if(activity.getEsqueleto2().getVida()>0) {
            activity.getRl().removeView(flechaPer2);
        }
        if(activity.getEsqueleto3().getVida()>0) {
            activity.getRl().removeView(flechaPer3);
        }
    }
}
