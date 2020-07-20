package com.example.sneaky;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>{

    ArrayList<Notes> gNotes;
    private OnNotesListener gOnNotesListener;

    public NotesRecyclerAdapter(ArrayList<Notes> lNotes, OnNotesListener OnNotesListener) {
        this.gNotes = lNotes;
        this.gOnNotesListener = OnNotesListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notes_list_item, parent, false);
        return new ViewHolder(view, gOnNotesListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.timestamp.setText(gNotes.get(position).getDate());
        holder.title.setText(gNotes.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return gNotes.size() ;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView text;
        TextView title, timestamp;
        OnNotesListener onNotesListener;

        public ViewHolder(@NonNull View itemView, OnNotesListener onNotesListener) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title);
            timestamp = itemView.findViewById(R.id.note_timestamp);
            this.onNotesListener = onNotesListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNotesListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNotesListener{
        void onNoteClick(int position);
    }
}
