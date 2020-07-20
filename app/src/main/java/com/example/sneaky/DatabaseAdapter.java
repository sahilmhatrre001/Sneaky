package com.example.sneaky;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseAdapter{
    Database d;
    Context ctx;
    SQLiteDatabase db;
    //String col[]={Database.COL_2};

    DatabaseAdapter(Context ctx)
    {
        this.ctx=ctx;
        d=new Database(ctx);
        db=d.getWritableDatabase();
    }

    public void insert(String id,Double lat, Double lng)
    {
        ContentValues c=new ContentValues();
        c.put(Database.COL_1,id);
        c.put(Database.COL_3,lat);
        c.put(Database.COL_4,lng);

        try {
            db.insert(Database.TABLE_NAME, null, c);
            Log.d("SNEAKY","inserted");
        }
        catch(Exception e)
        {
            Log.d("SNEAKY","catch insert");
        }
    }

    public ArrayList<HashMap> select()
    {
        ArrayList<HashMap> arrayList;
        HashMap<String,ArrayList<Double>> data;
        arrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT note_id,lat,lng FROM Sample",new String[]{});

        if (cursor != null && cursor.moveToFirst())
        {
            do {
                String note_id = cursor.getString(0);
                double lat = cursor.getDouble(1);
                double lng = cursor.getDouble(2);
                Log.d("SNEAKY","id: "+note_id);
                Log.d("SNEAKY","lat: "+lat);
                Log.d("SNEAKY","lng: "+lng);

                data = new HashMap<>();
                ArrayList<Double> location = new ArrayList<>();
                location.add(lat);
                location.add(lng);
                data.put(note_id,location);
                arrayList.add(data);
            }while (cursor.moveToNext());
        }
        else {
            Log.d("sql","nodata");
        }
        return arrayList;
    }


    public class Database extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME="notify";
        private static final int DATABASE_VERSION=2;
        private static final String TABLE_NAME="Sample";
        private static final String COL_1="note_id";
        private static final String COL_3="lat";
        private static final String COL_4="lng";
        private static final String CREATE_QUERY="create table "+TABLE_NAME+"("+COL_1+" VARCHAR(20) PRIMARY KEY,"+COL_3+" REAL ," +COL_4+" REAL)";
        private static final String DROP_TABLE="DROP TABLE IF EXISTS "+TABLE_NAME+";";

        public Database(Context context) {
            super(context,DATABASE_NAME,null, DATABASE_VERSION);
            Log.d("SNEAKY","Constructor is called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("SNEAKY","oncreate is called");
            try {
                db.execSQL(CREATE_QUERY);
                Log.d("SNEAKY","table is created");
            }
            catch (Exception e)
            {
                Log.d("SNEAKY",e.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("SNEAKY","onupgrade is called");
            try {
                db.execSQL(DROP_TABLE);
            }
            catch(Exception e)
            {
                Log.d("SNEAKY",e.getMessage());
            }
            onCreate(db);
        }
    }

}
