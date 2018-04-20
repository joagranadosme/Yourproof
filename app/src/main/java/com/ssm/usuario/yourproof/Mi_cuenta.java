package com.ssm.usuario.yourproof;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USUARIO on 28/06/2016.
 */
public class Mi_cuenta extends Activity {

    //idUser
    private int idUser1;

    //JSON
    JSONObject mJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_cuenta);


        Intent myIntent = getIntent();
        idUser1 = myIntent.getIntExtra("id",0);

        String aux = idUser1+"";
        //Toast.makeText(getApplicationContext(),aux,Toast.LENGTH_LONG).show();

        new CuentaConect(getApplicationContext(),this).execute(new String(idUser1+""));


    }

    public void Editar(View view){

        Intent intent = new Intent(this, Editar_Cuenta.class);
        intent.putExtra("id",idUser1);
        startActivity(intent);

        //Toast.makeText(getApplicationContext(),"Editar",Toast.LENGTH_LONG).show();
        /*TextView eMail = (TextView)  findViewById(R.id.Email_cuenta);
        TextView nombre = (TextView) findViewById(R.id.editText);
        TextView apellido = (TextView) findViewById(R.id.editText2);
        TextView contraseña = (TextView) findViewById(R.id.editText3);
        TextView celular = (TextView) findViewById(R.id.editText4);
        TextView fecha = (TextView) findViewById(R.id.editText5);

        new CuentaEdita(getApplicationContext(),this).execute(idUser1+"",eMail.getText().toString(),nombre.getText().toString(),apellido.getText().toString(),contraseña.getText().toString(),celular.getText().toString(),fecha.getText().toString());
*/

    }

    public void CerrarS(View view){
        Toast.makeText(getApplicationContext(),"Cerrar",Toast.LENGTH_LONG).show();
    }

}

class CuentaConect extends AsyncTask<String, Void, JSONObject> {

    private Context mContext;
    private Activity mActivity;

    public CuentaConect(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
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

        TextView eMail = (TextView) mActivity.findViewById(R.id.Email_cuenta);
        TextView nombre = (TextView) mActivity.findViewById(R.id.editText);
        TextView nombreE = (TextView) mActivity.findViewById(R.id.TvE_NomCuenta);
        TextView apellido = (TextView) mActivity.findViewById(R.id.editText2);
        TextView celular = (TextView) mActivity.findViewById(R.id.editText4);
        TextView fecha = (TextView) mActivity.findViewById(R.id.editText5);

        try{
            eMail.setText(jsonObject.getString("eMail"));
            nombre.setText(jsonObject.getString("nombre"));
            nombreE.setText(jsonObject.getString("nombre"));
            apellido.setText(jsonObject.getString("apellido"));
            celular.setText(jsonObject.getString("celular"));
            fecha.setText(jsonObject.getString("fecha"));

        }catch (JSONException e){
            Toast.makeText(mContext,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
}


/*class CuentaEdita extends AsyncTask<String, Void, String> {

    private Context mContext;
    private Activity mActivity;

    public CuentaEdita(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
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

        Toast.makeText(mContext,s,Toast.LENGTH_LONG).show();
    }
}*/