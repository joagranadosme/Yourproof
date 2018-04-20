package com.ssm.usuario.yourproof;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import static android.view.View.VISIBLE;

/**
 * Created by USUARIO on 10/06/2016.
 */
public class Menu_principal extends Activity {

    //User id
    private int idUser;

    TextView TVChefs;     //desclaramos nuestroTEXTVIEW de nuestro LAYOUT
    TextView TVRecetas;
    TextView TVEstableciemientos;
    TextView TVProductos;
    ImageButton Productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);

        //Pasar el id del Login
        Intent myIntent = getIntent();
        idUser = myIntent.getIntExtra("id",0);

        //botones para efecto click
        Productos = (ImageButton) findViewById(R.id.BtnProductos);

        //tipo de letra
        TVChefs = (TextView)findViewById(R.id.VChefs);
        TVRecetas = (TextView)findViewById(R.id.VRecetas);
        TVEstableciemientos = (TextView)findViewById(R.id.VEstablecimientos);
        TVProductos = (TextView)findViewById(R.id.VProductos);
        //definimos en ONCREATE donde esta por                                                                                                     //medio de un ID

        String font_path = "font/phantom.otf";  //definimos un STRING con el valor PATH ( o ruta por                                                                                    //donde tiene que buscar ) de nuetra fuente

        Typeface TF = Typeface.createFromAsset(getAssets(),font_path);

        //llamanos a la CLASS TYPEFACE y la definimos con un CREATE desde                                                    //ASSETS con la ruta STRING
/*
        TVChefs.setTypeface(TF);   //finalmente aplicamos TYPEFACE al TEXTVIEW
        TVRecetas.setTypeface(TF);
        TVEstableciemientos.setTypeface(TF);
        TVProductos.setTypeface(TF);
    */}


    //Intent para todos los botones
    public void Productos(View view) {

        //Iniciando la actividad
        Intent intent = new Intent(this, Productos.class);
        startActivity(intent);
        //Productos.setScaleType(matrix);

    }
    public void Establecimiento(View view) {

        //Iniciando la actividad ESTABLECIEMINTOS
        Intent intent = new Intent(this, Establecimiento.class);
        startActivity(intent);

    }
    public void Recetas(View view) {

        //Iniciando la actividad
        Intent intent = new Intent(this, Recetas.class);
        startActivity(intent);

    }

    public void Chefs(View view) {

        //Iniciando la actividad
        Intent intent = new Intent(this, Items_chefs.class);

        startActivity(intent);

    }
    public void CrearReceta(View view) {

        //Iniciando la actividad
        Intent intent = new Intent(this, Crear_Receta.class);
        intent.putExtra("tipo","Chefs");
        intent.putExtra("id",idUser);
        startActivity(intent);

    }

    public void Cuenta(View view) {

        //Iniciando la actividad
        Intent intent = new Intent(this, Mi_cuenta.class);
        intent.putExtra("id",idUser);
        startActivity(intent);

    }

    public void Saldo(View view) {

        //Iniciando la actividad
        Intent intent = new Intent(this, Saldo.class);
        startActivity(intent);
        //Productos.setScaleType(matrix);

    }

}