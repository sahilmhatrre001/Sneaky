package com.example.sneaky;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    Location curerentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final  int REQUEST_CODE = 101;
    private Marker myMarker,searchMarker;
    private GoogleMap googleMapMain;
    //search
    private EditText mSearchText;
    //gps
    private ImageView mGps;
    //loading
    private Intent startIntent;



    private boolean location_enable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startIntent = getIntent();
        startService();
        BottomNavigationView navigationView = findViewById(R.id.menu);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id =  menuItem.getItemId();

                if(id == R.id.home)
                {

                    HomeFragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_main,fragment);
                    fragmentTransaction.commit();
                }
                if(id == R.id.my_lists)
                {

                    mylistFragment fragment = new mylistFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_main,fragment);
                    fragmentTransaction.commit();
                }
                if(id == R.id.settings)
                {

                    SettingsFragment fragment = new SettingsFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_main,fragment);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });

        navigationView.setSelectedItemId(R.id.home);
        location_enable = false;
        locationEnabled();
        mSearchText = (EditText) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(location_enable == true)
        {
            fetchLastLocation();
            location_enable = false;
        }
    }

    public void startService()
    {
        Intent serviceIntent = new Intent(this,ExampleServie.class);
        serviceIntent.putExtra("sendFore","To Notify you about nearby task");
        startService(serviceIntent);

    }

    public void stopService(View v)
    {
        Intent serviceIntent = new Intent(this,ExampleServie.class);
        startService(serviceIntent);
    }

    private void init()
    {
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    Locate_me();
                }
                return false;
            }
        });
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
        Hide_keyboard();

    }

    private void locationEnabled () {
        final LocationManager lm = (LocationManager)
                getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(MainActivity. this )
                    .setMessage( "Turn on Location Services" )
                    .setPositiveButton( "Settings" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                   startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS ));
                                   location_enable = true;
                                }
                            })
                    .setNegativeButton( "Cancel" , null )
                    .show();
        }
    }


    private void Locate_me()
    {
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(getApplicationContext());

        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);

        }
        catch (IOException e)
        {
            Log.e("Error","Search err");
        }

        if(list.size() > 0)
        {

            Address address = list.get(0);
            Log.d("Location Search Bar :",address.toString());
            moveCamera_search( new LatLng(address.getLatitude(),address.getLongitude()),address.getAddressLine(0 ));
            return;
        }
        Toast.makeText(getApplicationContext(),"Address not found try again",Toast.LENGTH_LONG).show();
    }
    private void moveCamera(LatLng latLng,String title)
    {
        googleMapMain.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        myMarker = googleMapMain.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title(title)
        );
        Hide_keyboard();
    }

    private void moveCamera_search(LatLng latLng,String title)
    {
        googleMapMain.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        searchMarker = googleMapMain.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        );
        Hide_keyboard();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(myMarker))
        {
            //show list code here
//            Intent i = new Intent(getApplicationContext(),showlist.class);
//            i.putExtra("latitude",myMarker.getPosition().latitude);
//            i.putExtra("longitude",myMarker.getPosition().longitude);
//            startActivity(i);
        }
        else if (marker.equals(searchMarker))
        {
            popup("Create New List " ,marker.getPosition());

        }
        return false;
    }

    private void fetchLastLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE
            );
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                {
                    curerentLocation = location;
                    Toast.makeText(getApplicationContext(),"Locating",Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(MainActivity.this);
                }
            }
        });

    }

    private void init_googlemapMain(GoogleMap m)
    {
        googleMapMain = m;

    }

    private void popup(String str, final LatLng latLng){
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        final boolean[] ans = {false};
        ad.setMessage("Address :" + get_address(latLng.latitude,latLng.longitude));
        ad.setTitle(str);
        ad.setIcon(R.drawable.ic_addlist);
        ad.setPositiveButton("Create",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent create_list_i = new Intent(getApplicationContext(),createlist.class);
                        create_list_i.putExtra("latitude",latLng.latitude);
                        create_list_i.putExtra("longitude",latLng.longitude);
                        startActivity(create_list_i);
                        moveCamera(latLng,get_address(latLng.latitude, latLng.longitude));
                    }
                });
        ad.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alertDialog = ad.create();
        alertDialog.show();
    }

    private void getDeviceLocation()
    {
        LatLng latLng = new LatLng(curerentLocation.getLatitude(),curerentLocation.getLongitude());
        googleMapMain.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMapMain.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        init_googlemapMain(googleMap);
        getDeviceLocation();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        init();

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Hide_keyboard();
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                popup("Create New List " ,latLng);
            }
        });

        googleMap.setOnMarkerClickListener(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_CODE :
                if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    fetchLastLocation();
                }
                break;
        }
    }



    public String get_address(double LATITUDE, double LONGITUDE){
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Log.d("Here","address found");
                strAdd +=  " " + addresses.get(0).getAddressLine(0);
                strAdd += " " +addresses.get(0).getLocality();
                strAdd += " " + addresses.get(0).getPostalCode();
                return strAdd;
            }
            else {
                Log.d("Here","address not found");
                strAdd = "not ava";
                return strAdd;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Address Not Fond";

    }

    public void Hide_keyboard()
    {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


}
