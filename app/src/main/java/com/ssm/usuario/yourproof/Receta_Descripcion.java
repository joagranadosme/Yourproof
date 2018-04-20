package com.ssm.usuario.yourproof;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by USUARIO on 18/07/2016.
 */
public class Receta_Descripcion extends YouTubeBaseActivity {

    private int idReceta;
    public static String urlVIdeo;
    private YouTubePlayerView you;
    private YouTubePlayer.OnInitializedListener ini;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receta_descripcion);

        Intent mIntent = getIntent();
        idReceta = mIntent.getIntExtra("id",0);

        new getVideoTask(getApplicationContext(),this,this).execute(idReceta);
    }


    public void lanzarVideo(View view){
        //Toast.makeText(this,"Boton",Toast.LENGTH_LONG).show();

        ImageButton ib = (ImageButton) findViewById(R.id.Btn_Video);
        ib.getLayoutParams().height=0;
        ib.getLayoutParams().width=0;
        ib.setVisibility(View.GONE);

        String passwordApi = "AIzaSyAryM60MuTILF3y93HE8DJlZdjkDsJ2QVI";

        LinearLayout ly = (LinearLayout) findViewById(R.id.linearLayout1);

        you = (YouTubePlayerView) findViewById(R.id.view);
        you.getLayoutParams().height = ly.getHeight();
        you.getLayoutParams().width = ly.getWidth();
        you.setVisibility(View.VISIBLE);

        ini = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(urlVIdeo);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(), "Video no avaliable", Toast.LENGTH_LONG).show();
            }
        };
        you.initialize(passwordApi,ini);
    }
}

class getVideoTask extends AsyncTask<Integer,Void,String[]> {

    private String passwordApi = "AIzaSyAryM60MuTILF3y93HE8DJlZdjkDsJ2QVI";
    private Context mContext;
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private int idReceta;
    private Receta_Descripcion mReceta_descripcion;

    public getVideoTask(Context context, Activity activity,Receta_Descripcion receta){
        mContext = context;
        mActivity = activity;
        mReceta_descripcion = receta;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mReceta_descripcion);
        mProgressDialog.setMessage("Cargando...");
        mProgressDialog.show();
    }

    public String[] getJsonData(String JsonString) throws JSONException {
        String[] data = new String[2];
        final String NAME = "nombre";
        final String VIDEO = "video";

        JSONObject myJson = new JSONObject(JsonString);
        data[0] = myJson.get(NAME).toString();
        data[1] = myJson.get(VIDEO).toString();
        return data;
    }

    @Override
    protected String[] doInBackground(Integer... params) {

        HttpURLConnection urlC = null;
        BufferedReader reader = null;
        String jsonString = null;
        idReceta = params[0];

        try {
            String myUrl = "http://www.yourproofserver.com/API.php/?q=getInfoReceta";
            String ID = "id";

            Uri builtUri = Uri.parse(myUrl).buildUpon().appendQueryParameter(ID, params[0]+"").build();
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
            if(urlC != null){
                urlC.disconnect();
            }

            if(reader != null){
                try{
                    reader.close();
                }catch (IOException e){
                    Log.e("TAG","ERROR CLOSING",e);
                }
            }

            try{
                return getJsonData(jsonString);
            }catch (JSONException e){
                return null;
            }
        }
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        TextView nombre = (TextView) mActivity.findViewById(R.id.Tv_Nombre_Receta);
        nombre.setText(strings[0]);

        Receta_Descripcion.urlVIdeo = strings[1];

        new getDescripcion(mContext,mActivity,mProgressDialog).execute(idReceta);

    }
}

class getDescripcion extends AsyncTask<Integer,Void,String> {

    private Context mContext;
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private int idReceta;


    public getDescripcion(Context context, Activity activity,ProgressDialog progressDialog ){
        mContext = context;
        mActivity = activity;
        mProgressDialog = progressDialog;
    }

    @Override
    protected String doInBackground(Integer... params) {

        idReceta = params[0];
        HttpURLConnection urlC = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            String myUrl = "http://www.yourproofserver.com/API.php/?q=getDescripcionReceta";
            String ID = "id";

            Uri builtUri = Uri.parse(myUrl).buildUpon().appendQueryParameter(ID, params[0] + "").build();
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

        TextView descripcion = (TextView) mActivity.findViewById(R.id.Tv_Descripcion);
        descripcion.setText(s);

        new getImagen(mContext,mActivity,mProgressDialog).execute(idReceta);

    }
}

class getImagen extends AsyncTask<Integer,Void,Bitmap> {

    private Context mContext;
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private int idReceta;

    public getImagen(Context context, Activity activity, ProgressDialog progressDialog){
        mContext = context;
        mActivity = activity;
        mProgressDialog = progressDialog;
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {

        idReceta = params[0];
        HttpURLConnection urlC = null;
        BufferedReader reader = null;
        String jsonString = null;

        try{
            String myUrl = "http://www.yourproofserver.com/API.php/?q=getImg";
            String ID = "id";
            Uri builtUri = Uri.parse(myUrl).buildUpon().appendQueryParameter(ID, params[0] + "").build();
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

            URL img = new URL("http://www.yourproofserver.com/imagenes/"+jsonString);
            Bitmap bmp = BitmapFactory.decodeStream(img.openConnection().getInputStream());
            return bmp;

        }catch (Exception e){
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if(bitmap != null) {
            ImageButton mImageButton = (ImageButton) mActivity.findViewById(R.id.Btn_Video);
            mImageButton.setImageBitmap(bitmap);
        }else{
            Toast.makeText(mContext,"Error",Toast.LENGTH_LONG).show();
        }
        new getAutor(mContext,mActivity,mProgressDialog).execute(idReceta);
    }
}

class getAutor extends AsyncTask<Integer,Void,String[]>{

    private Context mContext;
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private int idReceta;

    public getAutor(Context context, Activity activity, ProgressDialog progressDialog){
        mContext = context;
        mActivity = activity;
        mProgressDialog = progressDialog;
    }

    public String[] getJsonData(String jsonString){
        String[] datos = new String[3];
        String user = "usuario";
        String level = "nivel";
        String type = "tipo";

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            datos[0] = jsonObject.getString(user).toString();
            datos[1] = jsonObject.getString(level).toString();
            datos[2] = jsonObject.getString(type).toString();

        }catch (Exception e){
            datos[0] = "Error";
            datos[1] = "Error";
            datos[2] = "Error";
        }

        return datos;
    }

    @Override
    protected String[] doInBackground(Integer... params) {
        idReceta = params[0];
        HttpURLConnection urlC = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            String myUrl = "http://www.yourproofserver.com/API.php/?q=getAutorReceta";
            String ID = "idReceta";

            Uri builtUri = Uri.parse(myUrl).buildUpon().appendQueryParameter(ID, params[0] + "").build();
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
            return getJsonData(jsonString);
        }
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        TextView autor = (TextView) mActivity.findViewById(R.id.Tv_Autor);
        TextView nivel = (TextView) mActivity.findViewById(R.id.Tv_Nivel);
        TextView tipo = (TextView) mActivity.findViewById(R.id.Tv_Tipopreparacion);

        autor.setText("Autor: " + strings[0]);
        nivel.setText("Nivel del autor: " + strings[1]);
        tipo.setText("Tipo de preparación: " + strings[2]);

        mProgressDialog.dismiss();
    }
}