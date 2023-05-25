package com.namson_nguyen.myruns5

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import com.namson_nguyen.myruns5.database.*
import java.util.*

class HistoryTabFragment : Fragment() {
    private lateinit var exerciseEntry: ExerciseEntry
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var factory: ExerciseEntryViewModelFactory
    private lateinit var viewModel: ExerciseEntryViewModel

    private lateinit var listView: ListView
    private lateinit var adapter: ListViewAdapter
    private lateinit var entries: ArrayList<ExerciseEntry>
    private lateinit var sharedPreferences : SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.history_tab, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        listView = view.findViewById(R.id.history_list_view)
        //Setting up database
        exerciseEntry = ExerciseEntry()
        database = ExerciseDatabase.getInstance(requireActivity())
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        factory = ExerciseEntryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ExerciseEntryViewModel::class.java]
        //Create a List for ListViewAdapter
        entries = ArrayList()
        adapter = ListViewAdapter(requireActivity(), entries)
        listView.adapter = adapter
        //Observing LiveData of ViewModel for any change, and update the data in ListViewAdapter class
        viewModel.allExerciseEntries.observe(requireActivity()) {
            entries = it as ArrayList<ExerciseEntry>
            adapter.replaceList(it)
            adapter.notifyDataSetChanged() //Notifying data set has been changed and re-rendering ListView
        }

        //Create onItemClickListener for each of the item in ListView
        listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                when (entries[position].inputType) {
                    0 -> {
                        //If an item is pressed, it will launch an activity with POSITION_KEY to identify which entry is launched
                        var intent = Intent(activity, ExerciseEntryScreen::class.java)
                        var bundle : Bundle = Bundle()
                        val unit = sharedPreferences.getString("unitListPreference", "")
                        bundle.putInt("POSITION_KEY", position)
                        bundle.putString("UNIT_KEY", unit)
                        intent.putExtras(bundle)
                        activity?.startActivity(intent)
                    }
                    1,2 -> {
                        var intent = Intent(activity, MapEntryScreen::class.java)
                        var bundle : Bundle = Bundle()
                        val unit = sharedPreferences.getString("unitListPreference", "")
                        bundle.putInt("POSITION_KEY", position)
                        bundle.putString("UNIT_KEY", unit)
                        intent.putExtras(bundle)
                        activity?.startActivity(intent)
                    }
                }
            }

        return view
    }

    override fun onResume() {
        super.onResume()
        //Check if user has updated their unit option on resume
        val unit = sharedPreferences.getString("unitListPreference", "")
        if (unit != null) {
            adapter.updateUnit(unit)            //Updating the unit in Adapter has changed
            adapter.notifyDataSetChanged()      //Notifying adater data has changed and re-rendering ListView
        }
    }

}
