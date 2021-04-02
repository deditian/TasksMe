package com.an.room.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.an.room.model.Note;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    Long insertTask(Note note);

    @Query("SELECT * FROM Note ORDER BY dateInput desc")
    LiveData<List<Note>> fetchAllTasks();

    @Update
    void updateTask(Note note);

    @Delete
    void deleteTask(Note note);

    @Query("DELETE from Note where id in (:taskId)")
    void deleteMultiple(List<Integer> taskId);

    @Query("SELECT * FROM Note WHERE id =:taskId")
    LiveData<Note> getTaskMultiple(List<Integer> taskId);

    @Query("SELECT EXISTS (SELECT 1 FROM Note WHERE title = :id)")
    Boolean existsTitle(String id);

    //https://stackoverflow.com/questions/47864156/how-can-i-get-count-of-number-of-rows-that-have-boolean-value-trueor-1-in-room
    @Query("SELECT COUNT(DONE) FROM Note WHERE DONE = 0")
    int getNumberDoneOfRows();

}
