package com.ssm.usuario.yourproof;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.telecom.Connection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_CONTACTS;

public class Login_inicio extends Activity {

    private static final int REQUEST_READ_CONTACTS = 0;
    private ImageButton Btnreg;

    private AutoCompleteTextView auto_mail;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_inicio);

        auto_mail = (AutoCompleteTextView) findViewById(R.id.TxtEmail_login);
        addAdapterToViews();
    }

    public void Send(View view) {

        //Iniciando la actividad
        Intent intent = new Intent(this, Registrarse.class);
        //Btnreg = (ImageButton) findViewById(R.id.BtnRegistrarse_login);
        //Btnreg.setImageResource(R.drawable.registrarse2);
        startActivity(intent);



        //Btnreg.setImageResource(R.drawable.registrarse);
        //Btnreg.setVisibility(View.VISIBLE);

    }

    public void Iniciarsesion(View view) {
        EditText correoS = (EditText) findViewById(R.id.TxtEmail_login);
        String correo = correoS.getText().toString();
        EditText passS = (EditText) findViewById(R.id.TxtContraseña_login);
        String pass = passS.getText().toString();

        //boolean flag=false;
        new ConecTask(getApplicationContext(),this).execute(correo, pass);
    }

    //RECORDAR EMAILS

    private void addAdapterToViews() {

        Account[] accounts = AccountManager.get(this).getAccounts();
        Set<String> emailSet = new HashSet<String>();
        for (Account account : accounts) {
            if (EMAIL_PATTERN.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }
        auto_mail.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));

    }
//FIN RECORDAR EMAILS
}



class ConecTask extends AsyncTask<String,Void,String> {

    private ProgressDialog progressDialog;
    private Context mContext;
    private int idUser;

    public ConecTask(Context context, Login_inicio activity) {
        mContext = context;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Logueando...");
        progressDialog.show();
    }

    public String getJsonData(String JsonString) throws JSONException {
        final String LOG = "Log";
        final String ID = "id";

        JSONObject myJson = new JSONObject(JsonString);
        idUser = Integer.parseInt(myJson.get(ID).toString());
        return myJson.get(LOG).toString();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlC = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            String myUrl = "http://www.yourproofserver.com/API.php/?q=login";
            String eMail = "eMail";
            String password = "password";

            Uri builtUri = Uri.parse(myUrl).buildUpon().appendQueryParameter(eMail, params[0]).appendQueryParameter(password, params[1]).build();
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

            try {
                return getJsonData(jsonString);
            } catch (JSONException e) {
                return e.toString();
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s.equals("true")) {
            Intent intent = new Intent(mContext, Menu_principal.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id", idUser);
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
        }
        progressDialog.dismiss();
    }
}



