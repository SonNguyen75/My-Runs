package com.namson_nguyen.myruns5.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ExerciseEntry::class], version = 1)
@TypeConverters(com.namson_nguyen.myruns5.database.TypeConverters::class)
abstract class ExerciseDatabase : RoomDatabase(){
    abstract val exerciseEntryDatabaseDao : ExerciseEntryDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE : ExerciseDatabase? = null
        fun getInstance(context: Context) : ExerciseDatabase{
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ExerciseDatabase::class.java,
                        "exercise_entry_DB"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}