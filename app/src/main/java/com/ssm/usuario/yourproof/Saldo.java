package com.ssm.usuario.yourproof;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Saldo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saldo);
    }

    public void Recargar(View view) {

        Intent intent = new Intent(this, Recargas.class);
        startActivity(intent);
    }
}
