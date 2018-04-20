package com.ssm.usuario.yourproof;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Detalle_descripcion extends AppCompatActivity {

    EditText Ingrediente;
    Button Agregar_i;
    EditText Descripcion;
    EditText Peso;
    EditText Nombre;
    EditText Blancox;

    String nombreReceta;
    String descripcionReceta;
    String ingredientesReceta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_descripcion);

        Nombre = (EditText) findViewById(R.id.Txt_Nombre_receta);
        Ingrediente = (EditText)findViewById(R.id.Txt_Ingredientes);
        Peso = (EditText)findViewById(R.id.Txt_Cpeso);
        Agregar_i = (Button)findViewById(R.id.Btn_Ingrediente);
        Descripcion = (EditText)findViewById(R.id.TxtDescripcion_Receta);
        Blancox = (EditText) findViewById(R.id.Txt_Cingrediente);

        Agregar_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ingre = Ingrediente.getText().toString();
                String cadena = Blancox.getText().toString()+"\t"+Peso.getText().toString();
                Ingrediente.setText(ingre+" \t\t "+cadena+"grs\n");
                Blancox.setText("");
                Peso.setText("");
            }
        });
    }

    public void guardar(View view){
        nombreReceta = Nombre.getText().toString();
        descripcionReceta = Descripcion.getText().toString();
        ingredientesReceta = Ingrediente.getText().toString();

        if(nombreReceta.equals("") || descripcionReceta.equals("") || ingredientesReceta.equals(""))
            Toast.makeText(getApplicationContext(),"Rellena todo los campos",Toast.LENGTH_LONG).show();
        else{
            Intent intent = new Intent();
            intent.putExtra("nombre",nombreReceta);
            intent.putExtra("descripcion",descripcionReceta);
            intent.putExtra("ingredientes",ingredientesReceta);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


}
