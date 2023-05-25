package com.namson_nguyen.myruns5.database

import android.icu.util.Calendar
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "exercise_entry_table")
class ExerciseEntry {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    @ColumnInfo(name = "input_type_column")
    var inputType : Int = 0

    @ColumnInfo(name = "activity_type_column")
    var activityType : Int = 0

    @ColumnInfo(name = "date_time_column")
    var dateTime : Calendar = Calendar.getInstance()

    @ColumnInfo(name = "duration_column")
    var duration : Double = 0.0

    @ColumnInfo(name = "distance_column")
    var distance : Double = 0.0

    @ColumnInfo(name = "avg_pace_column")
    var avgPace : Double = 0.0

    @ColumnInfo(name = "calorie_column")
    var calorie : Double = 0.0

    @ColumnInfo(name = "climb_column")
    var climb : Double = 0.0

    @ColumnInfo(name = "heart_rate_column")
    var heartRate : Double = 0.0

    @ColumnInfo(name = "comment")
    var comment : String = ""

    @ColumnInfo(name = "location_list_column")
    var locationList : ArrayList<LatLng> = ArrayList()
}