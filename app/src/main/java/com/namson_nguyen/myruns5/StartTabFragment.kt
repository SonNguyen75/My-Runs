package com.namson_nguyen.myruns5

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment

class StartTabFragment : Fragment() {
    private lateinit var startButton: Button
    private lateinit var inputType : Spinner
    private lateinit var activitySpinner: Spinner
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.start_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startButton = view.findViewById(R.id.startButton)
        inputType = view.findViewById(R.id.inputSpinner)
        activitySpinner = view.findViewById(R.id.activitySpinner)
        startButton.setOnClickListener {
            if (inputType.selectedItem.toString() == "Manual Entry"){
                var index = activitySpinner.selectedItemPosition
                var manualMode : Intent = Intent(activity, ManualEntryActivity::class.java)
                manualMode.putExtra("SPINNER_ITEM", index)
                activity?.startActivity(manualMode)
            }
            else if (inputType.selectedItem.toString() == "GPS"){
                var index = activitySpinner.selectedItemPosition
                var gpsMode : Intent = Intent (activity, MapActivity::class.java)
                gpsMode.putExtra("SPINNER_ITEM", index)
                activity?.startActivity(gpsMode)
            }
            else{
                var automaticMode : Intent = Intent(activity, AutomaticEntryActivity::class.java)
                activity?.startActivity(automaticMode)
            }
        }
    }

}