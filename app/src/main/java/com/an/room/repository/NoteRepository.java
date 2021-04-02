package com.an.room.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.an.room.db.NoteDatabase;
import com.an.room.model.Note;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteRepository {

    private String DB_NAME = "db_task";

    private NoteDatabase noteDatabase;
    public NoteRepository(Context context) {
        noteDatabase = Room.databaseBuilder(context, NoteDatabase.class, DB_NAME).build();
    }

    public void insertTask(String title,
                           String dateinput,
                           Boolean done) {

        Note note = new Note();
        note.setTitle(title);
        note.setDateInput(dateinput);
        note.setDone(done);

        insertTask(note);
    }

    public void insertTask(final Note note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                noteDatabase.daoAccess().insertTask(note);
                return null;
            }
        }.execute();
    }

    public void updateTask(final Note note,boolean done) {

        note.setDone(done);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                noteDatabase.daoAccess().updateTask(note);
                return null;
            }
        }.execute();
    }

    public void deleteTaskMultiple(final List<Integer> id) {
        final LiveData<Note> task = getTask(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Log.e("TAG", "doInBackground: "+id);
                    noteDatabase.daoAccess().deleteMultiple(id);
                    return null;
                }
            }.execute();
        }
    }

    public void deleteTask(final Note note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                noteDatabase.daoAccess().deleteTask(note);
                return null;
            }
        }.execute();
    }

    public LiveData<Note> getTask(List<Integer> id) {
        return noteDatabase.daoAccess().getTaskMultiple(id);
    }

    // https://stackoverflow.com/questions/51265338/how-to-get-all-data-from-room-table-in-android-using-async-task-in-a-list/51265902#
    public boolean getTaskByTitle(final String title) {
        try {
            return new GetUsersAsyncTask(title).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getNumbersDone() {
        try {
            return new GetDoneAsyncTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class GetUsersAsyncTask extends AsyncTask<Void, Void,Boolean> {
        String mTitle;
        GetUsersAsyncTask(String title){
            this.mTitle = title;
        }
        @Override
        protected Boolean doInBackground(Void... url) {
            return  noteDatabase.daoAccess().existsTitle(mTitle);
        }
    }

    private class GetDoneAsyncTask extends AsyncTask<Void, Void,Integer> {
        @Override
        protected Integer doInBackground(Void... url) {
            return  noteDatabase.daoAccess().getNumberDoneOfRows();
        }
    }



    public LiveData<List<Note>> getTasks() {
        return noteDatabase.daoAccess().fetchAllTasks();
    }
}
