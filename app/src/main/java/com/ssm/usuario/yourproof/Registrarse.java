package com.ssm.usuario.yourproof;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Registrarse extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mNombre;
    private EditText mApellido;
    private EditText mCelular;
    private EditText mDate;
    private View mProgressView;
    private View mLoginFormView;

    //Lista dias
    Spinner listadias;
    String[] datosdias = new String[33];

    //Lista meses
    Spinner listameses;
    String[] datosmeses = {"Mes","Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
    //Lista Año
    Spinner listaaño;
    String[] datosaño = new String[101];

    String[] FechaC = new String[8];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrarse);
        //setupActionBar();

        // Set up the iniciologin form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.TxtEmail);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.TxtContraseña);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        ImageButton mEmailSignInButton = (ImageButton) findViewById(R.id.BtnRegistrarse);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //Creacion lista dias
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


    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Set up the {@link ActionBar}, if the API is available.
     */
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }*/

    /**
     * Attempts to sign in or register the account specified by the iniciologin form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual iniciologin attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the iniciologin attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mNombre = (EditText) findViewById(R.id.TxtNombre);
            mApellido = (EditText) findViewById(R.id.TxtApellido);
            mEmailView = (AutoCompleteTextView) findViewById(R.id.TxtEmail);
            mPasswordView = (EditText) findViewById(R.id.TxtContraseña);
            mCelular = (EditText) findViewById(R.id.TxtCelular);
//          mDate = (EditText) findViewById(R.id.tvDate);

            String seledias = listadias.getSelectedItem().toString();
            int pos = listameses.getSelectedItemPosition();
            String seleaño = listaaño.getSelectedItem().toString();
            String Fechac = seleaño+"-"+pos+"-"+seledias;

            Toast.makeText(getApplicationContext(),Fechac,Toast.LENGTH_LONG).show();

            new UserLoginTask(getApplicationContext())
                    .execute(mNombre.getText().toString(),
                            mApellido.getText().toString(),
                            mEmailView.getText().toString(),
                            mPasswordView.getText().toString(),
                            Fechac,
                            mCelular.getText().toString()
                    );
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the iniciologin form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Registrarse.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous iniciologin/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Void, String> {

        private Context mContext;

        UserLoginTask(Context context) {
            mContext = context;
        }


        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlC = null;
            BufferedReader reader = null;
            String jsonString = null;

            try {
                String myUrl = "http://www.yourproofserver.com/API.php/?q=register";
                String nombre = "nombre";
                String apellido = "apellido";
                String eMail = "eMail";
                String password = "password";
                String celular = "celular";
                String fecha = "fecha";

                Uri builtUri = Uri.parse(myUrl).buildUpon().appendQueryParameter(nombre, params[0]).
                        appendQueryParameter(apellido, params[1]).
                        appendQueryParameter(eMail, params[2]).
                        appendQueryParameter(password, params[3]).
                        appendQueryParameter(fecha, params[4]).
                        appendQueryParameter(celular, params[5]).
                        build();

                //return builtUri.toString();

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
                //return jsonString;

            } catch (Exception e) {
                return e.toString();
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
        protected void onPostExecute (String s){
            super.onPostExecute(s);
            Toast toast = Toast.makeText(mContext, s, Toast.LENGTH_LONG);
            toast.show();

            finish();
        }

        @Override
        protected void onCancelled () {
            mAuthTask = null;
            showProgress(false);
        }

    }
}

