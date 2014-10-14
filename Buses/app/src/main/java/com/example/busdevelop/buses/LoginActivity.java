package com.example.busdevelop.buses;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUp();
    }

    private void setUp(){
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
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

        Button mLoginButton = (Button) findViewById(R.id.Login);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.Register);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (VERSION.SDK_INT >= 14) {
            // Use ContactsContract.Profile (API 14+)
            getLoaderManager().initLoader(0, null, this);
        } else if (VERSION.SDK_INT >= 8) {
            // Use AccountManager (API 8+)
            new SetupEmailAutoCompleteTask().execute(null, null);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = getCurrentEmail();
        String password = getCurrentPassword();

        boolean cancel = false;
        View focusView = null;

        //Check for a valid password, if the user entered one.
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
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void makeInvisible(){
        View appearLogin = findViewById(R.id.Register);
        appearLogin.setVisibility(View.INVISIBLE);
        appearLogin = findViewById(R.id.Login);
        appearLogin.setVisibility(View.INVISIBLE);
        appearLogin = findViewById(R.id.email);
        appearLogin.setVisibility(View.INVISIBLE);
        appearLogin = findViewById(R.id.password);
        appearLogin.setVisibility(View.INVISIBLE);
    }

    private void makeVisible(){
        View appearLogin = findViewById(R.id.Register);
        appearLogin.setVisibility(View.VISIBLE);
        appearLogin = findViewById(R.id.Login);
        appearLogin.setVisibility(View.VISIBLE);
        appearLogin = findViewById(R.id.email);
        appearLogin.setVisibility(View.VISIBLE);
        appearLogin = findViewById(R.id.password);
        appearLogin.setVisibility(View.VISIBLE);

        RelativeLayout relative = (RelativeLayout) findViewById(R.id.login_view);
        relative.setBackgroundResource(R.drawable.bus_logo);
    }

    /*Returns the current value at the email field*/
    public String getCurrentEmail(){
        try {
            return mEmailView.getText().toString();
        }catch(NullPointerException e){
            return null;
        }
    }

    /*Returns the current value at the password field*/
    public String getCurrentPassword(){
        try{
            return mPasswordView.getText().toString();
        }catch(NullPointerException e){
            return null;
        }
    }

    public boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public boolean isPasswordValid(String password) {
        return password.length() > 2;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /** Metodo para pasar a a la actividad CrearCuenta*/
    public void goToRegister(){
        Intent i = new Intent(this, CrearCuentaActivity.class);
        if(getCurrentEmail()!=null){
            i.putExtra("emailIngresado", getCurrentEmail());
        }
        startActivity(i);
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
        List<String> emails = new ArrayList<String>();
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

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Use an AsyncTask to fetch the user's email addresses on a background thread, and update
     * the email text field with results on the main UI thread.
     */
    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<String>();

            // Get all emails from the user's contacts and copy them to a list.
            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                        .CommonDataKinds.Email.DATA));
                emailAddressCollection.add(email);
            }
            emailCur.close();

            return emailAddressCollection;
        }

	    @Override
	    protected void onPostExecute(List<String> emailAddressCollection) {
	       addEmailsToAutoComplete(emailAddressCollection);
	    }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    public boolean validarDatos(String value,String email, String password){
        UserLoginTask u = new UserLoginTask(email,password);
        return u.validateData(value);
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String LOG_TAG = LoginActivity.class.getSimpleName();
        private final String mEmail;
        private final String mPassword;
        private boolean mValid = false;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            InputStream inputStream = null;
            String resultado = "";
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("https://murmuring-anchorage-1614.herokuapp.com/tokens");

                String json = "";

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("email", mEmail); //"Api@MiBusCR.co.cr"
                jsonObject.accumulate("password", mPassword); //"?$jMEyp5P_9=E7L"
                json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                HttpResponse httpResponse = httpclient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null){
                    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                    String linea = "";
                    while( (linea = bufferedReader.readLine()) != null){
                        resultado += linea;
                    }
                    inputStream.close();
                }else{
                    resultado = "Error al guardar datos";
                }
            }catch (Exception e){
                Log.d("InputStream", "Error: "+e.getLocalizedMessage());
            }


            if(!resultado.equals("Error al guardar datos")){
                saveUser();
                mValid = true;
            }else{
                mValid = false;
            }

            return mValid;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        //Valida si los datos están dentro del json de todos los usuarios
        private boolean validateData(String jsonString){
            boolean found = false;
            try{
                // una vez recibido el string con  el json
                //  se parsea guardando en un array
                JSONArray valores = new JSONArray(jsonString);
                String e;
                String p;
                //Se verifica si en el elemento i existen el email
                //y la contraseña
                for(int i = 0; i < jsonString.length(); i++){
                    e = valores.getJSONObject(i).getString("email");
                    p = valores.getJSONObject(i).getString("password");
                    if(e.equals(mEmail)&&p.equals(mPassword)){
                        found = true;
                        i = jsonString.length();
                    }
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return found;
        }

        public boolean validUser(){
            return mValid;
        }

        //Guarda dentro del SharedPreferences el email del usuario para saber que ya ingreso correctamente
        private void saveUser(){
            SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("UserEmail",mEmail);
            editor.putBoolean("SinRegistrar",false);
            editor.putString("UserPass",mPassword);
            editor.commit();
        }
    }
}



