package com.vicen.webel.components.wartish.hilos;

import android.os.AsyncTask;
import android.view.View;

import com.vicen.webel.components.wartish.dao.PersonajeDao;
import com.vicen.webel.components.wartish.activities.Experiencia;

import java.sql.SQLException;

/**
 * Created by Sala5 on 20/05/2016.
 */
public class HiloExperiencia extends AsyncTask<Void,Void,Void>{
    private Experiencia activity;

    public HiloExperiencia(Experiencia activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (activity.isEjecutar()){
            try {

                publishProgress();
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        activity.setExSum(activity.getExSum()+1);
        activity.setExSumTot(activity.getExSumTot()+1);
        if (activity.getExSum()>=activity.getExTot()){
            activity.setExSum(0);
            activity.setNivel(activity.getNivel()+1);
        }
        if (activity.getExSumTot()>=activity.getExp()){
            activity.setEjecutar(false);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.getBtnSalir().setVisibility(View.VISIBLE);
        try {
            PersonajeDao.actualizarNivel(activity,activity.getNivel());
            PersonajeDao.actualizarExperiencia(activity,activity.getExSum());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
