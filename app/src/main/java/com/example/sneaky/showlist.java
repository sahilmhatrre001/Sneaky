package com.example.sneaky;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class showlist extends AppCompatActivity {

    private Context context;
    private TextView mTvName, mTvDate,mTvTime, mTvAddress;
    private ListView mListview;
    private ArrayList<String> mArrayList;
    private Toolbar toolbar;
    private StoreData storeData;
    private FloatingActionButton complete;
    private ImageButton imageButton;

    String list_id,user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showlist);
        initialize();
        Intent i = getIntent();
        list_id = i.getStringExtra("Key");
        user_id = i.getStringExtra("userid");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child(list_id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    storeData = new StoreData();
                    storeData.setList_name(dataSnapshot.getValue(StoreData.class).getList_name());
                    storeData.setTime(dataSnapshot.getValue(StoreData.class).getTime());
                    storeData.setDate(dataSnapshot.getValue(StoreData.class).getDate());
                    storeData.setLat(dataSnapshot.getValue(StoreData.class).getLat());
                    storeData.setLng(dataSnapshot.getValue(StoreData.class).getLng());
                    getList();
                }
                catch (Exception e)
                {
                    Log.d("SNEAKY","excited");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Complete Task")
                        .setMessage("Complete this task ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        dataSnapshot.getRef().removeValue();
                                       Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                       startActivity(i);
                                        Toast.makeText(getApplicationContext(),"Task has been completed",Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(R.drawable.ic_logout)
                        .show();
            }
        });
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

    private void getList() {
        DatabaseReference save_list = FirebaseDatabase.getInstance().getReference();
        save_list = save_list.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(list_id).child("todo");
        save_list.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    mArrayList.add(dataSnapshot1.getValue().toString());
                }
                setData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    private void setData(){
        mTvName.setText(storeData.getList_name());
        mTvDate.setText(storeData.getDate());
        mTvTime.setText(storeData.getTime());
        mTvAddress.setText(get_address(storeData.getLat(),storeData.getLng()));
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,mArrayList);;
        mListview.setAdapter(arrayAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                delete_note(position);
            }

        });
       mTvAddress.setOnClickListener(new View.OnClickListener() {
        @Override
           public void onClick(View v) {
               showPopup(v);
          }
       });
    }

    private void showPopup(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.map_popup);
        imageButton = dialog.findViewById(R.id.close);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.popup_map);
                if(fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .commit();
                }
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void delete_note(final int position) {
        final ArrayList<DataSnapshot> find = new ArrayList<>();
        DatabaseReference save_list = FirebaseDatabase.getInstance().getReference();
        save_list = save_list.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(list_id).child("todo");
        save_list.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    find.add(ds);
                }
                DataSnapshot del = find.get(position);
                //del.getRef().setValue("Done");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initialize() {
        context = showlist.this;
        complete = findViewById(R.id.floating_action_button_complete);
        mTvName = findViewById(R.id.tv_name);
        mTvDate = findViewById(R.id.tv_date);
        mTvTime = findViewById(R.id.tv_time);
        mTvAddress = findViewById(R.id.tv_address);
        mListview = findViewById(R.id.listview);
        mArrayList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.appbar,menu);
        return true;
    }
}
