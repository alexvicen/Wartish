package com.vicen.webel.components.wartish.hilos;

import android.os.AsyncTask;
import android.view.View;

import com.vicen.webel.components.wartish.activities.ProcesarMateriales;

public class HiloEliminarMaterial extends AsyncTask<Void,Void,Void>{

    private ProcesarMateriales activity;
    private View v,iv;

    public HiloEliminarMaterial(ProcesarMateriales activity,View v, View iv) {
        this.activity = activity;
        this.v = v;
        this.iv = iv;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(550);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        publishProgress();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        activity.getLlJuego().removeView(v);
        activity.getLlJuego().removeView(iv);
    }


}
