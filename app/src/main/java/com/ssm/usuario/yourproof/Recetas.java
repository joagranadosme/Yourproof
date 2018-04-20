package com.ssm.usuario.yourproof;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by USUARIO on 27/06/2016.
 */
public class Recetas extends Activity {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recetas);
    }

    public void AlimentosC(View view) {

        //img = (ImageView) findViewById(R.id)

        //Iniciando la actividad
        Intent intent = new Intent(this, Items_recetas_1.class);
        intent.putExtra("tipo","AlimentosCalientes");
        startActivity(intent);

    }

    public void AlimentosF(View view) {

        Intent intent = new Intent(this, Items_recetas_1.class);
        //Bitmap bitmap = BitmapFactory.decodeResource (getResources(), R.drawable.comidafria); //imagen

        intent.putExtra("tipo","AlimentosFrios");
        startActivity(intent);
    }

    public void Postres(View view) {

        Intent intent = new Intent(this, Items_recetas_1.class);
        intent.putExtra("tipo","Postres");
        startActivity(intent);
    }

    public void Panaderia(View view) {

        Intent intent = new Intent(this, Items_recetas_1.class);
        intent.putExtra("tipo","Panaderia");
        startActivity(intent);
    }

    public void Cocteles(View view) {

        Intent intent = new Intent(this, Items_recetas_1.class);
        intent.putExtra("tipo","Cocteles");
        startActivity(intent);
    }

    public void Bebidas(View view) {

        Intent intent = new Intent(this, Items_recetas_1.class);
        intent.putExtra("tipo","Bebidas");
        startActivity(intent);
    }

    public void Otros(View view) {

        Intent intent = new Intent(this, Items_recetas_1.class);
        intent.putExtra("tipo","Otros");
        startActivity(intent);
    }
}
