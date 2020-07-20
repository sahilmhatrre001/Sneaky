package com.example.sneaky;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class createlist extends AppCompatActivity {
    Button b_date,b_time;
    EditText e_list_name;
    TextView e_time,e_date;
    Double lat,lng;
    FloatingActionButton next;
    ImageView cancel;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlist);
        Intent i = getIntent();
        b_date = (Button) findViewById(R.id.in_date);
        b_time = (Button) findViewById(R.id.in_time);
        e_list_name = (EditText) findViewById(R.id.edit_list_name);
        e_time = (TextView) findViewById(R.id.txt_time);
        e_date = (TextView) findViewById(R.id.txt_date);
        cancel = (ImageView) findViewById(R.id.cancel_action);
        lat = i.getDoubleExtra("latitude",0);
        lng = i.getDoubleExtra("longitude",0);
        final StoreData data = new StoreData();
        next = findViewById(R.id.floating_action_button_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(e_list_name.getText().toString().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"List title is empty",Toast.LENGTH_LONG).show();
                    }
                    else if(e_date.getText().toString().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"Date is empty",Toast.LENGTH_LONG).show();
                    }
                    else if(e_time.getText().toString().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"Time is empty",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        data.setLng(lng);
                        data.setLat(lat);
                        data.setDate(e_date.getText().toString());
                        data.setTime(e_time.getText().toString());
                        data.setList_name(e_list_name.getText().toString());
                        f_createlist fragment = new f_createlist(e_list_name.getText().toString(),data);
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.createlist_main, fragment);
                        fragmentTransaction.commit();
                    }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(home);
            }
        });



        b_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hide_keyboard();
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(createlist.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                if(year < mYear){
                                    Toast.makeText(getApplicationContext(),"Choose Valid Year",Toast.LENGTH_LONG).show();
                                }
                                else if(year == mYear && monthOfYear < mMonth)
                                {
                                    Toast.makeText(getApplicationContext(),"Choose Valid Month",Toast.LENGTH_LONG).show();
                                }
                                else if(year == mYear && monthOfYear == mMonth && dayOfMonth < mDay)
                                {
                                    Toast.makeText(getApplicationContext(),"Choose Valid Day",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    e_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.show();
            }
        });
        b_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hide_keyboard();
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(createlist.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                               if(!e_date.getText().toString().isEmpty())
                               {
                                       e_time.setText(hourOfDay + ":" + minute);
                               }
                               else{
                                   Toast.makeText(getApplicationContext(),"Choose Date First",Toast.LENGTH_LONG).show();
                               }

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

    }
    public void Hide_keyboard(){
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
