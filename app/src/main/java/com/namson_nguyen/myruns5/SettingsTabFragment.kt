package com.namson_nguyen.myruns5

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.preference.*


class SettingsTabFragment : PreferenceFragmentCompat() {
    private lateinit var userPreferenceScreen: PreferenceScreen
    private lateinit var unitListPreference: ListPreference
    private lateinit var linkPreferenceScreen: PreferenceScreen
    private lateinit var sharedPrefs: SharedPreferences
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        setPreferencesFromResource(R.xml.setting_tabs, rootKey)
        userPreferenceScreen = findPreference("userProfile")!!
        userPreferenceScreen.setOnPreferenceClickListener {
            var userProfileActivity: Intent = Intent(activity, UserProfileActivity::class.java)
            activity?.startActivity(userProfileActivity)
            true
        }

        unitListPreference = findPreference("unitListPreference")!!
        unitListPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener(){ preference: Preference, any: Any ->
            var editor = sharedPrefs.edit()
            editor.putString("UNIT_KEY", unitListPreference.value)
            editor.commit()
            true
        }
        linkPreferenceScreen = findPreference("link")!!
        linkPreferenceScreen.setOnPreferenceClickListener {
            var externalLinkIntent: Intent = Intent(Intent.ACTION_VIEW)
            externalLinkIntent.setData(Uri.parse(linkPreferenceScreen.summary.toString()))
            activity?.startActivity(externalLinkIntent)
            true
        }
    }

}