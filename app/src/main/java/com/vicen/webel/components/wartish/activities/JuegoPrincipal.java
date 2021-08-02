package com.vicen.webel.components.wartish.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vicen.webel.components.wartish.R;
import com.vicen.webel.components.wartish.dao.PersonajeDao;
import com.vicen.webel.components.wartish.entidades.Esqueleto;
import com.vicen.webel.components.wartish.entidades.Personaje;
import com.vicen.webel.components.wartish.hilos.HiloAtaqueEnemigos;
import com.vicen.webel.components.wartish.hilos.HiloAtaquePersonaje;
import com.vicen.webel.components.wartish.hilos.HiloMoverEntrada;
import com.vicen.webel.components.wartish.hilos.TaskHelper;

import java.sql.SQLException;
import java.util.Random;


public class JuegoPrincipal extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rl;
    private AnimationDrawable animacion1 = new AnimationDrawable();
    private AnimationDrawable animacion2 = new AnimationDrawable();
    private AnimationDrawable animacion3 = new AnimationDrawable();
    private AnimationDrawable animacion4 = new AnimationDrawable();
    private int movimiento1, movimiento2, movimiento3,movimiento4;
    private ImageView ivPer,iv1, iv2, iv3;
    private int ancho, alto, contadorEspecial=0;
    private TextView txt1, txt2,txt3,txtVida,txtNivelPersonaje,txtNivelEsqueleto1,txtNivelEsqueleto2,txtNivelEsqueleto3;
    private Button btnJugar,btnSimple,btnEspecial,btnVolver;
    private ProgressBar pbVida,pb1,pb2,pb3;
    private boolean encendido = true,ataque=true,ataquePer=true,ataqueEsp=true,enem1=false,enem2=false,enem3=false;
    private HiloMoverEntrada hm;
    private HiloAtaqueEnemigos ha;
    private TaskHelper t = new TaskHelper();
    private Personaje personaje;

    private Random r = new Random();
    private Esqueleto esqueleto1;
    private Esqueleto esqueleto2;
    private Esqueleto esqueleto3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego_principal);
        try {
            personaje = PersonajeDao.buscarPersonaje(this);
            esqueleto1 = new Esqueleto((r.nextInt(personaje.getNivel())+4));
            esqueleto2 = new Esqueleto((r.nextInt(personaje.getNivel())+4));
            esqueleto3 = new Esqueleto((r.nextInt(personaje.getNivel())+4));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        rl = (RelativeLayout) findViewById(R.id.rl);
        txtVida = (TextView)findViewById(R.id.txtvida);
        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);
        txt3 = (TextView)findViewById(R.id.txt3);
        txtNivelPersonaje = (TextView)findViewById(R.id.txtNivelPersonaje);
        txtNivelEsqueleto1 = (TextView)findViewById(R.id.txtNivelEsqueleto1);
        txtNivelEsqueleto2 = (TextView)findViewById(R.id.txtNivelEsqueleto2);
        txtNivelEsqueleto3 = (TextView)findViewById(R.id.txtNivelEsqueleto3);
        btnJugar = (Button) findViewById(R.id.btn);
        btnSimple = (Button) findViewById(R.id.btnSimple);
        btnEspecial = (Button) findViewById(R.id.btnEspecial);
        btnVolver = (Button)findViewById(R.id.btnVolver);
        pbVida = (ProgressBar)findViewById(R.id.pbVida);
        pb1 = (ProgressBar)findViewById(R.id.pb1);
        pb2 = (ProgressBar)findViewById(R.id.pb2);
        pb3 = (ProgressBar)findViewById(R.id.pb3);

        btnJugar.setOnClickListener(this);
        btnSimple.setOnClickListener(this);
        btnEspecial.setOnClickListener(this);
        btnVolver.setOnClickListener(this);
        btnSimple.setVisibility(View.INVISIBLE);
        btnEspecial.setVisibility(View.INVISIBLE);
        btnVolver.setVisibility(View.INVISIBLE);
        txtVida.setVisibility(View.INVISIBLE);
        txt1.setVisibility(View.INVISIBLE);
        txt2.setVisibility(View.INVISIBLE);
        txt3.setVisibility(View.INVISIBLE);
        txtNivelPersonaje.setVisibility(View.INVISIBLE);
        txtNivelEsqueleto1.setVisibility(View.INVISIBLE);
        txtNivelEsqueleto2.setVisibility(View.INVISIBLE);
        txtNivelEsqueleto3.setVisibility(View.INVISIBLE);
        pbVida.setVisibility(View.INVISIBLE);
        pb1.setVisibility(View.INVISIBLE);
        pb2.setVisibility(View.INVISIBLE);
        pb3.setVisibility(View.INVISIBLE);

        txtVida.setText(personaje.getVida() + "");
        txtNivelPersonaje.setText("("+personaje.getNivel()+")");
        pbVida.setMax(personaje.getVida());
        pbVida.setProgress(personaje.getVida());

        txt1.setText(esqueleto1.getVida() + "");
        txtNivelEsqueleto1.setText("("+esqueleto1.getNivel()+")");
        pb1.setMax(esqueleto1.getVida());
        pb1.setProgress(esqueleto1.getVida());

        txt2.setText(esqueleto2.getVida()+"");
        txtNivelEsqueleto2.setText("("+esqueleto2.getNivel()+")");
        pb2.setMax(esqueleto2.getVida());
        pb2.setProgress(esqueleto2.getVida());

        txt3.setText(esqueleto3.getVida()+"");
        txtNivelEsqueleto3.setText("("+esqueleto3.getNivel()+")");
        pb3.setMax(esqueleto3.getVida());
        pb3.setProgress(esqueleto3.getVida());

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {
            btnJugar.setVisibility(View.INVISIBLE);
            btnJugar.setEnabled(false);
            iv1 = new ImageView(this);
            iv2 = new ImageView(this);
            iv3 = new ImageView(this);
            ivPer = new ImageView(this);
            ancho = rl.getWidth();
            alto = rl.getHeight() - 120;
            iv1.setX(ancho);
            iv1.setY(alto);
            iv2.setX(ancho);
            iv2.setY(alto / 2);
            iv3.setX(ancho);
            iv3.setY(10);
            ivPer.setX(0);
            ivPer.setY(alto / 2);
            rl.addView(iv1);
            rl.addView(iv2);
            rl.addView(iv3);
            rl.addView(ivPer);
            movimiento1 = R.drawable.entrada_esqueleto;
            movimiento2 = R.drawable.entrada_esqueleto;
            movimiento3 = R.drawable.entrada_esqueleto;
            movimiento4 = R.drawable.entrada_chico;
            iv1.setBackgroundResource(movimiento1);
            iv2.setBackgroundResource(movimiento2);
            iv3.setBackgroundResource(movimiento3);
            ivPer.setBackgroundResource(movimiento4);
            animacion1 = (AnimationDrawable) iv1.getBackground();
            animacion2 = (AnimationDrawable) iv2.getBackground();
            animacion3 = (AnimationDrawable) iv3.getBackground();
            animacion4 = (AnimationDrawable) ivPer.getBackground();
            animacion1.start();
            animacion2.start();
            animacion3.start();
            animacion4.start();
            hm = new HiloMoverEntrada(this, iv1, iv2, iv3, ivPer);
            hm.execute();
        }else if (v.getId()==R.id.btnSimple){
            HiloAtaquePersonaje hap= new HiloAtaquePersonaje(this, iv1, iv2, iv3, ivPer);
            t.execute(hap);
            contadorEspecial++;
            ataqueEsp=false;
            if (contadorEspecial==7){
                btnEspecial.setClickable(true);
                btnEspecial.setAlpha(1f);
            }
        }else if (v.getId()==R.id.btnEspecial){
            HiloAtaquePersonaje hap= new HiloAtaquePersonaje(this, iv1, iv2, iv3, ivPer);
            t.execute(hap);
            ataqueEsp=true;
            contadorEspecial=0;
            btnEspecial.setClickable(false);
            btnEspecial.setAlpha(0.5f);
        }else if (v.getId()==R.id.btnVolver){
            encendido=false;
            ataque=false;
        }
    }

    public boolean isEncendido() {
        return encendido;
    }
    public void setEncendido(boolean encendido) {
        this.encendido = encendido;
    }
    public RelativeLayout getRl() {
        return rl;
    }
    public AnimationDrawable getAnimacion1() {
        return animacion1;
    }
    public AnimationDrawable getAnimacion2() {
        return animacion2;
    }
    public AnimationDrawable getAnimacion3() {
        return animacion3;
    }
    public AnimationDrawable getAnimacion4() {
        return animacion4;
    }
    public Button getBtnEspecial() {
        return btnEspecial;
    }
    public Button getBtnSimple() {
        return btnSimple;
    }
    public boolean isAtaque() {
        return ataque;
    }
    public void setAtaque(boolean ataque) {
        this.ataque = ataque;
    }
    public TaskHelper getT() {
        return t;
    }
    public boolean isAtaquePer() {
        return ataquePer;
    }
    public void setAtaquePer(boolean ataquePer) {
        this.ataquePer = ataquePer;
    }
    public boolean isAtaqueEsp() {
        return ataqueEsp;
    }
    public void setAtaqueEsp(boolean ataqueEsp) {
        this.ataqueEsp = ataqueEsp;
    }
    public Personaje getPersonaje() {
        return personaje;
    }
    public TextView getTxtVida() {
        return txtVida;
    }
    public Esqueleto getEsqueleto1() {
        return esqueleto1;
    }
    public Esqueleto getEsqueleto2() {
        return esqueleto2;
    }
    public Esqueleto getEsqueleto3() {
        return esqueleto3;
    }
    public TextView getTxt1() {
        return txt1;
    }
    public TextView getTxt2() {
        return txt2;
    }
    public TextView getTxt3() {
        return txt3;
    }
    public TextView getTxtNivelPersonaje() {
        return txtNivelPersonaje;
    }
    public TextView getTxtNivelEsqueleto1() {
        return txtNivelEsqueleto1;
    }
    public TextView getTxtNivelEsqueleto2() {
        return txtNivelEsqueleto2;
    }
    public TextView getTxtNivelEsqueleto3() {
        return txtNivelEsqueleto3;
    }
    public ProgressBar getPb1() {
        return pb1;
    }
    public ProgressBar getPb2() {
        return pb2;
    }
    public ProgressBar getPb3() {
        return pb3;
    }
    public ProgressBar getPbVida() {
        return pbVida;
    }
    public boolean isEnem1() {
        return enem1;
    }
    public void setEnem1(boolean enem1) {
        this.enem1 = enem1;
    }
    public boolean isEnem2() {
        return enem2;
    }
    public void setEnem2(boolean enem2) {
        this.enem2 = enem2;
    }
    public boolean isEnem3() {
        return enem3;
    }
    public void setEnem3(boolean enem3) {
        this.enem3 = enem3;
    }
    public Button getBtnVolver() {
        return btnVolver;
    }
    public void setBtnVolver(Button btnVolver) {
        this.btnVolver = btnVolver;
    }

    public void bajarVida(Esqueleto esqueleto){
        int daño = (esqueleto.getAtaque()-(personaje.getDefensa()));
        if (daño<0){
            daño=0;
        }
        personaje.setVida(personaje.getVida()- daño);
        if (personaje.getVida()<1){
            personaje.setVida(1);
        }
        pbVida.setProgress(personaje.getVida());
        txtVida.setText(personaje.getVida() + "");
        if (personaje.getVida()<=1){
            encendido=false;
        }
        if ((esqueleto1.getVida()<1)&&(esqueleto2.getVida()<1)&&(esqueleto3.getVida()<1)){
            encendido=false;
        }
    }
    public void bajarVidaCritico(Esqueleto esqueleto){
        int daño = (esqueleto.getAtaque()+esqueleto.getCritico()-(personaje.getDefensa()));
        if (daño<0){
            daño=0;
        }
        personaje.setVida(personaje.getVida()-daño);
        if (personaje.getVida()<1){
            personaje.setVida(1);
        }
        pbVida.setProgress(personaje.getVida());
        txtVida.setText(personaje.getVida()+"");
        if (personaje.getVida()<=5){
            encendido=false;
        }
        if ((esqueleto1.getVida()<1)&&(esqueleto2.getVida()<1)&&(esqueleto3.getVida()<1)){
            encendido=false;
        }
    }
    public void bajarVidaEnemigoCritico(Esqueleto esqueleto){
        if (esqueleto==esqueleto1){
            int daño =  (personaje.getAtaque()+personaje.getCritico()-esqueleto1.getDefensa());
            if (daño<0){
                daño=0;
            }
            esqueleto1.setVida(esqueleto1.getVida() - daño);

            if (esqueleto1.getVida()<=0){
                esqueleto1.setVida(0);
                enem1=true;
            }
            pb1.setProgress(esqueleto.getVida());
            txt1.setText(esqueleto.getVida()+"");
        }else if (esqueleto==esqueleto2){
            int daño =  (personaje.getAtaque()+personaje.getCritico()-esqueleto2.getDefensa());
            if (daño<0){
                daño=0;
            }
            esqueleto2.setVida(esqueleto2.getVida() - daño);

            if (esqueleto2.getVida()<=0){
                esqueleto2.setVida(0);
                enem2=true;
            }
            pb2.setProgress(esqueleto.getVida());
            txt2.setText(esqueleto.getVida()+"");
        }else if (esqueleto==esqueleto3){
            int daño =  (personaje.getAtaque()+personaje.getCritico()-esqueleto3.getDefensa());
            if (daño<0){
                daño=0;
            }
            esqueleto3.setVida(esqueleto3.getVida() -daño);

            if (esqueleto3.getVida()<=0){
                esqueleto3.setVida(0);
                enem3=true;
            }
            pb3.setProgress(esqueleto.getVida());
            txt3.setText(esqueleto.getVida()+"");
        }
        if (esqueleto1.getVida()<1&&esqueleto2.getVida()<1&&esqueleto3.getVida()<1){
            encendido=false;
        }
    }
    public void bajarVidaEnemigo(Esqueleto esqueleto){
        if (esqueleto==esqueleto1){
            int daño = (personaje.getAtaque() - (esqueleto1.getDefensa()));
            if (daño<0){
                daño=0;
            }
            esqueleto1.setVida(esqueleto1.getVida()-daño);
            if (esqueleto1.getVida()<=0){
                esqueleto1.setVida(0);
                enem1=true;
            }
            pb1.setProgress(esqueleto1.getVida());
            txt1.setText(esqueleto1.getVida()+"");
        }else if (esqueleto==esqueleto2){
            int daño= (personaje.getAtaque()-(esqueleto2.getDefensa()));
            if (daño<0){
                daño=0;
            }
            esqueleto2.setVida(esqueleto2.getVida() - daño);
            if (esqueleto2.getVida()<=0){
                esqueleto2.setVida(0);
                enem2=true;
            }
            pb2.setProgress(esqueleto2.getVida());
            txt2.setText(esqueleto2.getVida()+"");
        }else if (esqueleto==esqueleto3){
            int daño =  (personaje.getAtaque()-(esqueleto3.getDefensa()));
            if (daño<0){
                daño=0;
            }
            esqueleto3.setVida(esqueleto3.getVida() - daño);
            if (esqueleto3.getVida()<=0){
                esqueleto3.setVida(0);
                enem3=true;
            }
            pb3.setProgress(esqueleto3.getVida());
            txt3.setText(esqueleto3.getVida()+"");
        }
        if ((esqueleto1.getVida()<1)&&(esqueleto2.getVida()<1)&&(esqueleto3.getVida()<1)){
            encendido=false;
        }

    }
    public void bajarVidaEnemigoEspecial(Esqueleto esqueleto){

        if (esqueleto==esqueleto1){
            int daño = (personaje.getAtaque() + personaje.getMagia() - (esqueleto1.getDefensa()));
            if (daño<0){
                daño=0;
            }
            esqueleto1.setVida(esqueleto1.getVida()-daño);
            if (esqueleto1.getVida()<=0){
                esqueleto1.setVida(0);
                enem1=true;
            }
            pb1.setProgress(esqueleto1.getVida());
            txt1.setText(esqueleto1.getVida()+"");
        }else if (esqueleto==esqueleto2){
            int daño= (personaje.getAtaque() + personaje.getMagia()-(esqueleto2.getDefensa()));
            if (daño<0){
                daño=0;
            }
            esqueleto2.setVida(esqueleto2.getVida() - daño);
            if (esqueleto2.getVida()<=0){
                esqueleto2.setVida(0);
                enem2=true;
            }
            pb2.setProgress(esqueleto2.getVida());
            txt2.setText(esqueleto2.getVida()+"");
        }else if (esqueleto==esqueleto3){
            int daño =  (personaje.getAtaque() + personaje.getMagia()-(esqueleto3.getDefensa()));
            if (daño<0){
                daño=0;
            }
            esqueleto3.setVida(esqueleto3.getVida() - daño);
            if (esqueleto3.getVida()<=0){
                esqueleto3.setVida(0);
                enem3=true;
            }
            pb3.setProgress(esqueleto3.getVida());
            txt3.setText(esqueleto3.getVida()+"");
        }
        if ((esqueleto1.getVida()<1)&&(esqueleto2.getVida()<1)&&(esqueleto3.getVida()<1)){
            encendido=false;
        }
    }
}
