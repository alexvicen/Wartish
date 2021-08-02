package com.vicen.webel.components.wartish.hilos;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.vicen.webel.components.wartish.dao.PersonajeDao;
import com.vicen.webel.components.wartish.activities.Index;
import com.vicen.webel.components.wartish.activities.TresLinea;

import java.sql.SQLException;
import java.util.Random;

public class HiloJuego extends AsyncTask<Void,Void,Void>{
    private TresLinea activity;
    private int puntos;
    private int arraynum [][];
    private ImageView arrayimg [][];
    private Random r = new Random();

    public HiloJuego(TresLinea activity, int puntos, int[][] arraynum, ImageView[][] arrayimg) {
        this.activity = activity;
        this.puntos = puntos;
        this.arraynum = arraynum;
        this.arrayimg = arrayimg;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (activity.getPuntos()>0){

        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            PersonajeDao.actualizarRoca(activity,activity.getPunRoca());
            PersonajeDao.actualizarTronco(activity,activity.getPunTronco());
            PersonajeDao.actualizarHierro(activity,activity.getPunHierro());
            PersonajeDao.actualizarPepita(activity,activity.getPunPepita());
            PersonajeDao.actualizarGemaBruto(activity,activity.getPunGemaBruto());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(activity, Index.class);
        activity.startActivity(i);
        activity.finish();
    }

    public boolean comprobarHorizontal(){
        int contadorHoriz = 0;
        for (int i = 0;i<arraynum.length;i++) {
            for (int j = 1; j < arraynum[i].length; j++) {
                if (arraynum[i][j - 1] == arraynum[i][j]) {
                    contadorHoriz++;
                    if (contadorHoriz == 2) {
                        contadorHoriz = 0;
                        if (activity.isPlay()){
                            sumarPuntos(arraynum[i][j]);
                            activity.setCombo(activity.getCombo()+1);
                        }
                        activity.cambiarPosicion(arrayimg[i][j - 1],arrayimg[r.nextInt(arraynum.length)][r.nextInt(arraynum.length)]);
                        activity.cambiarPosicion(arrayimg[i][j],arrayimg[r.nextInt(arraynum.length)][r.nextInt(arraynum.length)]);
                        activity.cambiarPosicion(arrayimg[i][j], arrayimg[r.nextInt(arraynum.length)][r.nextInt(arraynum.length)]);
                        return true;
                    }
                } else {
                    contadorHoriz = 0;
                }
            }
        }
        return comprobarVertical();
    }
    public boolean comprobarVertical(){
        int contadorVertical=0;
        for (int i = 0; i < arraynum.length; i++) {
            for (int j = 1; j < arraynum[i].length; j++) {
                if (arraynum[j - 1][i] == arraynum[j][i]) {
                    contadorVertical++;
                    if (contadorVertical == 2) {
                        contadorVertical = 0;
                        if (activity.isPlay()){
                            sumarPuntos(arraynum[j][i]);
                            activity.setCombo(activity.getCombo()+1);
                        }
                        activity.cambiarPosicion(arrayimg[j - 1][i],arrayimg[r.nextInt(arraynum.length)][r.nextInt(arraynum.length)]);
                        activity.cambiarPosicion(arrayimg[j][i],arrayimg[r.nextInt(arraynum.length)][r.nextInt(arraynum.length)]);
                        activity.cambiarPosicion(arrayimg[j][i], arrayimg[r.nextInt(arraynum.length)][r.nextInt(arraynum.length)]);
                        return true;
                    }
                } else {
                    contadorVertical = 0;
                }
            }
        }
        return false;
    }
    public void sumarPuntos(int t){
        if (t==0){
            activity.setPunRoca(activity.getPunRoca()+3);
        }else if (t==1){
            activity.setPunTronco(activity.getPunTronco()+3);
        }else if (t==2){
            activity.setPunHierro(activity.getPunHierro()+3);
        }else if (t==3){
            activity.setPunPepita(activity.getPunPepita()+3);
        }else if (t==4){
            activity.setPunGemaBruto(activity.getPunGemaBruto()+3);
        }
    }


}
