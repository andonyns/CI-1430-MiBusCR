package com.example.busdevelop.buses;

<<<<<<< HEAD
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
=======
>>>>>>> 138fb32b243a3b86f0b7927d3f00d8270547f160
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
<<<<<<< HEAD
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

=======
>>>>>>> 138fb32b243a3b86f0b7927d3f00d8270547f160


public class MainActivity extends ActionBarActivity {

    private static final String mFIREBASE_URL = "https://blazing-fire-9075.firebaseio.com/";
    private String mGps;
    private String mLocation;
    private String mLatitud;
    private String mLongitud;
    private final String mPrefs_Name = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainMenuFragment())
                    .commit();
        }

<<<<<<< HEAD
        SharedPreferences settings = getSharedPreferences(mPrefs_Name, 0);
        Intent intent;
        if (settings.getBoolean("my_first_time", true)){

            Log.d("Comments", "First time");
             intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            settings.edit().putBoolean("my_first_time", false).commit();
        }else{

            intent = new Intent(this, RutasActivity.class);
            startActivity(intent);
        }



=======
>>>>>>> 138fb32b243a3b86f0b7927d3f00d8270547f160
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

<<<<<<< HEAD
    public void getLocation(View v){



           Firebase firebaseRef = new Firebase(mFIREBASE_URL);

            firebaseRef.addChildEventListener(new ChildEventListener(){

                @Override
                public void onChildChanged(DataSnapshot snapshot, String previousChildName){

                   mGps = (String) snapshot.child("GpsID").getValue();
                   mLocation = (String) snapshot.child("Location").getValue();
                   String[] parts = mLocation.split(" ");
                   mLatitud = parts[0];
                   mLongitud = parts[1];


                }

                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChildName){}

                @Override
                public void onChildRemoved(DataSnapshot snapshot){}

                @Override
                public void onChildMoved(DataSnapshot snapshot, String previousChildName){}

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }

            });



    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

=======
>>>>>>> 138fb32b243a3b86f0b7927d3f00d8270547f160
}
