package com.namson_nguyen.myruns5

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.namson_nguyen.myruns5.database.ExerciseEntry
import java.text.SimpleDateFormat
import java.util.*

class ListViewAdapter(private val context : Context, private var entriesList: List<ExerciseEntry>) : BaseAdapter() {
    //Imperial mode : imperialFlag = true, Metric mode: imperialFlag = true
    private var unit : String = ""
    private val inputTypes = arrayOf("Manual", "GPS", "Automatic")
    private val activityTypes = arrayOf(
       "Running", "Walking", "Standing",
        "Cycling", "Hiking", "Downhill Skiing",
        "Cross-Country Skiing", "Snowboarding", "Skating",
        "Swimming", "Mountain Biking", "Wheelchair",
        "Elliptical", "Other"
    )
    private val sharedPrefs : SharedPreferences =  context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

    override fun getCount(): Int {
        return entriesList.size
    }

    override fun getItem(position: Int): Any {
        return entriesList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.list_view_item, null)
        val labelTextView = view.findViewById(R.id.labelTextView) as TextView
        val detailTextView = view.findViewById(R.id.detailTextView) as TextView
        //Get the minute part of duration by getting the whole part of it
        val minutes : Int = (entriesList[position].duration / 1).toInt() //Get the whole part by divide by 1
        //Get the second part of duration by getting the decimal part of it and times it by 60
        val seconds :Int = (entriesList[position].duration % 1 * 60).toInt() //Get the decimal part by % by 1
        //Format the time from entry.datetime to the correct format of HH:mm:ss MMM dd yyyy
        var date = entriesList[position].dateTime.time
        var dateTimeFormatter = SimpleDateFormat("HH:mm:ss MMM dd yyyy")
        var timeStamp = date.time
        //Setting the label for each of entry of the list with input mode, activity mode and date data
        labelTextView.text = "${ inputTypes[entriesList[position].inputType] }:" +
                            "${ activityTypes[entriesList[position].activityType] }, "+
                            "${ dateTimeFormatter.format(timeStamp) }"
        //Setting the more detail textview for each of the entry with distance and duration of that entry with either metric or imperial value
        if (unit == "miles"){
            //Default unit is miles
            detailTextView.text = "${ String.format("%.2f",entriesList[position].distance) }${unit}, ${minutes}mins ${seconds}secs"
        }
        else{
            //If unit option is kilometer then we multiply by 1.6 to get metric value
            detailTextView.text = "${ entriesList[position].distance * 1.6 }${unit}, ${minutes}mins ${seconds}secs"
        }
        return view
    }

    //Replace current list holding all current entries with a new one
    fun replaceList(newList : List<ExerciseEntry>){
        entriesList = newList
    }

    //Update the unit used for distance in "History" tab, newUnit can either be "kilometer" or "miles"
    fun updateUnit(newUnit : String){
        unit = newUnit
    }
}