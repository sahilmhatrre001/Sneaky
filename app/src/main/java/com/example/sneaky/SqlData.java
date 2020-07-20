package com.example.sneaky;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqlData extends SQLiteOpenHelper {
    private static final String dbname = "notify_data.db";
    private static final int version = 1;
    private static Double lat,lng;
    private static String note_id;

    public SqlData(Context context)
    {
        super(context,dbname,null,version);
        Log.d("SNEAKY","constructor");

    }

    public void set_data(StoreData op,String note_id_send)
    {
        lat = op.getLat();
        lng = op.getLng();
        note_id = note_id_send;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            Log.d("SNEAKY","constructor1");
            String sql = "CREATE TABLE NOTIFY (_id INTEGER PRIMARY KEY AUTOINCREMENT,LAT DOUBLE,LNG DOUBLE,NOTE_ID STRING)";
            db.execSQL(sql);
            insertData(db);
    }

    private void insertData(SQLiteDatabase ds)
    {
        ContentValues values = new ContentValues();
        values.put("LAT",lat);
        values.put("LNG",lng);
        values.put("NOTE_ID",note_id);
        ds.insert("NOTIFY",null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
