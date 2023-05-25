package com.namson_nguyen.myruns5

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.util.Calendar
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.namson_nguyen.myruns5.database.*
import com.namson_nguyen.myruns5.databinding.ActivityGpsEntryBinding

class AutomaticEntryActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {
    private var mapCentered = false
    private val PERMISSION_REQUEST_CODE = 0
    private lateinit var sensorServiceIntent: Intent
    //Map variables
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityGpsEntryBinding
    private lateinit var locationManager: LocationManager
    private lateinit var markerOptions : MarkerOptions
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var polyLines : ArrayList<Polyline>
    private lateinit var latLngList : ArrayList<LatLng>
    private lateinit var locationList : ArrayList<Location>

    //Running stats variable
    private var activityType : String = ""
    private var distance : Double = 0.0
    private var calorie : Double = 0.0
    private var climb : Double = 0.0
    private var avgSpeed : Double = 0.0
    private var startTime : Long = 0
    private lateinit var speedList : ArrayList<Float>

    //UI variables
    private lateinit var statsScreen : TextView
    private lateinit var cancelButton : Button
    private lateinit var saveButton : Button
    private val activityTypes = arrayOf(
        "Running", "Walking", "Standing",
        "Cycling", "Hiking", "Downhill Skiing",
        "Cross-Country Skiing", "Snowboarding", "Skating",
        "Swimming", "Mountain Biking", "Wheelchair",
        "Elliptical", "Other"
    )

    //Database variables
    private lateinit var exerciseEntry: ExerciseEntry
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var factory: ExerciseEntryViewModelFactory
    private lateinit var viewModel: ExerciseEntryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startTime = System.currentTimeMillis()
        binding = ActivityGpsEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //Initializing stat screen with correct properties
        statsScreen = findViewById(R.id.type_stats)
        statsScreen.setTextColor(Color.BLACK)
        statsScreen.textSize = 20f

        //Initializing ArrayList
        latLngList = ArrayList()
        locationList = ArrayList()
        speedList = ArrayList()

        //Setting up database
        exerciseEntry = ExerciseEntry()
        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        factory = ExerciseEntryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ExerciseEntryViewModel::class.java]

        //Setting up button onClickListener
        cancelButton = findViewById(R.id.gpsActivityCancelButton)
        cancelButton.setOnClickListener {
            finish()
        }

        saveButton = findViewById(R.id.gpsActivitySaveButton)
        saveButton.setOnClickListener {
            onSaveButtonClick()
            finish()
        }
        sensorServiceIntent = Intent(this, SensorService::class.java)
        startService(sensorServiceIntent)

        //Getting which activity it currently is via Weka
        var messageReceiver = MessageReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter("GPSLocationUpdates"))

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        //Getting Google Map Ready
        mMap = googleMap
        markerOptions = MarkerOptions()
        //Getting polyline options to draw the path on the screen
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)              //Set the line color to Black
        polyLines = ArrayList()                         //Initializing poly lines array list
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL        //Get normal type of Google Map
        checkPermission()

    }

    /**
     * Setting up location manager
     */
    @SuppressLint("MissingPermission")
    private fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            //Supporting altitude
            criteria.isAltitudeRequired = true
            criteria.isSpeedRequired = true
            val provider = locationManager.getBestProvider(criteria, true)
            val location = locationManager.getLastKnownLocation(provider!!)
            if(location != null){
                onLocationChanged(location)
            }
            locationManager.requestLocationUpdates(provider, 0, 0f, this)
        } catch (e: SecurityException) {
        }
    }

    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val long = location.longitude
        val latLng = LatLng(lat, long)
        val index = sensorServiceIntent.getIntExtra("SPINNER_ITEM", 0)
        var stats : String = "Type: ${activityTypes[index]}\n" +
                "Avg speed: 0,00 m/h\n" +
                "Cur speed: 0,00 m/h\n" +
                "Climb: 0 miles\n" +
                "Calorie: 0\n" +
                "Distance: 0,00 miles"
        //Zoom to the current LatLng
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        //If map is not centered when it is first launched
        if (!mapCentered) {
            //Save a copy of the original LatLng when the app first got launched
            latLngList.add(latLng)
            //Animate the camera to zoom in the location when it's not centered
            mMap.animateCamera(cameraUpdate)
            //Add a marker at the original location
            markerOptions.position(latLng)
            mMap.addMarker(markerOptions)
            polylineOptions.add(latLng)
            //Initializing stat screens with defaults info
            val index = sensorServiceIntent.getIntExtra("SPINNER_ITEM", 0)
            statsScreen.text = stats
            //Update map centered to now be true
            mapCentered = true
        }
        //Adding new latLng to the LatLng list
        latLngList.add(latLng)
        exerciseEntry.locationList = latLngList

        locationList.add(location)
        //Clear every marker on the map to draw a marker with updated location
        mMap.clear()
        //Since we remove every marker we need to redraw the original marker using the first LatLng value in location list
        markerOptions.position(latLngList[0])
        mMap.addMarker(markerOptions)
        //Drawing new marker with the updated location
        markerOptions.position(latLng)
        mMap.addMarker(markerOptions)
        //Adding a a polyline based on current location
        polylineOptions.add(latLng)
        val polyline = mMap.addPolyline(polylineOptions)
        polyLines.add(polyline)

        //Update stats screen with new info
        /**For distance the second newest location is the starting location and newest is the destination
         */
        if (locationList.size >= 2){
            val previousLocation = locationList[locationList.size - 2]
            val currentLocation = locationList[locationList.size - 1]
            //Distance returns in meters so we times by 1000 to get km and 0.62 for miles
            distance += (currentLocation.distanceTo(previousLocation)/1000 * 0.621371)
            exerciseEntry.distance = distance
            //Climb is current location altitude minus last location altitude
            climb += (currentLocation.altitude - previousLocation.altitude)/1000 * 0.621371
            exerciseEntry.climb = climb
        }
        var totalSpeed = 0.0
        if (speedList.size > 1){
            for (speed in speedList){
                totalSpeed += speed
            }
            avgSpeed = totalSpeed / speedList.size
            exerciseEntry.avgPace = avgSpeed
        }
        //Assuming every mile consumes 100 calorie
        calorie = (distance * 100)
        exerciseEntry.calorie = calorie

        stats = "Type: $activityType\n" +
                "Avg speed:${String.format("%.2f", avgSpeed)} m/h\n" +
                "Cur speed : ${String.format("%.2f", location.speedAccuracyMetersPerSecond * 2.236936)} m/h\n" + //Times 2.236936 to get miles/hour
                "Climb: ${String.format("%.2f", climb)} miles\n" +
                "Calorie: ${String.format("%.2f",calorie)} calories\n" +
                "Distance: ${String.format("%.2f", distance)} miles"
        println(distance)
        statsScreen.text = stats

    }


    override fun onMapLongClick(p0: LatLng) {

    }

    override fun onMapClick(p0: LatLng) {

    }
    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
        stopService(sensorServiceIntent)
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        else
            initLocationManager()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) initLocationManager()
        }
    }

    private fun onSaveButtonClick(){
        //Automatic mode, input type = 2
        exerciseEntry.inputType = 2
        //Get the activity type from StartTabFragment
        when(activityType){
            //Setting activity type based on spinner item index
            "Standing" -> {
                exerciseEntry.activityType = 2
            }
            "Walking" -> {
                exerciseEntry.activityType = 1
            }
            "Running" ->{
                exerciseEntry.activityType = 0
            }
        }

        //Set the date time for the entry as the current time when the user pressed save
        exerciseEntry.dateTime = Calendar.getInstance()
        //Set the rest of entry's fields
        //Since the timestamp is in millisecond we need to convert it to minutes
        exerciseEntry.duration = (System.currentTimeMillis() - startTime) / 60000.00
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
    inner class MessageReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            var pValue = intent?.getDoubleExtra("p_value", 0.0)
            when(pValue) {
                0.0 -> {
                    activityType = "Standing"
                }
                1.0 -> {
                    activityType = "Walking"
                }
                2.0 -> {
                    activityType = "Running"
                }
            }
        }

    }
}


