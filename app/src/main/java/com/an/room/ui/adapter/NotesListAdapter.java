package com.an.room.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.an.room.R;
import com.an.room.model.Note;
import com.an.room.repository.NoteRepository;
import com.an.room.util.NoteDiffUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.CustomViewHolder> {
    ArrayList<Integer> checkedDone = new ArrayList<>();
    List<Boolean> checkboxState;
    private List<Note> notes;
    private Activity mContext;
    private DataAdapterListener mListener;
    private Boolean isSelectAll = false;
    private NoteRepository noteRepository;
    public NotesListAdapter(Activity context, List<Note> notes
            , DataAdapterListener listener
    ) {


        this.notes = notes;
        this.checkboxState =  new ArrayList<Boolean>(Collections.nCopies(notes.size(), true));
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        noteRepository = new NoteRepository(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    public void setChecked(boolean state, int position)
    {
        checkboxState.set(position, state);
    }
    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        final Note note = getItem(position);

        holder.itemTitle.setText(note.getTitle());
        holder.itemTime.setText(note.getDateInput());

        if (noteRepository.getNumbersDone() == 1){
            holder.cbList.setChecked(checkboxState.get(position));
        }else {
            holder.cbList.setChecked(false);
        }
        if (!isSelectAll){
            holder.cbList.setChecked(false);
        } else {
            holder.cbList.setChecked(true);
        }
        holder.cbList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked){
                    noteRepository.updateTask(note,true);
                }
                else {
                    noteRepository.updateTask(note,false);
                }
                mListener.onDataSelected(note, isChecked);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public Note getItem(int position) {
        return notes.get(position);
    }

    public void selectAll(){
        isSelectAll = true;
        notifyDataSetChanged();
    }

    public void unSelectAll(){
        isSelectAll = false;
        notifyDataSetChanged();
    }

    public void addTasks(List<Note> newNotes) {
        NoteDiffUtil noteDiffUtil = new NoteDiffUtil(notes, newNotes);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(noteDiffUtil);
        notes.clear();
        notes.addAll(newNotes);

        diffResult.dispatchUpdatesTo(this);
    }

    protected class CustomViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cbList;
        private LinearLayout ln;
        private TextView itemTitle, itemTime;
        public CustomViewHolder(View itemView) {
            super(itemView);
            ln = itemView.findViewById(R.id.item_clickTask);
            cbList = itemView.findViewById(R.id.item_cbList);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemTime = itemView.findViewById(R.id.item_desc);
        }
    }

    public interface DataAdapterListener {
        void onDataSelected(Note note, Boolean isChecked);
    }
}
