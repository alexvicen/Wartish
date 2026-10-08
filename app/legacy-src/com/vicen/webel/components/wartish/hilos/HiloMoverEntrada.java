package com.vicen.webel.components.wartish.hilos;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.vicen.webel.components.wartish.activities.JuegoPrincipal;


public class HiloMoverEntrada extends AsyncTask<Void,Void,Void>{
    private JuegoPrincipal activity;
    private int ancho,ancho1=0;
    private ImageView iv1,iv2,iv3,ivPer;

    public HiloMoverEntrada(JuegoPrincipal activity, ImageView iv1, ImageView iv2, ImageView iv3, ImageView ivPer) {
        this.activity = activity;
        this.iv1 = iv1;
        this.iv2 = iv2;
        this.iv3 = iv3;
        this.ivPer = ivPer;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (activity.isEncendido()){
            try {
                Thread.sleep(40);
                publishProgress();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ancho = activity.getRl().getWidth();

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.setEncendido(true);
        HiloAtaqueEnemigos ha = new HiloAtaqueEnemigos(activity,iv1,iv2,iv3,ivPer);
        activity.getT().execute(ha);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (ancho>=activity.getRl().getWidth()/2){
            ancho -= 5;
        }
        if (ancho1<=activity.getRl().getWidth()/6){
            ancho1+=5;
        }
        if (ivPer.getX()<=activity.getRl().getWidth()/6){
            ivPer.setX(ancho1);
        }else{
            activity.getAnimacion4().stop();
        }
        if (iv1.getX()<=activity.getRl().getWidth()/1.5){
            activity.getAnimacion1().stop();

        }else{
            iv1.setX(ancho);
        }
        if (iv2.getX()<=activity.getRl().getWidth()/1.8){
            activity.getAnimacion2().stop();
            activity.setEncendido(false);
            activity.getBtnSimple().setVisibility(View.VISIBLE);
            activity.getBtnEspecial().setVisibility(View.VISIBLE);
            activity.getBtnEspecial().setAlpha(0.5f);
            activity.getBtnEspecial().setClickable(false);
            activity.getBtnVolver().setVisibility(View.VISIBLE);
            activity.getTxtVida().setVisibility(View.VISIBLE);
            activity.getTxt1().setVisibility(View.VISIBLE);
            activity.getTxt2().setVisibility(View.VISIBLE);
            activity.getTxt3().setVisibility(View.VISIBLE);
            activity.getTxtNivelPersonaje().setVisibility(View.VISIBLE);
            activity.getTxtNivelEsqueleto1().setVisibility(View.VISIBLE);
            activity.getTxtNivelEsqueleto2().setVisibility(View.VISIBLE);
            activity.getTxtNivelEsqueleto3().setVisibility(View.VISIBLE);
            activity.getPbVida().setVisibility(View.VISIBLE);
            activity.getPb1().setVisibility(View.VISIBLE);
            activity.getPb2().setVisibility(View.VISIBLE);
            activity.getPb3().setVisibility(View.VISIBLE);
        }else{
            iv2.setX(ancho);
        }
        if (iv3.getX()<=activity.getRl().getWidth()/1.5){
            activity.getAnimacion3().stop();
        }else{
            iv3.setX(ancho);
        }
    }
}
