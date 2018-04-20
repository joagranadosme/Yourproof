package com.ssm.usuario.yourproof;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Crear_Receta extends AppCompatActivity {

    public int idUser;
    public static int idReceta;

    private static final int REQUEST_PATH = 1;
    private static final int PICK_FILE_REQUEST = 2;
    private static final int FILL_DATA = 3;

    private static final String TAG = Crear_Receta.class.getSimpleName();

    private String SERVER_URL = "http://www.yourproofserver.com/API.php/?q=uploadImg";

    String curFileName;
    String curPathName;
    String nombreReceta;
    String descripcionReceta;
    String ingredientesReceta;


    ProgressDialog progressDialog;
    ImageView imageView;
    TextView textView;
    Spinner Tiporeceta;
    String[] Datosreceta = {"Selecciona el tipo de preparación","Alimentos Calientes","Alimentos Frios","Postres","Panaderia y Pasteleria","Cocteles","Bebidas","Otros"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_receta);

        Intent intent = getIntent();
        idUser = intent.getIntExtra("id",0);

        imageView = (ImageView) findViewById(R.id.imageView25);
        textView = (TextView) findViewById(R.id.textView25);

        Tiporeceta = (Spinner) findViewById(R.id.Lst_Tipo_receta);
        ArrayAdapter adaptador = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Datosreceta);
        Tiporeceta.setAdapter(adaptador);
    }

    public void crearReceta(View view){

        new crearRecetaTask(idUser,getApplicationContext(),this).execute(nombreReceta,descripcionReceta+"\n"+ingredientesReceta,"Postres");

        Toast.makeText(getApplicationContext(),idReceta+"",Toast.LENGTH_LONG).show();


        //on upload button Click
        if(curPathName != null){
            progressDialog = ProgressDialog.show(Crear_Receta.this,"","Creando receta...",true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //creating new thread to handle Http Operations
                    uploadFile(curPathName,idReceta);
                }
            }).start();
        }else{
            Toast.makeText(Crear_Receta.this,"¡Debes escoger una imagen para tu receta!",Toast.LENGTH_SHORT).show();
        }

    }

    public int uploadFile(final String selectedFilePath, int idReceta){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            progressDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                Uri build = Uri.parse(SERVER_URL).buildUpon().appendQueryParameter("idReceta",(idReceta+1)+"").build();
                URL url = new URL(build.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Crear_Receta.this,"FIle Upload Complete",Toast.LENGTH_LONG).show();
                            //tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + ""+ fileName);
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Crear_Receta.this,"File Not Found",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(Crear_Receta.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Crear_Receta.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }



    public void adjuntarFoto(View view){
        //Toast.makeText(getApplicationContext(),"Foto",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, itemLista.class);
        startActivityForResult(intent,REQUEST_PATH);
    }

    public void Descripcion(View view) {

        //Iniciando la actividad tocando la pantalla
        Intent intent = new Intent(this, Detalle_descripcion.class);
        startActivityForResult(intent,FILL_DATA);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        if (requestCode == REQUEST_PATH) {
            if (resultCode == RESULT_OK) {
                curPathName = data.getStringExtra("GetPath");
                curFileName = data.getStringExtra("GetFileName");
                imageView = (ImageView) findViewById(R.id.imageView25);

                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(curPathName);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }

        if(requestCode == PICK_FILE_REQUEST){
            if(data == null){
                //no data present
                return;
            }

            if(curPathName!= null && !curPathName.equals("")){
                TextView textView = (TextView) findViewById(R.id.textView29);
                textView.setText(curFileName);
            }else{
                Toast.makeText(this,"Cannot upload file to server",Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == FILL_DATA){
            nombreReceta = data.getStringExtra("nombre");
            descripcionReceta = data.getStringExtra("descripcion");
            ingredientesReceta = data.getStringExtra("ingredientes");

            /*Toast.makeText(getApplicationContext(),nombreReceta,Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),descripcionReceta,Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),ingredientesReceta,Toast.LENGTH_LONG).show();*/
        }
    }
}


class crearRecetaTask extends AsyncTask<String,Void,String>{

    private int idUser;
    private Context context;
    private Activity activity;

    private ProgressDialog progressDialog;

    public crearRecetaTask(int user, Context mcContext, Activity mActivity){
        idUser = user;
        context = mcContext;
        activity = mActivity;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Creando receta...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlC = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            String myUrl = "http://www.yourproofserver.com/API.php/?q=addReceta";
            String user = "idUser";
            String nombre = "nombre";
            String descripcion = "descripcion";
            String tipo = "tipo";

            Uri builtUri = Uri.parse(myUrl).buildUpon().appendQueryParameter(user, idUser+"").appendQueryParameter(nombre, params[0]).appendQueryParameter(descripcion, params[1]).appendQueryParameter(tipo, params[2]).build();
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
            Toast toast = Toast.makeText(context, e.toString(), Toast.LENGTH_LONG);
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
        Crear_Receta.idReceta = Integer.parseInt(s.substring(0,s.length()-1));
        progressDialog.dismiss();
    }
}
