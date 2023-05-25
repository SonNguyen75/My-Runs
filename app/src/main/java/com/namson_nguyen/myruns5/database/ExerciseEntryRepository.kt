package com.namson_nguyen.myruns5.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExerciseEntryRepository(private val exerciseEntryDatabaseDao: ExerciseEntryDatabaseDao) {
    //Getting a Flow of List of ExerciseEntries from ExerciseEntryDao by using getAllExerciseEntries()
    val allExerciseEntries : Flow<List<ExerciseEntry>> = exerciseEntryDatabaseDao.getAllExerciseEntries()

    //Insert an exercise entry into the database
    fun insert(exerciseEntry: ExerciseEntry){
        CoroutineScope(IO).launch {
            exerciseEntryDatabaseDao.insertExerciseEntry(exerciseEntry)
        }
    }

    //Delete an exercise entry from the database
    fun delete(id : Long){
        CoroutineScope(IO).launch {
            exerciseEntryDatabaseDao.deleteExerciseEntry(id)
        }
    }

    //Delete all of the entries from the database(might not need this for now)
    fun deleteAll(){
        CoroutineScope(IO).launch {
            exerciseEntryDatabaseDao.deleteAll()
        }
    }
}