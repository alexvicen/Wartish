package com.vicen.webel.components.wartish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vicen.webel.components.wartish.R;
import com.vicen.webel.components.wartish.constats.BBDDConstantes;
import com.vicen.webel.components.wartish.dao.PersonajeDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

public class Index extends AppCompatActivity implements View.OnClickListener {

    private Button btnCandy, btnJuegoPrincipal, btnHerreria, btnCerrarSesion, btnProcesar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        btnCandy = (Button) findViewById(R.id.btnCandy);
        btnHerreria = (Button) findViewById(R.id.btnHerreria);
        btnJuegoPrincipal = (Button) findViewById(R.id.btnJuegoPrincipal);
        btnCerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);
        btnProcesar = (Button) findViewById(R.id.btnProcesar);
        btnCandy.setOnClickListener(this);
        btnJuegoPrincipal.setOnClickListener(this);
        btnHerreria.setOnClickListener(this);
        btnCerrarSesion.setOnClickListener(this);
        btnProcesar.setOnClickListener(this);
        PersonajeDao.newPersonaje(this, 0, "nombre", 1, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.btnCandy:
                i = new Intent(this, TresLinea.class);
                startActivity(i);
                finish();
                break;
            case R.id.btnJuegoPrincipal:
                i = new Intent(this, JuegoPrincipal.class);
                startActivityForResult(i, 0);
                break;
            case R.id.btnHerreria:
                i = new Intent(this, Herreria.class);
                startActivity(i);
                finish();
                break;
            case R.id.btnProcesar:
                i = new Intent(this, ProcesarMateriales.class);
                startActivity(i);
                finish();
                break;
            case R.id.btnCerrarSesion:

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            int[] array = data.getIntArrayExtra("nivel");
            int exp = (array[0] + array[1] + array[2]) * 50;
            Intent i = new Intent(this, Experiencia.class);
            i.putExtra("exp", exp);
            startActivity(i);
        }
    }
}
