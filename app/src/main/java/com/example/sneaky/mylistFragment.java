package com.example.sneaky;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class mylistFragment extends Fragment implements
        NotesRecyclerAdapter.OnNotesListener{
    private RecyclerView gRecyclerView;
    //Var section
    private ArrayList<Notes> gNotes = new ArrayList<>();
    private NotesRecyclerAdapter gNotesRecyclerAdapter;
    private DatabaseReference databaseReference;
    private ArrayList<String> Note_ids = new ArrayList<>();
    private String userid;
    private View view;
    private String key;
    public mylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_mylist, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        gRecyclerView = (RecyclerView) view.findViewById(R.id.rview);
        gRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        gRecyclerView.addItemDecoration(itemDecorator);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(gRecyclerView);
        gNotesRecyclerAdapter = new NotesRecyclerAdapter(gNotes, this);
        gRecyclerView.setAdapter(gNotesRecyclerAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                insertNotes(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    private void insertNotes(DataSnapshot dataSnapshot){

        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            key = ds.getKey();
            Note_ids.add(key);
            DatabaseReference todo_it = databaseReference.child(key);

            todo_it.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    StoreData data = new StoreData();
                    if(data != null) {
                        try {
                            Notes note = new Notes();
                            data.setList_name(dataSnapshot.getValue(StoreData.class).getList_name());
                            data.setDate(dataSnapshot.getValue(StoreData.class).getDate());
                            data.setTime(dataSnapshot.getValue(StoreData.class).getTime());
                            data.setLat(dataSnapshot.getValue(StoreData.class).getLat());
                            data.setLng(dataSnapshot.getValue(StoreData.class).getLng());
                            note.setTitle(data.getList_name());
                            note.setTime(data.getTime());
                            note.setDate(data.getDate());
                            note.setLat(data.getLat());
                            note.setLng(data.getLng());
                            gNotes.add(note);
                            gNotesRecyclerAdapter.notifyDataSetChanged();
                        }
                        catch (NullPointerException e)
                        {
                            Log.d("SNEAKY","excited");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }




    private void deleteNote(Notes notes){
        gNotes.remove(notes);
        gNotesRecyclerAdapter.notifyDataSetChanged();
    }

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            new AlertDialog.Builder(view.getContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteNote(gNotes.get(viewHolder.getAdapterPosition()));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent own = new Intent(view.getContext(),MainActivity.class);
                            startActivity(own);
                        }
                    })
                    .setIcon(R.drawable.ic_logout)
                    .show();
        }
    };


    @Override
    public void onNoteClick(int position) {
        String pos = Integer.toString(position);
        Intent intent = new Intent(view.getContext(), showlist.class);
        intent.putExtra("Key",Note_ids.get(position));
        intent.putExtra("userid",userid);
        startActivity(intent);
    }
}
