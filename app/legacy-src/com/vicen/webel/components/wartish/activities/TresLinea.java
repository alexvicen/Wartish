package com.vicen.webel.components.wartish.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vicen.webel.components.wartish.R;
import com.vicen.webel.components.wartish.hilos.HiloJuego;

import java.util.Random;

public class TresLinea extends AppCompatActivity implements View.OnClickListener {

    private int arraynum[][] = new int[8][8];
    private ImageView arrayimg[][] = new ImageView[arraynum.length][arraynum[1].length];
    private Random r = new Random();
    private LinearLayout panelJuego;
    private int contador = 0, puntos = 20, punRoca = 0, punTronco = 0, punHierro = 0, punPepita = 0, punGemaBruto = 0, combo = 0;
    private ImageView casilla1, casilla2;
    private TextView txtPuntos, txtRoca, txtTronco, txtHierro, txtPepita, txtGemaBruto, txtCombo;
    private boolean play = false;
    private HiloJuego hj;
    private Button btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tres_linea);
        panelJuego = (LinearLayout) findViewById(R.id.panelJuego);
        txtPuntos = (TextView) findViewById(R.id.txtPuntos);
        txtRoca = (TextView) findViewById(R.id.txtRoca);
        txtTronco = (TextView) findViewById(R.id.txtTronco);
        txtHierro = (TextView) findViewById(R.id.txtHierro);
        txtPepita = (TextView) findViewById(R.id.txtPepita);
        txtGemaBruto = (TextView) findViewById(R.id.txtGemaBruto);
        txtCombo = (TextView) findViewById(R.id.txtCombo);
        txtCombo.setVisibility(View.INVISIBLE);
        btnSalir = (Button) findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this);
        CrearArrayTablero();
        CrearImagenesTablero();
        CrearTablero();
        hj = new HiloJuego(this, puntos, arraynum, arrayimg);
        hj.execute();
        while (hj.comprobarHorizontal()) ;
        txtPuntos.setText(puntos + "");
        play = true;
    }

    public void CrearArrayTableroMano() {

        arraynum[0][0] = 0;
        arraynum[0][1] = 0;
        arraynum[0][2] = 2;
        arraynum[0][3] = 3;
        arraynum[0][4] = 4;
        arraynum[0][5] = 0;
        arraynum[0][6] = 1;
        arraynum[0][7] = 2;
        arraynum[1][0] = 3;
        arraynum[1][1] = 4;
        arraynum[1][2] = 0;
        arraynum[1][3] = 1;
        arraynum[1][4] = 2;
        arraynum[1][5] = 3;
        arraynum[1][6] = 4;
        arraynum[1][7] = 0;
        arraynum[2][0] = 1;
        arraynum[2][1] = 2;
        arraynum[2][2] = 3;
        arraynum[2][3] = 3;
        arraynum[2][4] = 0;
        arraynum[2][5] = 1;
        arraynum[2][6] = 2;
        arraynum[2][7] = 3;
        arraynum[3][0] = 4;
        arraynum[3][1] = 1;
        arraynum[3][2] = 1;
        arraynum[3][3] = 2;
        arraynum[3][4] = 3;
        arraynum[3][5] = 2;
        arraynum[3][6] = 0;
        arraynum[3][7] = 1;
        arraynum[4][0] = 2;
        arraynum[4][1] = 3;
        arraynum[4][2] = 4;
        arraynum[4][3] = 0;
        arraynum[4][4] = 1;
        arraynum[4][5] = 2;
        arraynum[4][6] = 3;
        arraynum[4][7] = 1;
        arraynum[5][0] = 0;
        arraynum[5][1] = 1;
        arraynum[5][2] = 2;
        arraynum[5][3] = 2;
        arraynum[5][4] = 4;
        arraynum[5][5] = 0;
        arraynum[5][6] = 1;
        arraynum[5][7] = 2;
        arraynum[6][0] = 1;
        arraynum[6][1] = 4;
        arraynum[6][2] = 0;
        arraynum[6][3] = 1;
        arraynum[6][4] = 2;
        arraynum[6][5] = 3;
        arraynum[6][6] = 4;
        arraynum[6][7] = 0;
        arraynum[7][0] = 1;
        arraynum[7][1] = 2;
        arraynum[7][2] = 3;
        arraynum[7][3] = 4;
        arraynum[7][4] = 0;
        arraynum[7][5] = 1;
        arraynum[7][6] = 2;
        arraynum[7][7] = 3;

    }

    public void CrearArrayTablero() {
        for (int i = 0; i < arraynum.length; i++) {
            for (int j = 0; j < arraynum[i].length; j++) {
                arraynum[i][j] = r.nextInt(5);
            }
        }
    }

    public void CrearImagenesTablero() {
        for (int i = 0; i < arraynum.length; i++) {
            for (int j = 0; j < arraynum[i].length; j++) {
                ImageView imageView = new ImageView(this);
                if (arraynum[i][j] == 0) {
                    imageView.setImageResource(R.drawable.roca);
                } else if (arraynum[i][j] == 1) {
                    imageView.setImageResource(R.drawable.troncos);
                } else if (arraynum[i][j] == 2) {
                    imageView.setImageResource(R.drawable.hierro);
                } else if (arraynum[i][j] == 3) {
                    imageView.setImageResource(R.drawable.pepita);
                } else {
                    imageView.setImageResource(R.drawable.gema_bruto);
                }
                arrayimg[i][j] = imageView;
            }
        }
    }

    public void CrearTablero() {
        for (int i = 0; i < arraynum.length; i++) {
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            for (int j = 0; j < arraynum[i].length; j++) {
                ImageView iv;
                iv = arrayimg[i][j];
                iv.setTag(i + "" + j);
                iv.setLayoutParams(lp);
                iv.setOnClickListener(this);
                ll.addView(iv);
            }
            panelJuego.addView(ll);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSalir) {
            puntos = 0;
        } else {
            contador++;
            if (contador == 1) {
                setCombo(0);
                txtCombo.setVisibility(View.INVISIBLE);
                casilla1 = (ImageView) v;
                activarProximos();
            } else if (contador == 2) {
                casilla2 = (ImageView) v;
                if (casilla1 == casilla2) {
                    contador = 0;
                    activarTodos();
                } else {
                    cambiarPosicion(casilla1, casilla2);
                    txtCombo.setVisibility(View.VISIBLE);
                    while (hj.comprobarHorizontal()) ;
                    contador = 0;
                    movimientos();
                    activarTodos();

                }
            }
        }
    }

    public void cambiarPosicion(ImageView imageView1, ImageView imageView2) {
        String tag1 = imageView1.getTag().toString();
        String tag2 = imageView2.getTag().toString();
        int i1 = Integer.parseInt(tag1.substring(0, 1));
        int j1 = Integer.parseInt(tag1.substring(1));
        int i2 = Integer.parseInt(tag2.substring(0, 1));
        int j2 = Integer.parseInt(tag2.substring(1));
        int aux;
        aux = arraynum[i1][j1];
        arraynum[i1][j1] = arraynum[i2][j2];
        arraynum[i2][j2] = aux;
        panelJuego.removeAllViews();
        CrearImagenesTablero();
        CrearTablero();
    }

    public void activarProximos() {
        String tag1 = casilla1.getTag().toString();
        int i1 = Integer.parseInt(tag1.substring(0, 1));
        int j1 = Integer.parseInt(tag1.substring(1));
        for (int i = 0; i < arrayimg.length; i++) {
            for (int j = 0; j < arrayimg[i].length; j++) {
                if (i1 == i - 1 && j1 == j - 1 || i1 == i && j1 == j - 1 || i1 == i + 1 && j1 == j - 1 || i1 == i + 1 && j1 == j || i1 == i + 1 && j1 == j + 1 || i1 == i && j1 == j + 1 || i1 == i - 1 && j1 == j + 1 || i1 == i - 1 && j1 == j || i1 == i && j1 == j) {

                } else {
                    arrayimg[i][j].setClickable(false);
                    arrayimg[i][j].setAlpha(0.5f);
                }
            }
        }
    }

    public void activarTodos() {
        for (int i = 0; i < arrayimg.length; i++) {
            for (int j = 0; j < arrayimg[i].length; j++) {
                arrayimg[i][j].setClickable(true);
                arrayimg[i][j].setAlpha(1f);
            }
        }
    }

    public int getPuntos() {
        return puntos;
    }

    public int getPunRoca() {
        return punRoca;
    }

    public void setPunRoca(int punRoca) {
        this.punRoca = punRoca;
        txtRoca.setText(punRoca + "");
    }

    public int getPunTronco() {
        return punTronco;
    }

    public void setPunTronco(int punTronco) {
        this.punTronco = punTronco;
        txtTronco.setText(punTronco + "");
    }

    public int getPunPepita() {
        return punPepita;
    }

    public void setPunPepita(int punPepita) {
        this.punPepita = punPepita;
        txtPepita.setText(punPepita + "");
    }

    public int getPunHierro() {
        return punHierro;
    }

    public void setPunHierro(int punHierro) {
        this.punHierro = punHierro;
        txtHierro.setText(punHierro + "");
    }

    public int getPunGemaBruto() {
        return punGemaBruto;
    }

    public void setPunGemaBruto(int punGemaBruto) {
        this.punGemaBruto = punGemaBruto;
        txtGemaBruto.setText(punGemaBruto + "");
    }

    public void movimientos() {
        puntos--;
        txtPuntos.setText(puntos + "");
        if (puntos == 0) {
            panelJuego.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isPlay() {
        return play;
    }

    public int getCombo() {
        return combo;
    }

    public void setCombo(int combo) {
        this.combo = combo;
        if (combo <= 2) {
            txtCombo.setTextColor(Color.GREEN);
            txtCombo.setTextSize(20);
        } else if (combo <= 5) {
            txtCombo.setTextColor(Color.YELLOW);
            txtCombo.setTextSize(25);
        } else {
            txtCombo.setTextColor(Color.RED);
            txtCombo.setTextSize(30);
        }
        txtCombo.setText("Combo x " + combo);
    }
}
