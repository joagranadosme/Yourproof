package com.ssm.usuario.yourproof;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Editar_Cuenta extends AppCompatActivity {

    //idUser
    private int idUser;

    //JSON
    JSONObject mJson;

    Spinner listadias;
    String[] datosdias = new String[33];
    //Lista meses
    Spinner listameses;
    String[] datosmeses = {"Mes","Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
    //Lista Año
    Spinner listaaño;
    String[] datosaño = new String[101];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_cuenta);

        Intent myIntent = getIntent();
        idUser = myIntent.getIntExtra("id",0);

        String aux = idUser+"";
        //Toast.makeText(getApplicationContext(),aux,Toast.LENGTH_LONG).show();

        new CuentaConec(getApplicationContext(),this,this).execute(new String(idUser+""));

        for(int i=0;i<=31;i++){

            datosdias[i+1] = String.valueOf(i);
//            Toast.makeText(this,i,Toast.LENGTH_LONG);
        }
        datosdias[0]="Dia";
        listadias = (Spinner) findViewById(R.id.Lst_dias);
        ArrayAdapter adaptador = new ArrayAdapter(this, android.R.layout.simple_spinner_item,datosdias);
        listadias.setAdapter(adaptador);

        //Creacion lista meses
        listameses = (Spinner) findViewById(R.id.Lst_meses);
        ArrayAdapter adaptador1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item,datosmeses);
        listameses.setAdapter(adaptador1);

        //Creacion lista año
        for(int j=0;j<=99;j++){

            datosaño[j+1] = String.valueOf(j+1902);
        }
        datosaño[0]="Año";
        listaaño = (Spinner) findViewById(R.id.Lst_años);
        ArrayAdapter adaptador2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item,datosaño);
        listaaño.setAdapter(adaptador2);

        //Fin spinner


    }

    public void Guardartodo(View view){
        //Toast.makeText(getApplicationContext(),"Editar",Toast.LENGTH_LONG).show();
        TextView eMail = (TextView) findViewById(R.id.Tv_Email);
        TextView nombre = (TextView) findViewById(R.id.E_Txt_Nombre);
        TextView apellido = (TextView) findViewById(R.id.E_Txt_Apellido);
        TextView contraseña = (TextView) findViewById(R.id.E_Txt_Contraseña);
        TextView celular = (TextView) findViewById(R.id.E_Txt_Celular);

        String seledias = listadias.getSelectedItem().toString();
        int pos = listameses.getSelectedItemPosition();
        String seleaño = listaaño.getSelectedItem().toString();
        String Fechac = seleaño+"-"+pos+"-"+seledias;

        //Toast.makeText(getApplicationContext(),Fechac,Toast.LENGTH_LONG).show();

        new CuentaEdit(getApplicationContext(),this,this).execute(idUser+"",eMail.getText().toString(),nombre.getText().toString(),apellido.getText().toString(),contraseña.getText().toString(),celular.getText().toString(),Fechac);


    }
    public void AgregarD(View view){
        Intent intent = new Intent(this, Documentos.class);
        startActivity(intent);
    }
}

class CuentaConec extends AsyncTask<String, Void, JSONObject> {

    private ProgressDialog progressDialog;
    private Context mContext;
    private Activity mActivity;

    public CuentaConec(Context context, Activity activity,Editar_Cuenta cuenta) {
        mContext = context;
        mActivity = activity;
        progressDialog = new ProgressDialog(cuenta);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Cargando...");
        progressDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpURLConnection urlC = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            String myUrl = "http://www.yourproofserver.com/API.php/?q=infoUser";
            String id = "id";

            Uri builtUri = Uri.parse(myUrl).buildUpon().appendQueryParameter(id, params[0]).build();
            URL url = new URL(builtUri.toString());
            urlC = (HttpURLConnection) url.openConnection();
            urlC.setRequestMethod("GET");
            urlC.connect();

            InputStream inputStream = urlC.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            jsonString = buffer.toString();


        } catch (Exception e) {
            Toast toast = Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG);
            toast.show();
        } finally {
            if (urlC != null) {
                urlC.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("TAG", "ERROR CLOSING", e);
                }
            }

            try{
                return new JSONObject(jsonString);
            }catch (JSONException e){
                return null;
            }
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);

        TextView eMail = (TextView) mActivity.findViewById(R.id.Tv_Email);
        TextView nombre = (TextView) mActivity.findViewById(R.id.E_Txt_Nombre);
        TextView nombreE = (TextView) mActivity.findViewById(R.id.TvE_NomUsu);
        TextView apellido = (TextView) mActivity.findViewById(R.id.E_Txt_Apellido);
        TextView contraseña = (TextView) mActivity.findViewById(R.id.E_Txt_Contraseña);
        TextView celular = (TextView) mActivity.findViewById(R.id.E_Txt_Celular);
        //TextView fecha = (TextView) mActivity.findViewById(R.id.Tv_Fecha);

        Spinner dia = (Spinner) mActivity.findViewById(R.id.Lst_dias);
        Spinner mes = (Spinner) mActivity.findViewById(R.id.Lst_meses);
        Spinner año = (Spinner) mActivity.findViewById(R.id.Lst_años);

        try{
            String[] dates;
            eMail.setText(jsonObject.getString("eMail"));
            nombre.setText(jsonObject.getString("nombre"));
            nombreE.setText(jsonObject.getString("nombre"));
            apellido.setText(jsonObject.getString("apellido"));
            contraseña.setText(jsonObject.getString("password"));
            celular.setText(jsonObject.getString("celular"));
            dates=(jsonObject.getString("fecha").toString().split("-"));
            dia.setSelection(Integer.parseInt(dates[2])+1);
            mes.setSelection(Integer.parseInt(dates[1]));
            año.setSelection(Integer.parseInt(dates[0])-1901);


        }catch (JSONException e){
            Toast.makeText(mContext,e.toString(),Toast.LENGTH_LONG).show();
        }
        progressDialog.dismiss();
    }
}


class CuentaEdit extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;
    private Context mContext;
    private Activity mActivity;

    public CuentaEdit(Context context, Activity activity, Editar_Cuenta cuenta) {
        mContext = context;
        mActivity = activity;
        progressDialog = new ProgressDialog(cuenta);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Guardando...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlC = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            String myUrl = "http://www.yourproofserver.com/API.php/?q=updateUser";
            String ID = "id";
            String MAIL = "eMail";
            String NOMBRE = "nombre";
            String APELLIDO = "apellido";
            String PASS = "password";
            String CEL = "celular";
            String DATE = "fecha";

            Uri builtUri = Uri.parse(myUrl).buildUpon().appendQueryParameter(ID, params[0]).appendQueryParameter(MAIL, params[1]).appendQueryParameter(NOMBRE, params[2]).appendQueryParameter(APELLIDO, params[3]).appendQueryParameter(PASS, params[4]).appendQueryParameter(CEL, params[5]).appendQueryParameter(DATE, params[6]).build();
            URL url = new URL(builtUri.toString());
            urlC = (HttpURLConnection) url.openConnection();
            urlC.setRequestMethod("GET");
            urlC.connect();

            InputStream inputStream = urlC.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            jsonString = buffer.toString();


        } catch (Exception e) {
            Toast toast = Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG);
            toast.show();
        } finally {
            if (urlC != null) {
                urlC.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("TAG", "ERROR CLOSING", e);
                }
            }

            return jsonString;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
    }
}
