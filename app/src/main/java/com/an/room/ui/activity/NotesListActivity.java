package com.an.room.ui.activity;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.an.room.R;
import com.an.room.model.Note;
import com.an.room.repository.NoteRepository;
import com.an.room.ui.adapter.NotesListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotesListActivity extends AppCompatActivity implements NotesListAdapter.DataAdapterListener
{

    ArrayList<Integer> checkedDone = new ArrayList<>();
    private TextView emptyView, txtDate, txtLeftItem;
    private EditText edtTitleTask;
    private Button btnClearItem;
    private RecyclerView recyclerView;
    private NotesListAdapter notesListAdapter;
    private CheckBox cbMarkAll;

    private NoteRepository noteRepository;
    private DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        noteRepository = new NoteRepository(getApplicationContext());

        recyclerView = findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3 , StaggeredGridLayoutManager.VERTICAL));

        emptyView = findViewById(R.id.empty_view);
        edtTitleTask = findViewById(R.id.edtTitleTask);
        cbMarkAll = findViewById(R.id.cbMarkAll);
        txtLeftItem = findViewById(R.id.txtLeftItem);
        btnClearItem = findViewById(R.id.btnClearItem);

        cbMarkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) notesListAdapter.selectAll();
                else notesListAdapter.unSelectAll();
            }
        });

        txtDate = findViewById(R.id.txtDate);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerShow();
            }
        });



        updateTaskList();
    }

    private void datePickerShow() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 2);

        String dtStart = "2011-07-16";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        txtDate.setText((monthOfYear + 1) + "/"
                                +  dayOfMonth + "/" + year);

                        String titleTask = edtTitleTask.getText().toString();
                        String dateInput = (monthOfYear+1)+"/"+dayOfMonth+"/"+year;

                        String input = edtTitleTask.getText().toString();
                        if (input.isEmpty()){
                            Toast.makeText(NotesListActivity.this,"Task do not Empty", Toast.LENGTH_SHORT).show();
                        } else if (!noteRepository.getTaskByTitle(input)){
                            txtLeftItem.setText((noteRepository.getNumbersDone())+" item left");
                            noteRepository.insertTask(titleTask,dateInput,false);
                        } else {
                            Toast.makeText(NotesListActivity.this,"Task do not Same", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, mYear, mMonth, mDay);

       // https://stackoverflow.com/questions/8573250/android-how-can-i-convert-string-to-date
        try {
            Date date = format.parse(dtStart);
            Date dateNow = new Date();
            datePickerDialog.getDatePicker().setMinDate(date.getTime());
            datePickerDialog.getDatePicker().setMaxDate(dateNow.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        datePickerDialog.show();
    }

    private void updateTaskList() {
        txtLeftItem.setText((noteRepository.getNumbersDone())+" item left");
        noteRepository.getTasks().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                if(notes.size() > 0) {

                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    txtLeftItem.setVisibility(View.VISIBLE);
                    btnClearItem.setVisibility(View.VISIBLE);
                    if (notesListAdapter == null) {
                        notesListAdapter = new NotesListAdapter(NotesListActivity.this,notes
                                ,NotesListActivity.this
                        );
                        recyclerView.setAdapter(notesListAdapter);

                    } else {
                        notesListAdapter.addTasks(notes);
                    }
//                    txtLeftItem.setText((noteRepository.getNumbersDone())+" item left");
                } else updateEmptyView();
                txtLeftItem.setText((noteRepository.getNumbersDone())+" item left");
            }
        });
    }

    private void updateEmptyView() {
        checkedDone.clear();
        txtLeftItem.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        btnClearItem.setVisibility(View.GONE);
    }

    @Override
    public void onDataSelected(final Note notes,Boolean isChecked) {
        Log.e("dedi", "onDataSelected: noteRepository.getNumbersDone() "+ noteRepository.getNumbersDone());
        txtLeftItem.setText((noteRepository.getNumbersDone())+" item left");
            if (isChecked){
                checkedDone.add(notes.getId());
            }
            else{
                if (checkedDone != null){
                checkedDone.remove(checkedDone.indexOf(notes.getId()));
                }
            }

        btnClearItem.setText("Clear "+checkedDone.size()+" completed item");
        btnClearItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteRepository.deleteTaskMultiple(checkedDone);
                notesListAdapter.notifyDataSetChanged();
            }
        });




    }
}