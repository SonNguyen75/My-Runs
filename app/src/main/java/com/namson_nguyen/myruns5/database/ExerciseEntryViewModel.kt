package com.namson_nguyen.myruns5.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.CreationExtras

class ExerciseEntryViewModel(private val repository: ExerciseEntryRepository) : ViewModel(){
    //Getting all entries from the repository
    val allExerciseEntries = repository.allExerciseEntries.asLiveData()
    //Insert an entry to the repository
    fun insert(entry: ExerciseEntry){
        repository.insert(entry)
    }

    //Delete an item from the repository with given id
    fun delete(id : Long){
        val entryList = allExerciseEntries.value
        //Only delete if the entryList if not empty and is initialized
        if (entryList != null && entryList.isNotEmpty()){
            repository.delete(id)
        }
    }

    //Delete all item from repository (not needed right now but still useful for debugging)
    fun deleteAll(){
        val entryList = allExerciseEntries.value
        //Only delete if the entryList if not empty and is initialized
        if (entryList != null && entryList.isNotEmpty()){
            repository.deleteAll()
        }
    }

}

class ExerciseEntryViewModelFactory(private val repository: ExerciseEntryRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ExerciseEntryViewModel::class.java)){
            return ExerciseEntryViewModel(repository) as T
        }
        throw java.lang.IllegalArgumentException("ERROR!")
    }
}