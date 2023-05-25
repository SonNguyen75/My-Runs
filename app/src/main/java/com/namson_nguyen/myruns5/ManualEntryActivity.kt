package com.namson_nguyen.myruns5

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.namson_nguyen.myruns5.database.*

class ManualEntryActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private val inputFields = arrayOf(
        "Date", "Time", "Duration", "Distance",
        "Calories", "Heart Rate", "Comment"
    )
    private val  calendar = Calendar.getInstance()
    private lateinit var activitySpinner : Spinner
    private lateinit var listView: ListView
    private lateinit var exerciseEntry: ExerciseEntry
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var factory : ExerciseEntryViewModelFactory
    private lateinit var viewModel: ExerciseEntryViewModel

    private var activityType : Int = 0
    private var dateSelected : Calendar = Calendar.getInstance()
    private var duration : Double = 0.0
    private var distance : Double = 0.0
    private var calorie : Double = 0.0
    private var heartRate : Double = 0.0
    private var comment : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entry)
        //Set up for ListView
        val arrayAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, inputFields)
        listView = findViewById(R.id.listView)

        var saveBtn: Button = Button(this)
        saveBtn.text = "SAVE"
        saveBtn.setOnClickListener(View.OnClickListener {
            onSaveButtonClick(saveBtn)
        })

        var cancelBtn: Button = Button(this)
        cancelBtn.text = "CANCEL"
        cancelBtn.setOnClickListener(View.OnClickListener {
            onCancelButtonClick(cancelBtn)
        })
        listView.addFooterView(saveBtn)
        listView.addFooterView(cancelBtn)
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { adapterView, view, i, l ->
            val element = arrayAdapter.getItem(i)
            if (element != null) {
                launchSelector(element)
            }
        }

        val view = View.inflate(this, R.layout.start_tab, null)
        activitySpinner = view.rootView.findViewById(R.id.activitySpinner)
        //Setting up database
        exerciseEntry = ExerciseEntry()
        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        factory = ExerciseEntryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ExerciseEntryViewModel::class.java]

    }
    private fun launchSelector(element: String){
        when (element) {
            "Date" -> {
                launchDateSelector()
            }
            "Time" -> {
                launchTimeSelector()
            }
            "Duration", "Distance", "Calories", "Heart Rate" -> {
                launchInputDialogBox(element, InputType.TYPE_NUMBER_FLAG_DECIMAL)
            }
            "Comment" -> {
                launchInputDialogBox(element, InputType.TYPE_CLASS_TEXT)
            }
        }
    }

    private fun launchDateSelector(){
        val datePickerDialog = DatePickerDialog(this,
                                                this,
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                calendar.get((Calendar.DAY_OF_MONTH)))
        datePickerDialog.show()
    }

    private fun launchTimeSelector(){
        val timePickerDialog = TimePickerDialog(this,
                                                this,
                                                calendar.get(Calendar.HOUR_OF_DAY),
                                                calendar.get(Calendar.MINUTE),
                                                true)

        timePickerDialog.show()
    }

    private fun launchInputDialogBox(element: String, type: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(element)
        val input = EditText(this)
        input.inputType = type
        builder.setView(input)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener(){ dialogInterface, i ->
            //Setting corresponding fields to corresponding values
            when(element){

                "Duration" -> {
                    duration = input.text.toString().toDouble()
                }
                "Distance" -> {
                    distance = input.text.toString().toDouble()
                }
                "Calories" -> {
                    calorie = input.text.toString().toDouble()
                }
                "Heart Rate" -> {
                    heartRate = input.text.toString().toDouble()
                }
                "Comment" -> {
                    comment = input.text.toString()
                }
            }
        })
        builder.setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialogInterface, i ->  })
        builder.show()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        //Set selected year, month, day to the calendar for database
        dateSelected.set(year,month,day)
        return
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        //Set selected hour, minute to the calendar for database
        dateSelected.set(Calendar.HOUR, hour)
        dateSelected.set(Calendar.MINUTE, minute)
        return
    }

    fun onCancelButtonClick(view: View) {
        Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun onSaveButtonClick(view: View) {
        //Manual Entry, input type is 0
        exerciseEntry.inputType = 0
        //Get the activity type from StartTabFragment
        activityType = intent.getIntExtra("SPINNER_ITEM", 0)
        exerciseEntry.activityType = activityType
        //Set the date time for the entry as the date time user selected
        exerciseEntry.dateTime = dateSelected
        /* For some reasons when storing to the database, Calendar time has been incremented by 12 hours
          This is to readjust to the correct time */
        exerciseEntry.dateTime.add(Calendar.HOUR, -12)
        //Set the rest of entry's fields
        exerciseEntry.distance = distance
        exerciseEntry.duration = duration
        exerciseEntry.calorie = calorie
        exerciseEntry.heartRate = heartRate
        exerciseEntry.comment = comment
        viewModel.insert(exerciseEntry)

        viewModel.allExerciseEntries.observe(this){
            //Getting the list from viewModel
            val list = it
            //Getting the newest entry from the list
            val newEntry = list[list.size - 1]
            //Notifying the user entry has been saved to the database
            Toast.makeText(this, "Entry #${newEntry.id} added.", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

}