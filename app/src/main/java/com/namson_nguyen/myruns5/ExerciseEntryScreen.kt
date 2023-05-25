package com.namson_nguyen.myruns5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.namson_nguyen.myruns5.database.*
import java.text.SimpleDateFormat
import java.util.*

class ExerciseEntryScreen : AppCompatActivity() {
    private lateinit var exerciseEntry : ExerciseEntry
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var factory: ExerciseEntryViewModelFactory
    private lateinit var viewModel: ExerciseEntryViewModel

    private lateinit var entries : List<ExerciseEntry>
    private lateinit var requiredEntry : ExerciseEntry

    private lateinit var inputTypeEditText : EditText
    private lateinit var activityTypeEditText: EditText
    private lateinit var dateAndTimeEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var distanceEditText: EditText
    private lateinit var calorieEditText: EditText
    private lateinit var heartRateEditText: EditText

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_entry_screen)
//        var toolbar : Toolbar? = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)

        //Getting the bundle with additional to get the entry
        val bundle = intent.extras
        //Initializing EditText fields
        inputTypeEditText = findViewById(R.id.inputTypeEditText)
        activityTypeEditText = findViewById(R.id.activityTypeEditText)
        dateAndTimeEditText = findViewById(R.id.dateAndTimeEditText)
        durationEditText = findViewById(R.id.durationEditText)
        distanceEditText = findViewById(R.id.distanceEditText)
        calorieEditText = findViewById(R.id.calorieEditText)
        heartRateEditText = findViewById(R.id.heartRateEditText)

        //Setting up database
        exerciseEntry = ExerciseEntry()
        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        factory = ExerciseEntryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ExerciseEntryViewModel::class.java]
        entries = ArrayList()
        viewModel.allExerciseEntries.observe(this) {
            entries = it
            val position = bundle!!.getInt("POSITION_KEY")
            if (position < entries.size) {
                requiredEntry = entries[position]
            }
            //Setting each EditText to the corresponding value from requiredEntry
            inputTypeEditText.setText(inputTypes[requiredEntry.inputType])
            activityTypeEditText.setText(activityTypes[requiredEntry.activityType])

            //Formatting date
            val date = requiredEntry.dateTime.time
            val dateTimeFormatter = SimpleDateFormat("HH:mm:ss MMM dd yyyy")
            val timeStamp = date.time
            dateAndTimeEditText.setText("${ dateTimeFormatter.format(timeStamp) }")

            //Setting the more detail textview for each of the entry with distance and duration of that entry with either metric or imperial value
            unit = bundle.getString("UNIT_KEY")!!
            if (unit == "miles"){
                distanceEditText.setText("${requiredEntry.distance}miles")
            }
            else{
                distanceEditText.setText("${requiredEntry.distance * 1.6}kilometers")
            }
            //Get the minute part of duration by getting the whole part of it
            val minutes : String = (requiredEntry.duration / 1).toInt().toString() //Get the whole part by divide by 1
            //Get the second part of duration by getting the decimal part of it and times it by 60
            val seconds : String = (requiredEntry.duration % 1 * 60).toInt().toString() //Get the decimal part by % by 1
            durationEditText.setText("${minutes}mins ${seconds}secs")
            calorieEditText.setText("${requiredEntry.calorie} cals")
            heartRateEditText.setText("${requiredEntry.heartRate} bpm")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_exercise_entry_screen, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteEntry){
            viewModel.delete(requiredEntry.id)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}