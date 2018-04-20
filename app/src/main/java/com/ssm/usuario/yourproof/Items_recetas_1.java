package com.ssm.usuario.yourproof;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
public class Items_recetas_1 extends AppCompatActivity {
    private String tipo;
    private ListView mList;
    private TextView titulo;
    private ImageView Img;
    public static ArrayList<String> mArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_recetas_1);
        mList = (ListView) findViewById(R.id.Lst_items);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lanzarVideo(Integer.parseInt(mArray.get(position)));
            }
        });
        Intent intent = getIntent();
        tipo = intent.getStringExtra("tipo");
        switch (tipo){
            case "AlimentosCalientes":
                Img = (ImageView) findViewById(R.id.Img_titulo_lista);
                titulo = (TextView) findViewById(R.id.Tv_Titulo_lista);
                Img.setImageResource(R.drawable.alimentoscalientes);
                titulo.setText("ALIMENTOS CALIENTES");
                new getRecetaTask(getApplicationContext(),this).execute();
                break;
            case "AlimentosFrios":
                Img = (ImageView) findViewById(R.id.Img_titulo_lista);
                Img.setImageResource(R.drawable.comidafria);
                titulo = (TextView) findViewById(R.id.Tv_Titulo_lista);
                titulo.setText("ALIMENTOS FRIOS");
                new getRecetaTask(getApplicationContext(),this).execute();
                break;
            case "Postres":
                Img = (ImageView) findViewById(R.id.Img_titulo_lista);
                Img.setImageResource(R.drawable.postres);
                titulo = (TextView) findViewById(R.id.Tv_Titulo_lista);
                titulo.setText("POSTRES");
                new getRecetaTask(getApplicationContext(),this).execute();
                break;
            case "Panaderia":
                Img = (ImageView) findViewById(R.id.Img_titulo_lista);
                Img.setImageResource(R.drawable.panaderias);
                titulo = (TextView) findViewById(R.id.Tv_Titulo_lista);
                titulo.setText("PANADERIA");
                new getRecetaTask(getApplicationContext(),this).execute();
                break;
            case "Cocteles":
                Img = (ImageView) findViewById(R.id.Img_titulo_lista);
                Img.setImageResource(R.drawable.cocteles);
                titulo = (TextView) findViewById(R.id.Tv_Titulo_lista);
                titulo.setText("COCTELES");
                new getRecetaTask(getApplicationContext(),this).execute();
                break;
            case "Bebidas":
                Img = (ImageView) findViewById(R.id.Img_titulo_lista);
                Img.setImageResource(R.drawable.bebidas);
                titulo = (TextView) findViewById(R.id.Tv_Titulo_lista);
                titulo.setText("BEBIDAS");
                new getRecetaTask(getApplicationContext(),this).execute();
                break;
            case "Otros":
                Img = (ImageView) findViewById(R.id.Img_titulo_lista);
                Img.setImageResource(R.drawable.otros);
                titulo = (TextView) findViewById(R.id.Tv_Titulo_lista);
                titulo.setText("OTROS");
                new getRecetaTask(getApplicationContext(),this).execute();
                break;
        }
    }
    public void lanzarVideo(int id){
        Intent videoIntent1 = new Intent(this,Receta_Descripcion.class);
        videoIntent1.putExtra("id",id);
        startActivity(videoIntent1);
    }
}
class getRecetaTask extends AsyncTask<Void,Void,ArrayList<ArrayList<String>>> {
    private Context mContext;
    private Activity mActivity;
    public getRecetaTask(Context context, Activity activity){
        mContext = context;
        mActivity = activity;
    }
    public ArrayList<ArrayList<String>> getJsonData(String JsonString) throws JSONException {
        ArrayList<ArrayList<String>> lista = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        JSONArray myJson = new JSONArray(JsonString);
        if(myJson != null){
            for(int i=0; i<myJson.length();i++){
                JSONObject jsonObject = myJson.getJSONObject(i);
                nombres.add(jsonObject.get("nombre").toString());
                ids.add(jsonObject.get("id").toString());
            }
        }
        lista.add(ids);
        lista.add(nombres);
        return lista;
    }
    @Override
    protected ArrayList<ArrayList<String>> doInBackground(Void... params) {
        HttpURLConnection urlC = null;
        BufferedReader reader = null;
        String jsonString = null;
        try {
            String myUrl = "http://www.yourproofserver.com/API.php/?q=getRecetas";
            Uri builtUri = Uri.parse(myUrl).buildUpon().build();
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
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
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
    protected void onPostExecute(ArrayList<ArrayList<String>> arrayList) {
        super.onPostExecute(arrayList);
        Items_recetas_1.mArray = arrayList.get(0);
        if(arrayList == null){
            Toast.makeText(mContext,"Null",Toast.LENGTH_LONG).show();
        }else{
            ListView myList = (ListView) mActivity.findViewById(R.id.Lst_items);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1, arrayList.get(1)){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    /// Get the Item from ListView
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    // Set the text size 25 dip for ListView each item
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,11);
                    // Return the view
                    return view;
                }
            };
            myList.setAdapter(arrayAdapter);
        }
    }
}