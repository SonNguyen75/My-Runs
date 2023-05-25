package com.namson_nguyen.myruns5.database

import android.icu.util.Calendar
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * Type converters used to convert from a Calendar object to a Long when inserting to database
 * And converting a Long from database to a Calendar object
 */
class TypeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        return value?.let { Calendar.getInstance().apply { timeInMillis = it } }
    }

    @TypeConverter
    fun toTimestamp(timestamp: Calendar?): Long? {
        return timestamp?.timeInMillis
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<LatLng?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun toArrayList(value: String?): ArrayList<LatLng>? {
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        return Gson().fromJson(value, listType)
    }
}