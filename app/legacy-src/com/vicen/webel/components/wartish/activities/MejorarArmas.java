package com.vicen.webel.components.wartish.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vicen.webel.components.wartish.R;
import com.vicen.webel.components.wartish.dao.PersonajeDao;
import com.vicen.webel.components.wartish.entidades.Personaje;

import java.sql.SQLException;

public class MejorarArmas extends Activity implements View.OnClickListener {
    private String equipo;
    private TextView txtNivel,txtPiedraMejora,txtTablaMaderaMejora,txtLingoteHierroMejora,txtLingoteOroMejora,txtGemaMejora;
    private TextView txtPiedra,txtTablaMadera,txtLingoteHierro,txtLingoteOro,txtGema;
    private Button btnMejorar,btnSalir;
    private ImageButton ibArma;
    private int contMejora=0;
    private Personaje p = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mejorar_armas);
        txtNivel = (TextView)findViewById(R.id.txtNivel);
        txtPiedraMejora = (TextView)findViewById(R.id.txtPiedraMejora);
        txtTablaMaderaMejora = (TextView)findViewById(R.id.txtTablaMaderaMejora);
        txtLingoteHierroMejora = (TextView)findViewById(R.id.txtLingoteHierroMejora);
        txtLingoteOroMejora = (TextView)findViewById(R.id.txtLingoteOroMejora);
        txtGemaMejora = (TextView)findViewById(R.id.txtGemaMejora);
        txtPiedra = (TextView)findViewById(R.id.txtPiedra);
        txtTablaMadera = (TextView)findViewById(R.id.txtTablasMadera);
        txtLingoteHierro = (TextView)findViewById(R.id.txtLingoteHierro);
        txtLingoteOro = (TextView)findViewById(R.id.txtLingoteOro);
        txtGema = (TextView)findViewById(R.id.txtGema);
        btnMejorar = (Button)findViewById(R.id.btnMejorar);
        btnSalir = (Button)findViewById(R.id.btnSalir);
        ibArma = (ImageButton)findViewById(R.id.ibArma);
        btnMejorar.setOnClickListener(this);
        btnSalir.setOnClickListener(this);
        ibArma.setOnClickListener(this);
        btnMejorar.setClickable(false);
        btnMejorar.setAlpha(0.5f);
        try {
            p = PersonajeDao.buscarPersonaje(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        equipo = getIntent().getStringExtra("equipo");
        switch (equipo){
            case "casco":
                ibArma.setBackgroundResource(R.drawable.casco_pequeno);
                float nivCasco = p.getNivCasco();
                if (nivCasco==0){
                    nivCasco=0.5f;
                }
                txtNivel.setText("Nivel: "+p.getNivCasco());
                txtPiedraMejora.setText((int)(nivCasco*50)+"");
                txtTablaMaderaMejora.setText((int)(nivCasco*50)+"");
                txtLingoteHierroMejora.setText((int)(nivCasco*100)+"");
                txtLingoteOroMejora.setText((int)(nivCasco*100)+"");
                txtGemaMejora.setText((int)(nivCasco*100)+"");
                break;
            case "arco":
                ibArma.setBackgroundResource(R.drawable.arco_pequeno);
                float nivArco = p.getNivArco();
                if (nivArco==0){
                    nivArco=0.5f;
                }
                txtNivel.setText("Nivel: "+p.getNivArco());
                txtPiedraMejora.setText((int)(nivArco*50)+"");
                txtTablaMaderaMejora.setText((int)(nivArco*100)+"");
                txtLingoteHierroMejora.setText((int)(nivArco*50)+"");
                txtLingoteOroMejora.setText((int)(nivArco*100)+"");
                txtGemaMejora.setText((int)(nivArco*100)+"");
                break;
            case "escudo":
                ibArma.setBackgroundResource(R.drawable.escudo_pequeno);
                float nivEscudo = p.getNivEscudo();
                if (nivEscudo==0){
                    nivEscudo=0.5f;
                }
                txtNivel.setText("Nivel: "+p.getNivEscudo());
                txtPiedraMejora.setText((int)(nivEscudo*100)+"");
                txtTablaMaderaMejora.setText((int)(nivEscudo*100)+"");
                txtLingoteHierroMejora.setText((int)(nivEscudo*100)+"");
                txtLingoteOroMejora.setText((int)(nivEscudo*100)+"");
                txtGemaMejora.setText((int)(nivEscudo*100)+"");
                break;
            case "guantes":
                ibArma.setBackgroundResource(R.drawable.guantes_pequenos);
                float nivGuantes = p.getNivEscudo();
                if (nivGuantes==0){
                    nivGuantes=0.5f;
                }
                txtNivel.setText("Nivel: "+p.getNivGuantes());
                txtPiedraMejora.setText((int)(nivGuantes*50)+"");
                txtTablaMaderaMejora.setText((int)(nivGuantes*50)+"");
                txtLingoteHierroMejora.setText((int)(nivGuantes*100)+"");
                txtLingoteOroMejora.setText((int)(nivGuantes*100)+"");
                txtGemaMejora.setText((int)(nivGuantes*100)+"");
                break;
            case "botas":
                ibArma.setBackgroundResource(R.drawable.botas_pequenas);
                float nivBotas = p.getNivBotas();
                if (nivBotas==0){
                    nivBotas=0.5f;
                }
                txtNivel.setText("Nivel: "+p.getNivBotas());
                txtPiedraMejora.setText((int)(nivBotas*50)+"");
                txtTablaMaderaMejora.setText((int)(nivBotas*50)+"");
                txtLingoteHierroMejora.setText((int)(nivBotas*50)+"");
                txtLingoteOroMejora.setText((int)(nivBotas*100)+"");
                txtGemaMejora.setText((int)(nivBotas*100)+"");
                break;
            case "flechas":
                ibArma.setBackgroundResource(R.drawable.flecha_pequena);
                float nivFlecha = p.getNivFlecha();
                if (nivFlecha==0){
                    nivFlecha=0.5f;
                }
                txtNivel.setText("Nivel: "+p.getNivFlecha());
                txtPiedraMejora.setText((int)(nivFlecha*100)+"");
                txtTablaMaderaMejora.setText((int)(nivFlecha*100)+"");
                txtLingoteHierroMejora.setText((int)(nivFlecha*50)+"");
                txtLingoteOroMejora.setText((int)(nivFlecha*100)+"");
                txtGemaMejora.setText((int)(nivFlecha*100)+"");
                break;
        }
        txtPiedra.setText(p.getPiedra()+"");
        txtTablaMadera.setText(p.getTabla_madera()+"");
        txtLingoteHierro.setText(p.getLingote_hierro()+"");
        txtLingoteOro.setText(p.getLingote_oro()+"");
        txtGema.setText(p.getGema()+"");
        if (Integer.parseInt(txtPiedraMejora.getText().toString())>Integer.parseInt(txtPiedra.getText().toString())){
            txtPiedraMejora.setTextColor(Color.RED);
            ibArma.setClickable(false);
            btnMejorar.setText("SALIR");
        }
        if (Integer.parseInt(txtTablaMaderaMejora.getText().toString())>Integer.parseInt(txtTablaMadera.getText().toString())){
            txtTablaMaderaMejora.setTextColor(Color.RED);
            ibArma.setClickable(false);
            btnMejorar.setText("SALIR");
        }
        if (Integer.parseInt(txtLingoteHierroMejora.getText().toString())>Integer.parseInt(txtLingoteHierro.getText().toString())){
            txtLingoteHierroMejora.setTextColor(Color.RED);
            ibArma.setClickable(false);
            btnMejorar.setText("SALIR");
        }
        if (Integer.parseInt(txtLingoteOroMejora.getText().toString())>Integer.parseInt(txtLingoteOro.getText().toString())){
            txtLingoteOroMejora.setTextColor(Color.RED);
            ibArma.setClickable(false);
            btnMejorar.setText("SALIR");
        }
        if (Integer.parseInt(txtGemaMejora.getText().toString())>Integer.parseInt(txtGema.getText().toString())){
            txtGemaMejora.setTextColor(Color.RED);
            ibArma.setClickable(false);
            btnMejorar.setText("SALIR");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibArma:
                contMejora++;
                if (contMejora==5){
                    contMejora=0;
                    btnMejorar.setClickable(true);
                    btnMejorar.setAlpha(1f);
                    ibArma.setClickable(false);
                }
                break;
            case R.id.btnMejorar:
                switch (equipo){
                    case "casco":
                        try {
                            PersonajeDao.actualizarNivelCasco(this,p.getNivCasco()+1);
                            finish();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "arco":
                        try {
                            PersonajeDao.actualizarNivelArco(this,p.getNivArco()+1);
                            finish();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "escudo":
                        try {
                            PersonajeDao.actualizarNivelEscudo(this,p.getNivEscudo()+1);
                            finish();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "guantes":
                        try {
                            PersonajeDao.actualizarNivelGuantes(this,p.getNivGuantes()+1);
                            finish();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "botas":
                        try {
                            PersonajeDao.actualizarNivelBotas(this,p.getNivBotas()+1);
                            finish();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "flechas":
                        try {
                            PersonajeDao.actualizarNivelFlecha(this,p.getNivFlecha()+1);
                            finish();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                try {
                    PersonajeDao.actualizarPiedra(this,-Integer.parseInt(txtPiedraMejora.getText().toString()));
                    PersonajeDao.actualizarTablaMadera(this,-Integer.parseInt(txtTablaMaderaMejora.getText().toString()));
                    PersonajeDao.actualizarLingoteHierro(this,-Integer.parseInt(txtLingoteHierroMejora.getText().toString()));
                    PersonajeDao.actualizarLingoteOro(this,-Integer.parseInt(txtLingoteOroMejora.getText().toString()));
                    PersonajeDao.actualizarGema(this,-Integer.parseInt(txtGemaMejora.getText().toString()));

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnSalir:
                finish();
                break;
        }
    }
}
