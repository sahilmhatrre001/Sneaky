package com.example.sneaky;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.sneaky.Foreground.CHANNEL_ID;

public class ExampleServie extends Service {
    private final String EDITOR_CONS = "task list";
    private SqlData sqlData;
    private SQLiteDatabase database;
    private String note;
    private ArrayList<HashMap> list;
    private DatabaseAdapter databaseAdapter;
    private LocationManager locationManager;
    private final static AtomicInteger c = new AtomicInteger(2);

    @Override
    public void onCreate() {
        super.onCreate();
        sqlData = new SqlData(this);
        database = sqlData.getReadableDatabase();
        databaseAdapter = new DatabaseAdapter(getBaseContext());
        list = databaseAdapter.select();

        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Log.d("SNEAKY", "location check");
            locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000,
                    10, locationListener);
            return;
        }

    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            pushNotification(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void pushNotification(Location location) {

        for (int i = 0;i<list.size();i++)
        {
            HashMap<String,ArrayList<Double>> data = list.get(i);

            String note_id = null;
            ArrayList<Double> latlng = null;
            for (Map.Entry<String,ArrayList<Double>> ee : data.entrySet())
            {
                note_id = ee.getKey();
                latlng = ee.getValue();
            }
            double lt = latlng.get(0);
            double lg = latlng.get(1);

            double distance_bet = distance(lt,lg,location.getLatitude(),location.getLongitude());
            if(distance_bet <= 1.0)
            {
                Log.d("SNEAKY","inside" + note_id);
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child(note_id);
                final String finalNote_id = note_id;
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        StoreData databse = new StoreData();
                        try {
                            databse.setList_name(dataSnapshot.getValue(StoreData.class).getList_name());
                            display_notification(databse.getList_name(), finalNote_id);
                            Log.d("SNEAKY", "notify " + databse.getList_name());
                        }
                        catch (NullPointerException e)
                        {
                            Log.d("SNEAKY"," null ptt");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            else {
                Log.d("SNEAKY","outside" + note_id);
            }
        }

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public void display_notification(String x,String note_id)
    {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_placeholder) //set icon for notification
                        .setContentTitle("Task Nearby") //set title of notification
                        .setContentText("Task Name : " + x)//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        Intent notificationIntent = new Intent(this, showlist.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("Key", note_id);
        notificationIntent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        int notif_id =  getID();

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        manager.notify(notif_id, builder.build());
            Log.d("SNEAKY","pop");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("sendFore");
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this,
                0,notificationIntent,0
                );
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Sneaky")
                .setContentText(note)
                .setSmallIcon(R.drawable.ic_placeholder)
                .setContentIntent(pendingIntent)
                .build();


        startForeground(1,notification);

        return START_NOT_STICKY;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    IBinder mBinder = (IBinder) new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ExampleServie getServerInstance() {
            return ExampleServie.this;
        }
    }


    public int getID() {
        return c.incrementAndGet();
    }

}
