package com.namson_nguyen.myruns5.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseEntryDatabaseDao {
    //Insert an exercise entry in the parameter to the database
    @Insert
    suspend fun insertExerciseEntry(exerciseEntry: ExerciseEntry)

    //Get all of the entries from the database
    @Query("SELECT * FROM exercise_entry_table")
    fun getAllExerciseEntries(): Flow<List<ExerciseEntry>>

    //Delete an entry from the database with the given id
    @Query("DELETE FROM exercise_entry_table WHERE id = :key") //":" indicates that it is a Bind variable
    suspend fun deleteExerciseEntry(key: Long)

    //Delete all of the entries (might not need this for now)
    @Query("DELETE FROM exercise_entry_table")
    suspend fun deleteAll()
}