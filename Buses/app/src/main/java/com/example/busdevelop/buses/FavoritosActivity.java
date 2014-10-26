package com.example.busdevelop.buses;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavoritosActivity extends ActionBarActivity {

    private GoogleMap mGoogleMap;
    private List<Ruta> mFavoritosArray;
    private List<String> mNombreRutaArray;
    private ListView mList;
    private List<LatLng> mMarkerParadas;
    private Usuario mUsuario;
    private String mUrlFavorita= "https://murmuring-anchorage-1614.herokuapp.com/rutas/";
    private ArrayAdapter<String> mAdapter;
    private List<String> ids = new ArrayList<String>();
    private final String mPrefs_Name = "MyPrefsFile";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        mMarkerParadas = new ArrayList<LatLng>();
        mFavoritosArray = new ArrayList<Ruta>();


       getRutas();

        try {
            if (mGoogleMap == null) {
                mGoogleMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }

        CameraUpdate centro = CameraUpdateFactory.newLatLng(new LatLng(9.935783, -84.051375));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(14);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.moveCamera(centro);
        mGoogleMap.animateCamera(zoom);

        mGoogleMap.setTrafficEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Mapa", "exception", e);
        }

        showBuses();

       }


    private void getRutas(){
        mUsuario = new Usuario();
        SharedPreferences sharedPref = getSharedPreferences("MyPrefsFile", 0);
        mUsuario.setEmail(sharedPref.getString("UserEmail", ""));
        mUsuario.setEncrypted_password(sharedPref.getString("UserPass", ""));

        new HttpAsyncTaskToken(this).execute();
    }

    /* TODO: Muestra los buses*/
    public void showBuses() {
        /*Firebase firebaseRef = new Firebase(mFIREBASE_URL);

        firebaseRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

                Location location = new Location("dummyprovider");

                mGps = (String) snapshot.child("GpsID").getValue();
                mLocation = (String) snapshot.child("Location").getValue();
                String[] parts = mLocation.split(" ");
                mLatitud = Double.parseDouble(parts[0]);
                mLongitud = Double.parseDouble(parts[1]);
                location.setLatitude(mLatitud);
                location.setLongitude(mLongitud);
                onLocationChanged(location);
                mMarcadorBus = mMarcadorUpdate;

            }

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }

        });*/
    }

    private void createListView(){
        // Get ListView object from xml
        mList = (ListView) findViewById(R.id.favoritoslist);

        mNombreRutaArray = new ArrayList<String>();
        for ( Ruta r : mFavoritosArray){
            mNombreRutaArray.add(r.getNombre());
            Log.d("Prueba",r.getNombre());
        }
        if(!mNombreRutaArray.isEmpty()){
            mList.setVisibility(View.VISIBLE);
            Log.e("mensaje","lista vacia");
        }

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,mNombreRutaArray);


        // Assign adapter to ListView
        mList.setAdapter(adapter);

        // ListView Item Click Listener
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                final int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) mList.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();


                        Ruta seleccionada = mFavoritosArray.get(itemPosition);
                        String nombre = mNombreRutaArray.get(itemPosition);

                            mGoogleMap = ((MapFragment) getFragmentManager().
                                    findFragmentById(R.id.map)).getMap();


                        new DibujarRuta(mGoogleMap, seleccionada);

            }
        });
    }


    @Override
    protected void onResume(){

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favoritos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }







    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        Activity mActivity;
        private HttpAsyncTask(Activity activity){
            this.mActivity = activity;
        }

        @Override
        protected String doInBackground(String... urls) {
            try{
                // una vez recibido el string con  el json
                //  se parsea guardando en un array
                JSONArray rutas = new JSONArray(GetFavoritos(urls[0]));
                String nombre;



                //  cada i corresponderia a una diferente ruta
                // se obtiene el objetoJson de esa posicion
                // y se le sacan los atributos que todos serian
                //  Strings. Se guarda una ruta en el arreglo de rutas

                for (int i = 0; i < rutas.length(); i++) {
                    Ruta favoritos = new Ruta();
                    ids.add(Integer.toString(rutas.getJSONObject(i).getInt("ruta_id")));


                   // favoritos.setNombre(rutas.getJSONObject(i).getString("nombre"));
                   // nombre = rutas.getJSONObject(i).getString("nombre");
                   // favoritos.setFrecuencia(rutas.getJSONObject(i).getString("frecuencia"));
                   // favoritos.setPrecio(rutas.getJSONObject(i).getString("precio"));
                   // favoritos.setHorario(rutas.getJSONObject(i).getString("horario"));
                   // favoritos.setParadas(mUsuario.getToken());
                   // mFavoritosArray.add(favoritos);
                   // Toast.makeText(getBaseContext(), nombre, Toast.LENGTH_LONG).show();
                }

                //llamar al otro asyntask





                // mResultRutas.setText(mRutasArray.get(1).getFrecuencia());

                // Pasar las rutas al  ListViewAdapter
                //mAdapter = new ListViewAdapter(mActivity, mRutasArray);

                // enlazar el adaptador con el listView
                //mList.setAdapter(mAdapter);


            }catch(JSONException e){
                e.printStackTrace();
            }
            return "Rutas Obtenidas!";
        }

        /**
         * metodo que se ejecuta después de obtener la respuesta
         * al request get
         * @param resultado
         */
        @Override
        protected void onPostExecute(String resultado) {


            // una vez obtenido el token se pide las rutas
            for(String id : ids) {
                new HttpAsyncTaskRutas(mActivity).execute("https://murmuring-anchorage-1614.herokuapp.com/rutas/"+id);
            }
        }


    }

    /**
     * Metodo que hace un request al API con la url donde
     * se pregunta por la tabla de favoritos
     * @param url url que almacena las rutas favoritas
     * @return String con  el array Json
     */
    public  String GetFavoritos(String url){
        String resultado = ApiManager.httpGet(url,mUsuario.getToken());

        return resultado;
    }

    public  String GET(String url){
        String resultado = ApiManager.httpGet(url,mUsuario.getToken());

        return resultado;
    }

    private class HttpAsyncTaskRutas extends AsyncTask<String, Void, String> {
        Activity mActivity;
        private HttpAsyncTaskRutas(Activity activity){
            this.mActivity = activity;
        }

        @Override
        protected String doInBackground(String... urls) {
            try{
                // una vez recibido el string con  el json
                //  se parsea guardando en un array
                JSONObject rutas = new JSONObject(GET(urls[0]));




                //  cada i corresponderia a una diferente ruta
                // se obtiene el objetoJson de esa posicion
                // y se le sacan los atributos que todos serian
                //  Strings. Se guarda una ruta en el arreglo de rutas


                    Ruta favoritos = new Ruta();
                    favoritos.setId(Integer.toString(rutas.getInt("id")));
                    favoritos.setNombre(rutas.getString("nombre"));
                    favoritos.setFrecuencia(rutas.getString("frecuencia"));
                    favoritos.setPrecio(rutas.getString("precio"));
                    favoritos.setHorario(rutas.getString("horario"));
                    //favoritos.setParadas(mUsuario.getToken());
                    mFavoritosArray.add(favoritos);








                // mResultRutas.setText(mRutasArray.get(1).getFrecuencia());

                // Pasar las rutas al  ListViewAdapter
                //mAdapter = new ListViewAdapter(mActivity, mRutasArray);

                // enlazar el adaptador con el listView
                //mList.setAdapter(mAdapter);


            }catch(JSONException e){
                e.printStackTrace();
            }
            return "Rutas Obtenidas!";
        }

        /**
         * metodo que se ejecuta después de obtener la respuesta
         * al request get
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
            createListView();
        }


    }

    /**
     * Obtener el token para consultar rutas favoritas
     */

    private class HttpAsyncTaskToken extends AsyncTask<Void, Void, String> {
        Activity mActivity;
        private HttpAsyncTaskToken(Activity activity){
            this.mActivity = activity;
        }


        @Override
        protected String doInBackground(Void...params) {

            return mUsuario.obtenerToken(mUsuario.getEmail(), mUsuario.getEncrypted_password());
        }

        /**
         * metodo que se ejecuta después de obtener la respuesta
         * al request post del token
         * @param resultado
         */
        @Override
        protected void onPostExecute(String resultado) {
            mUsuario.guardarTokenId(resultado);

            // una vez obtenido el token se pide las rutas
            new HttpAsyncTask(mActivity).execute("https://murmuring-anchorage-1614.herokuapp.com/favoritas");

        }
    }
}
