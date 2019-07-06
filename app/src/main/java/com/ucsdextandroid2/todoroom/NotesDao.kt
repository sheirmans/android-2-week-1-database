package com.ucsdextandroid2.todoroom

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by rjaylward on 2019-07-05
 */

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes ORDER BY datetime DESC")
    fun getAllNotes(): List<Note>

    @Query("SELECT * FROM notes ORDER BY datetime DESC")
    fun getAllNotesLiveData(): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY datetime DESC")
    fun getAllNotesPaged(): DataSource.Factory<Int, Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

}
