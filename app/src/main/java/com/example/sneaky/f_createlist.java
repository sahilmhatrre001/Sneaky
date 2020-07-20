package com.example.sneaky;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;


public class f_createlist extends Fragment {

    View view;
    TextView list_name;
    String list_name_f;
    Button add_btn;
    EditText itemET;
    ListView itemList;
    StoreData sv;
    ImageView cancel;
    private DatabaseReference reff;
    private ImageView save_i;
    private ExampleServie exampleServie;
    private ServiceConnection serviceConnection;

    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    public f_createlist(String x,StoreData j) {
        list_name_f = x;
        sv = j;

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_f_createlist, container, false);
        list_name = (TextView) view.findViewById(R.id.list_name_txt);
        reff = FirebaseDatabase.getInstance().getReference();
        list_name.setText(list_name_f);
        save_i = view.findViewById(R.id.floating_action_button_save);
        itemET = view.findViewById(R.id.item_edit_text);
        add_btn = view.findViewById(R.id.add_button);
        itemList = view.findViewById(R.id.item_list);
        cancel = view.findViewById(R.id.cancel_action);

        items = FileHelper.readData(view.getContext());
        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, items);
        itemList.setAdapter(adapter);
        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                items.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No",null)
                        .setIcon(R.drawable.ic_logout)
                        .show();
                return true;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(v.getContext(),MainActivity.class);
                startActivity(home);
            }
        });

        save_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(items.isEmpty() == false)
                {
                    String key = reff.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                    reff.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(sv);

                    DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
                    databaseAdapter.insert(key,sv.getLat(),sv.getLng());
                    Log.d("SNEAKY","done");

                    Intent serviceIntent = new Intent(getActivity(),ExampleServie.class);
                    getActivity().stopService(serviceIntent);

                    Intent serviceIntent1 = new Intent(getActivity(),ExampleServie.class);
                    getActivity().startService(serviceIntent1);

                    DatabaseReference save_list = FirebaseDatabase.getInstance().getReference();
                    //store_sql.set_data(sv,key);
                    save_list = save_list.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("todo");
                    for(int i = 0;i<items.size();i++)
                    {
                        save_list.push().setValue(items.get(i));
                    }
                    items.clear();
                    FileHelper.writeData(items,view.getContext());
                    Intent home = new Intent(view.getContext(),MainActivity.class);
                    startActivity(home);
                }
                else {
                    Toast.makeText(view.getContext(),"List is empty",Toast.LENGTH_LONG).show();
                }
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!itemET.getText().toString().isEmpty())
                {
                    String itemEntered = itemET.getText().toString();
                    adapter.add(itemEntered);
                    itemET.setText("");
                    FileHelper.writeData(items,view.getContext());
                    Toast.makeText(view.getContext(),"Item Added in List",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(view.getContext(),"Text is empty",Toast.LENGTH_LONG).show();
                }

            }
        });


        return view;
    }

}
