package com.vicen.webel.components.wartish.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vicen.webel.components.wartish.R;
import com.vicen.webel.components.wartish.dao.PersonajeDao;
import com.vicen.webel.components.wartish.entidades.Personaje;
import com.vicen.webel.components.wartish.hilos.HiloBajaProcesa;
import com.vicen.webel.components.wartish.hilos.HiloCrearElementoProcesa;
import com.vicen.webel.components.wartish.hilos.HiloEliminarMaterial;
import com.vicen.webel.components.wartish.hilos.TaskHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;


public class ProcesarMateriales extends AppCompatActivity implements View.OnClickListener{
    private TextView txtRoca,txtTronco,txtHierro,txtOro,txtGemaBruto,txtPiedra,txtTablasMadera,txtLingoteHierro,txtLingoteOro,txtGema;
    private int roca=0,tronco=0,hierro=0,oro=0,gemaBruto=0,piedra=0,tablasMadera=0,lingoteHierro=0,lingoteOro=0,gema=0;
    private TextView txtTiempo;
    private ImageView ivFuego,ivCintaTransportadora;
    private Button btnJugar,btnPausa,btnSalir;
    private Personaje personaje;
    private RelativeLayout llJuego;
    private Boolean play = true,bucle=true;
    private ArrayList<ImageView> arrayList = new ArrayList<>();
    private HiloCrearElementoProcesa hcep;
    private HiloBajaProcesa hbp;
    private HiloEliminarMaterial hem;
    private Random random = new Random();

    private AnimationDrawable animacion1 = new AnimationDrawable();
    private AnimationDrawable animacion2 = new AnimationDrawable();
    private AnimationDrawable animacion3 = new AnimationDrawable();
    private AnimationDrawable animacion4 = new AnimationDrawable();
    private int movimiento1,movimiento2,movimiento3,movimiento4,contador=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.procesar_materiales);
        txtRoca = (TextView)findViewById(R.id.txtRoca);
        txtTronco = (TextView)findViewById(R.id.txtTronco);
        txtHierro = (TextView)findViewById(R.id.txtHierro);
        txtOro= (TextView)findViewById(R.id.txtOro);
        txtGemaBruto = (TextView)findViewById(R.id.txtGemaBruto);
        txtPiedra = (TextView)findViewById(R.id.txtPiedra);
        txtTablasMadera = (TextView)findViewById(R.id.txtTablasMadera);
        txtLingoteHierro = (TextView)findViewById(R.id.txtLingoteHierro);
        txtLingoteOro = (TextView)findViewById(R.id.txtLingoteOro);
        txtGema = (TextView)findViewById(R.id.txtGema);
        btnJugar = (Button)findViewById(R.id.btnJugar);
        btnPausa = (Button)findViewById(R.id.btnPausa);
        btnSalir = (Button)findViewById(R.id.btnSalir);
        llJuego = (RelativeLayout)findViewById(R.id.llJuego);
        txtTiempo=(TextView)findViewById(R.id.txtTiempo);
        ivFuego = (ImageView)findViewById(R.id.ivFuego);
        ivCintaTransportadora=(ImageView)findViewById(R.id.ivCintaTransportadora);
        btnJugar.setOnClickListener(this);
        btnPausa.setOnClickListener(this);
        btnSalir.setOnClickListener(this);
        btnPausa.setVisibility(View.GONE);
        movimiento2 = R.drawable.fuego;
        ivFuego.setBackgroundResource(movimiento2);
        animacion2 = (AnimationDrawable) ivFuego.getBackground();
        animacion2.start();
        movimiento3 = R.drawable.fondo_cinta;
        ivCintaTransportadora.setBackgroundResource(movimiento3);
        animacion3 = (AnimationDrawable) ivCintaTransportadora.getBackground();
        animacion3.start();
        try {
            personaje = PersonajeDao.buscarPersonaje(this);
            roca=personaje.getRoca();
            txtRoca.setText(String.valueOf(roca));
            tronco=personaje.getTronco();
            txtTronco.setText(String.valueOf(tronco));
            hierro=personaje.getHierro();
            txtHierro.setText(String.valueOf(hierro));
            oro=personaje.getPepita();
            txtOro.setText(String.valueOf(oro));
            gemaBruto=personaje.getGema_bruto();
            txtGemaBruto.setText(String.valueOf(gemaBruto));

            piedra=personaje.getPiedra();
            txtPiedra.setText(String.valueOf(piedra));
            tablasMadera=personaje.getTabla_madera();
            txtTablasMadera.setText(String.valueOf(tablasMadera));
            lingoteHierro=personaje.getLingote_hierro();
            txtLingoteHierro.setText(String.valueOf(lingoteHierro));
            lingoteOro=personaje.getLingote_oro();
            txtLingoteOro.setText(String.valueOf(lingoteOro));
            gema=personaje.getGema();
            txtGema.setText(String.valueOf(gema));
        } catch (SQLException e) {
            Toast.makeText(ProcesarMateriales.this, "Fallo al coger personaje", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnJugar) {
            if (contador == 1) {
                contador++;
                hcep = new HiloCrearElementoProcesa(this, arrayList);
                TaskHelper.execute(hcep);
                hbp = new HiloBajaProcesa(this, arrayList);
                TaskHelper.execute(hbp);
                btnJugar.setVisibility(View.INVISIBLE);
                btnJugar.setEnabled(false);
                btnPausa.setVisibility(View.VISIBLE);
            } else {
                btnJugar.setVisibility(View.INVISIBLE);
                btnJugar.setEnabled(false);
                btnPausa.setVisibility(View.VISIBLE);
                btnSalir.setVisibility(View.GONE);
                for (int i = 0; i <arrayList.size() ; i++) {
                    arrayList.get(i).setClickable(true);
                    arrayList.get(i).setAlpha(1f);
                }
                bucle = true;
            }
        }else if (v.getId()==R.id.btnPausa) {
            bucle = false;
            btnJugar.setText("Continuar");
            btnJugar.setVisibility(View.VISIBLE);
            btnJugar.setEnabled(true);
            btnPausa.setVisibility(View.GONE);
            btnSalir.setVisibility(View.VISIBLE);
            for (int i = 0; i <arrayList.size() ; i++) {
                arrayList.get(i).setClickable(false);
                arrayList.get(i).setAlpha(0.5f);
            }
        }else if (v.getId()==R.id.btnSalir){
            play = false;
            bucle = false;
        }else{
            ImageView iv = new ImageView(this);
            iv.setX(v.getX()-65);
            iv.setY(v.getY()-60);
            iv.setMaxHeight(70);
            iv.setMaxWidth(70);
            llJuego.addView(iv);
            hem = new HiloEliminarMaterial(this,v,iv);
            TaskHelper.execute(hem);
            arrayList.remove(v);
            v.setClickable(false);
            switch (v.getTag().toString()){
                case "roca":
                    setRoca(-1);
                    movimiento2 = R.drawable.golpe_pico;
                    iv.setBackgroundResource(movimiento2);
                    animacion2 = (AnimationDrawable) iv.getBackground();
                    animacion2.start();
                    if (random.nextInt(5)==2){
                        movimiento1 = R.drawable.roca_rota;
                        v.setBackgroundResource(movimiento1);
                        animacion1 = (AnimationDrawable) v.getBackground();
                        animacion1.start();
                    }else {
                        movimiento1 = R.drawable.roca_trans;
                        v.setBackgroundResource(movimiento1);
                        animacion1 = (AnimationDrawable) v.getBackground();
                        animacion1.start();
                        setPiedra(1);
                    }
                    break;
                case "tronco":
                    movimiento2 = R.drawable.golpe_hacha;
                    iv.setBackgroundResource(movimiento2);
                    animacion2 = (AnimationDrawable) iv.getBackground();
                    animacion2.start();
                    if (random.nextInt(5)==2){
                        movimiento1 = R.drawable.troncos_rotos;
                        v.setBackgroundResource(movimiento1);
                        animacion1 = (AnimationDrawable) v.getBackground();
                        animacion1.start();

                    }else {
                        movimiento1 = R.drawable.troncos_trans;
                        v.setBackgroundResource(movimiento1);
                        animacion1 = (AnimationDrawable) v.getBackground();
                        animacion1.start();
                        setTablasMadera(1);
                    }
                    break;
                case "gema_bruto":
                    movimiento2 = R.drawable.golpe_martillo;
                    iv.setBackgroundResource(movimiento2);
                    animacion2 = (AnimationDrawable) iv.getBackground();
                    animacion2.start();
                    if (random.nextInt(5)==2){
                        movimiento1 = R.drawable.gema_bruto_rota;
                        v.setBackgroundResource(movimiento1);
                        animacion1 = (AnimationDrawable) v.getBackground();
                        animacion1.start();
                    }else {
                        movimiento1 = R.drawable.gema_bruto_trans;
                        v.setBackgroundResource(movimiento1);
                        animacion1 = (AnimationDrawable) v.getBackground();
                        animacion1.start();
                        setGema(1);
                    }
                    break;
                case "hierro":
                    int ra = random.nextInt(3)+1;
                    if (ra==1){
                        movimiento2 = R.drawable.golpe_martillo;
                        iv.setBackgroundResource(movimiento2);
                        animacion2 = (AnimationDrawable) iv.getBackground();
                        animacion2.start();
                    }else if (ra==2){
                        movimiento2 = R.drawable.golpe_hacha;
                        iv.setBackgroundResource(movimiento2);
                        animacion2 = (AnimationDrawable) iv.getBackground();
                        animacion2.start();
                    }else{
                        movimiento2 = R.drawable.golpe_pico;
                        iv.setBackgroundResource(movimiento2);
                        animacion2 = (AnimationDrawable) iv.getBackground();
                        animacion2.start();
                    }
                    movimiento1 = R.drawable.hierro_roto;
                    v.setBackgroundResource(movimiento1);
                    animacion1 = (AnimationDrawable) v.getBackground();
                    animacion1.start();
                    break;
                case "pepita":
                    int r = random.nextInt(3)+1;
                    if (r==1){
                        movimiento2 = R.drawable.golpe_martillo;
                        iv.setBackgroundResource(movimiento2);
                        animacion2 = (AnimationDrawable) iv.getBackground();
                        animacion2.start();
                    }else if (r==2){
                        movimiento2 = R.drawable.golpe_hacha;
                        iv.setBackgroundResource(movimiento2);
                        animacion2 = (AnimationDrawable) iv.getBackground();
                        animacion2.start();
                    }else{
                        movimiento2 = R.drawable.golpe_pico;
                        iv.setBackgroundResource(movimiento2);
                        animacion2 = (AnimationDrawable) iv.getBackground();
                        animacion2.start();
                    }
                    movimiento1 = R.drawable.pepita_rota;
                    v.setBackgroundResource(movimiento1);
                    animacion1 = (AnimationDrawable) v.getBackground();
                    animacion1.start();
                    break;
            }
            if (arrayList.size()==0&&hcep.getStatus()== HiloCrearElementoProcesa.Status.FINISHED){
                hbp.cancel(true);
            }
        }

    }

    public void TerminarJuego() throws SQLException {
        PersonajeDao.actualizarGemaBruto(this,-(personaje.getGema_bruto()-getGemaBruto()));
        PersonajeDao.actualizarRoca(this,-(personaje.getRoca()-getRoca()));
        PersonajeDao.actualizarPepita(this,-(personaje.getPepita()-getOro()));
        PersonajeDao.actualizarHierro(this,-(personaje.getHierro()-getHierro()));
        PersonajeDao.actualizarTronco(this,-(personaje.getTronco()-getTronco()));

        PersonajeDao.actualizarGema(this,gema);
        PersonajeDao.actualizarPiedra(this,piedra);
        PersonajeDao.actualizarLingoteOro(this,lingoteOro);
        PersonajeDao.actualizarLingoteHierro(this,lingoteHierro);
        PersonajeDao.actualizarTablaMadera(this,tablasMadera);

        Intent i = new Intent(this, Index.class);
        startActivity(i);
        finish();
    }
    public Boolean isPlaying() {
        return play;
    }
    public void setPlay(Boolean play) {
        this.play = play;
    }
    public TextView getTxtRoca() {
        return txtRoca;
    }
    public void setTxtRoca(TextView txtRoca) {
        this.txtRoca = txtRoca;
    }
    public TextView getTxtTronco() {
        return txtTronco;
    }
    public void setTxtTronco(TextView txtTronco) {
        this.txtTronco = txtTronco;
    }
    public TextView getTxtHierro() {
        return txtHierro;
    }
    public void setTxtHierro(TextView txtHierro) {
        this.txtHierro = txtHierro;
    }
    public TextView getTxtOro() {
        return txtOro;
    }
    public void setTxtOro(TextView txtOro) {
        this.txtOro = txtOro;
    }
    public TextView getTxtGemaBruto() {
        return txtGemaBruto;
    }
    public void setTxtGemaBruto(TextView txtGemaBruto) {
        this.txtGemaBruto = txtGemaBruto;
    }
    public TextView getTxtPiedra() {
        return txtPiedra;
    }
    public void setTxtPiedra(TextView txtPiedra) {
        this.txtPiedra = txtPiedra;
    }
    public TextView getTxtTablasMadera() {
        return txtTablasMadera;
    }
    public void setTxtTablasMadera(TextView txtTablasMadera) {
        this.txtTablasMadera = txtTablasMadera;
    }
    public TextView getTxtLingoteHierro() {
        return txtLingoteHierro;
    }
    public void setTxtLingoteHierro(TextView txtLingoteHierro) {
        this.txtLingoteHierro = txtLingoteHierro;
    }
    public TextView getTxtLingoteOro() {
        return txtLingoteOro;
    }
    public void setTxtLingoteOro(TextView txtLingoteOro) {
        this.txtLingoteOro = txtLingoteOro;
    }
    public TextView getTxtGema() {
        return txtGema;
    }
    public void setTxtGema(TextView txtGema) {
        this.txtGema = txtGema;
    }
    public Personaje getPersonaje() {
        return personaje;
    }
    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }
    public RelativeLayout getLlJuego() {
        return llJuego;
    }
    public void setLlJuego(RelativeLayout llJuego) {
        this.llJuego = llJuego;
    }
    public TextView getTxtTiempo() {
        return txtTiempo;
    }
    public void setTxtTiempo(TextView txtTiempo) {
        this.txtTiempo = txtTiempo;
    }
    public Button getBtnJugar() {
        return btnJugar;
    }
    public void setBtnJugar(Button btnJugar) {
        this.btnJugar = btnJugar;
    }
    public Boolean getPlay() {
        return play;
    }
    public ArrayList<ImageView> getArrayList() {
        return arrayList;
    }
    public void setArrayList(ArrayList<ImageView> arrayList) {
        this.arrayList = arrayList;
    }
    public int getRoca() {
        return roca;
    }
    public void setRoca(int roca) {
        this.roca += roca;
        txtRoca.setText(String.valueOf(this.roca));
    }

    public int getTronco() {
        return tronco;
    }
    public void setTronco(int tronco) {
        this.tronco += tronco;
        txtTronco.setText(String.valueOf(this.tronco));
    }

    public int getHierro() {
        return hierro;
    }
    public void setHierro(int hierro) {
        this.hierro += hierro;
        txtHierro.setText(String.valueOf(this.hierro));
    }

    public int getOro() {
        return oro;
    }
    public void setOro(int oro) {
        this.oro += oro;
        txtOro.setText(String.valueOf(this.oro));
    }

    public int getGemaBruto() {
        return gemaBruto;
    }
    public void setGemaBruto(int gemaBruto) {
        this.gemaBruto += gemaBruto;
        txtGemaBruto.setText(String.valueOf(this.gemaBruto));
    }

    public int getPiedra() {
        return piedra;
    }
    public void setPiedra(int piedra) {
        this.piedra += piedra;
        txtPiedra.setText(String.valueOf(this.piedra));
    }

    public int getTablasMadera() {
        return tablasMadera;
    }
    public void setTablasMadera(int tablasMadera) {
        this.tablasMadera += tablasMadera;
        txtTablasMadera.setText(String.valueOf(this.tablasMadera));
    }

    public int getLingoteHierro() {
        return lingoteHierro;
    }
    public void setLingoteHierro(int lingoteHierro) {
        this.lingoteHierro += lingoteHierro;
        txtLingoteHierro.setText(String.valueOf(this.lingoteHierro));
    }

    public int getLingoteOro() {
        return lingoteOro;
    }
    public void setLingoteOro(int lingoteOro) {
        this.lingoteOro += lingoteOro;
        txtLingoteOro.setText(String.valueOf(this.lingoteOro));
    }

    public int getGema() {
        return gema;
    }
    public void setGema(int gema) {
        this.gema += gema;
        txtGema.setText(String.valueOf(this.gema));
    }

    public Boolean isBucle() {
        return bucle;
    }

    public void setBucle(Boolean bucle) {
        this.bucle = bucle;
    }
}
