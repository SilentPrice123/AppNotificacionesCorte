package com.example.appnotificacionescorte;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class principal_usuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_usuario);
    }
    public void reportar(View v){
        Intent reportar = new Intent(principal_usuario.this,ReporteActivity.class);
        startActivity(reportar);
    }
}