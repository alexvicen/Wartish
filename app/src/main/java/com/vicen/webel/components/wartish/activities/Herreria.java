package com.vicen.webel.components.wartish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vicen.webel.components.wartish.R;
import com.vicen.webel.components.wartish.dao.PersonajeDao;
import com.vicen.webel.components.wartish.entidades.Personaje;

import java.sql.SQLException;


public class Herreria extends AppCompatActivity implements View.OnClickListener{
    private TextView txtPiedra,txtTablasMadera,txtLingoteHierro,txtLingoteOro,txtGema,txtNivCasco,txtNivArco,txtNivEscudo,txtNivGuantes,txtNivBotas,txtNivFlechas;
    private Button btnCasco,btnArco,btnEscudo,btnGuantes,btnBotas,btnFlechas,btnIndex;
    private Boolean activo = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herreria);
        txtPiedra =(TextView)findViewById(R.id.txtPiedra);
        txtTablasMadera =(TextView)findViewById(R.id.txtTablasMadera);
        txtLingoteHierro =(TextView)findViewById(R.id.txtLingoteHierro);
        txtLingoteOro =(TextView)findViewById(R.id.txtLingoteOro);
        txtGema =(TextView)findViewById(R.id.txtGema);
        txtNivCasco =(TextView)findViewById(R.id.txtNivCasco);
        txtNivArco =(TextView)findViewById(R.id.txtNivArco);
        txtNivEscudo =(TextView)findViewById(R.id.txtNivEscudo);
        txtNivGuantes =(TextView)findViewById(R.id.txtNivGuantes);
        txtNivBotas =(TextView)findViewById(R.id.txtNivBotas);
        txtNivFlechas =(TextView)findViewById(R.id.txtNivFlechas);
        btnCasco = (Button)findViewById(R.id.btnCasco);
        btnArco = (Button)findViewById(R.id.btnArco);
        btnEscudo = (Button)findViewById(R.id.btnEscudo);
        btnGuantes = (Button)findViewById(R.id.btnGuantes);
        btnBotas = (Button)findViewById(R.id.btnBotas);
        btnFlechas = (Button)findViewById(R.id.btnFlechas);
        btnIndex = (Button)findViewById(R.id.btnIndex);
        btnCasco.setOnClickListener(this);
        btnArco.setOnClickListener(this);
        btnEscudo.setOnClickListener(this);
        btnGuantes.setOnClickListener(this);
        btnBotas.setOnClickListener(this);
        btnFlechas.setOnClickListener(this);
        btnIndex.setOnClickListener(this);
        Personaje p=null;
        try {
            p = PersonajeDao.buscarPersonaje(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        txtPiedra.setText(p.getPiedra()+"");
        txtTablasMadera.setText(p.getTabla_madera()+"");
        txtLingoteHierro.setText(p.getLingote_hierro()+"");
        txtLingoteOro.setText(p.getLingote_oro()+"");
        txtGema.setText(p.getGema()+"");
        txtNivCasco.setText(p.getNivCasco()+"");
        txtNivArco.setText(p.getNivArco()+"");
        txtNivEscudo.setText(p.getNivEscudo()+"");
        txtNivGuantes.setText(p.getNivGuantes()+"");
        txtNivBotas.setText(p.getNivBotas()+"");
        txtNivFlechas.setText(p.getNivFlecha()+"");
    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        i = new Intent(this,MejorarArmas.class);
        switch (v.getId()){
            case R.id.btnCasco:
                i.putExtra("equipo","casco");
                startActivity(i);
                break;
            case R.id.btnArco:
                i.putExtra("equipo","arco");
                startActivity(i);
                break;
            case R.id.btnEscudo:
                i.putExtra("equipo","escudo");
                startActivity(i);
                break;
            case R.id.btnGuantes:
                i.putExtra("equipo","guantes");
                startActivity(i);
                break;
            case R.id.btnBotas:
                i.putExtra("equipo","botas");
                startActivity(i);
                break;
            case R.id.btnFlechas:
                i.putExtra("equipo","flechas");
                startActivity(i);
                break;
            case R.id.btnIndex:
                i = new Intent(this,Index.class);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activo){
            recreate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        activo=true;
    }
}
