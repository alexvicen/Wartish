package com.vicen.webel.components.wartish.hilos;


import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vicen.webel.components.wartish.R;
import com.vicen.webel.components.wartish.activities.ProcesarMateriales;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;


public class HiloCrearElementoProcesa extends AsyncTask<Void,Void,Void>{

    private ProcesarMateriales activity;
    private RelativeLayout llJuego;
    private int ancho;
    private Random random = new Random();
    private ArrayList<ImageView> arrayList;
    private int tiempo=0,minutos,segundos;
    private boolean mat=true;

    public HiloCrearElementoProcesa(ProcesarMateriales activity,ArrayList<ImageView>arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (activity.isPlaying()&&minutos<1&&mat) {
            while(activity.isBucle()&&minutos<1&&mat){
                try {
                    publishProgress();
                    Thread.sleep(400);
                    tiempo+=400;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        llJuego = activity.getLlJuego();
        ancho=llJuego.getWidth();
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        segundos=tiempo/1000;
        if (segundos>=60){
            segundos=0;
            tiempo=0;
            minutos++;
        }
        DecimalFormat formato = new DecimalFormat("00");
        activity.getTxtTiempo().setText("01:00 - "+formato.format(minutos)+":"+formato.format(segundos));
        ImageView iv = new ImageView(activity);
        iv.setX(random.nextInt(ancho-90)+10);
        iv.setY(-40);

        int contador = 0;
        if (activity.getOro()>0){
            contador++;
        }
        if (activity.getRoca()>0){
            contador++;
        }
        if (activity.getTronco()>0){
            contador++;
        }
        if (activity.getGemaBruto()>0){
            contador++;
        }
        if (activity.getHierro()>0){
            contador++;
        }
        int material = random.nextInt(contador)+1;
        int b=0;
        if (activity.getRoca()>0&&material>0){
            material--;
            b=1;
        }
        if (activity.getTronco()>0&&material>0){
            material--;
            b=2;
        }
        if (activity.getGemaBruto()>0&&material>0){
            material--;
            b=3;
        }
        if (activity.getHierro()>0&&material>0){
            material--;
            b=4;
        }
        if (activity.getOro()>0&&material>0){
            b=5;
        }
        switch (b){
            case 1:
                iv.setBackgroundResource(R.drawable.roca);
                iv.setTag("roca");
                activity.setRoca(-1);
                break;
            case 2:
                iv.setBackgroundResource(R.drawable.troncos);
                iv.setTag("tronco");
                activity.setTronco(-1);
                break;
            case 3:
                iv.setBackgroundResource(R.drawable.gema_bruto);
                iv.setTag("gema_bruto");
                activity.setGemaBruto(-1);
                break;
            case 4:
                iv.setBackgroundResource(R.drawable.hierro);
                iv.setTag("hierro");
                activity.setHierro(-1);
                break;
            case 5:
                iv.setBackgroundResource(R.drawable.pepita);
                iv.setTag("pepita");
                activity.setOro(-1);
                break;
        }
        arrayList.add(iv);
        iv.setOnClickListener(activity);
        llJuego.addView(iv);
        if (activity.getRoca()==0&&activity.getTronco()==0&&activity.getGemaBruto()==0&&activity.getHierro()==0&&activity.getOro()==0){
            mat=false;
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
