package com.namson_nguyen.myruns5

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.namson_nguyen.myruns5.database.*
import com.namson_nguyen.myruns5.databinding.ActivityMapEntryScreenBinding

class MapEntryScreen : AppCompatActivity(), OnMapReadyCallback, LocationListener {
    private var mapCentered = false

    //Map variable
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapEntryScreenBinding
    private lateinit var locationManager: LocationManager
    private lateinit var markerOptions : MarkerOptions
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var polyLines : ArrayList<Polyline>
    private lateinit var latLngList : ArrayList<LatLng>

    //UI variables
    private lateinit var statsScreen : TextView
    private var statString : String = ""
    private val activityTypes = arrayOf(
        "Running", "Walking", "Standing",
        "Cycling", "Hiking", "Downhill Skiing",
        "Cross-Country Skiing", "Snowboarding", "Skating",
        "Swimming", "Mountain Biking", "Wheelchair",
        "Elliptical", "Other"
    )

    //Database variables
    private lateinit var exerciseEntry : ExerciseEntry
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var factory: ExerciseEntryViewModelFactory
    private lateinit var viewModel: ExerciseEntryViewModel

    //Entries variables
    private lateinit var entries : List<ExerciseEntry>
    private lateinit var requiredEntry : ExerciseEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapEntryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        latLngList = ArrayList()
        markerOptions = MarkerOptions()
        polylineOptions = PolylineOptions()
        //Initializing stat screen with correct properties
        statsScreen = findViewById(R.id.history_activity_stats_screen)
        statsScreen.setTextColor(Color.BLACK)
        statsScreen.textSize = 20f

        //Getting data from database
        exerciseEntry = ExerciseEntry()
        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        factory = ExerciseEntryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ExerciseEntryViewModel::class.java]
        entries = ArrayList()
        val bundle = intent.extras
        viewModel.allExerciseEntries.observe(this){
            entries = it
            val position = bundle!!.getInt("POSITION_KEY")
            if (position < entries.size) {
                requiredEntry = entries[position]
            }
            statString = "Type: ${activityTypes[requiredEntry.activityType]}\n" +
                    "Avg speed: ${String.format("%.2f",requiredEntry.avgPace)} m/h\n" +
                    "Cur speed:n/a m/h\n" +
                    "Climb: ${String.format("%.2f", requiredEntry.climb)} miles\n" +
                    "Calorie: ${String.format("%.2f", requiredEntry.calorie)} calories\n" +
                    "Distance: ${String.format("%.2f", requiredEntry.distance)} miles"
            statsScreen.text = statString
            latLngList = requiredEntry.locationList

            markerOptions.position(latLngList[0])
            mMap.addMarker(markerOptions)
            markerOptions.position(latLngList[latLngList.size - 1])
            mMap.addMarker(markerOptions)
            for (latLng in latLngList){
                polylineOptions.add(latLng)
                val polyline = mMap.addPolyline(polylineOptions)
                polyLines.add(polyline)

            }
        }
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
        initLocationManager()
    }

    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val long = location.longitude
        val latLng = LatLng(lat, long)
        //Zoom to the current LatLng
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        if (!mapCentered) {
            //Animate the camera to zoom in the location when it's not centered
            mMap.animateCamera(cameraUpdate)
            //Update map centered to now be true
            mapCentered = true
        }

    }

    /**
     * Setting up location manager
     */
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
                println(location.hasSpeed())
            }
            locationManager.requestLocationUpdates(provider, 0, 0f, this)
        } catch (e: SecurityException) {
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