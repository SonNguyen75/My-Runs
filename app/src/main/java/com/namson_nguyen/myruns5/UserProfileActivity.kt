package com.namson_nguyen.myruns5

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class UserProfileActivity : AppCompatActivity(), DialogInterface.OnClickListener {
    private lateinit var imageView: ImageView
    private lateinit var imageFile: File
    private lateinit var backupImageFile: File
    private lateinit var imageURI: Uri
    private lateinit var galleryImageFile : File
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var sharedPrefs: SharedPreferences

    private val options = arrayOf("Open Camera", "Choose From Gallery")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        galleryImageFile = File(getExternalFilesDir(null), "galleryImgFile.jpg")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharedPrefs = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        Util.checkPermissions(this)
        loadInfo(sharedPrefs)
        imageURI = FileProvider.getUriForFile(this, "com.namson_nguyen.myruns5", imageFile)
        imageBackup()
        loadPrevImage()
        startCamera()
        startGallery()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Settings")
        return super.onCreateOptionsMenu(menu)
    }

    fun onChangeButtonClick(view: View) {
        val builder = AlertDialog.Builder(this).setTitle("Pick Profile Picture")
        builder.setItems(options, this)
        builder.show()

    }

    fun onSaveButtonClick(view: View) {
        val sharedPrefs = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        saveInfo(sharedPrefs)
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
    }


    fun onCancelButtonClick(view: View) {
        if (imageFile.exists()) {
            imageFile.delete()
        }
        backupImageFile.copyTo(imageFile)
        finish()
    }

    private fun saveInfo(sharedPrefs: SharedPreferences) {
        val editor = sharedPrefs.edit()

        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val phoneEditText: EditText = findViewById(R.id.phoneEditText)
        val classEditText: EditText = findViewById(R.id.classEditText)
        val majorEditText: EditText = findViewById(R.id.majorEditText)

        saveEditText(editor, "KEY_NAME", nameEditText)
        saveEditText(editor, "KEY_PHONE", phoneEditText)
        saveEditText(editor, "KEY_CLASS", classEditText)
        saveEditText(editor, "KEY_MAJOR", majorEditText)
        saveRadioButtonChoice(sharedPrefs, editor)

        editor.apply()
        finish()
    }

    private fun loadInfo(sharedPrefs: SharedPreferences) {
        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val phoneEditText: EditText = findViewById(R.id.phoneEditText)
        val classEditText: EditText = findViewById(R.id.classEditText)
        val majorEditText: EditText = findViewById(R.id.majorEditText)

        val savedName = loadEditText(sharedPrefs, "KEY_NAME")
        val savedPhone = loadEditText(sharedPrefs, "KEY_PHONE")
        val savedClass = loadEditText(sharedPrefs, "KEY_CLASS")
        val savedMajor = loadEditText(sharedPrefs, "KEY_MAJOR")

        nameEditText.setText(savedName)
        phoneEditText.setText(savedPhone)
        classEditText.setText(savedClass)
        majorEditText.setText(savedMajor)
        loadRadioButton(sharedPrefs)

        imageView = findViewById(R.id.profileImgView)
        imageFile = File(getExternalFilesDir(null), "profilePic.jpg")
    }

    /**
     * Create a backup of the original profilePic.jpg
     * In case user press "CANCEL" it will delete the profile photo user just took and replace profilePic.jpg with its backup
     **/
    private fun imageBackup() {
        backupImageFile = File(getExternalFilesDir(null), "backupProfilePic.jpg")
        if (backupImageFile.exists()) {
            backupImageFile.delete()
        }
        if (imageFile.exists()) {
            imageFile.copyTo(backupImageFile)
        }
        FileProvider.getUriForFile(this, "com.namson_nguyen.myruns5", backupImageFile)
    }

    private fun loadPrevImage() {
        if (imageFile.exists()) {
            val bitmap = Util.getBitmap(this, imageURI)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun startCamera() {
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val bitmap = Util.getBitmap(this, imageURI)
                imageView.setImageBitmap(bitmap)
            }

        }
    }

    private fun startGallery(){
        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imageURI = it.data?.data!!
                val bitmap = Util.getBitmap(this, imageURI)
                imageView.setImageBitmap(bitmap)
                val os : OutputStream
                os = FileOutputStream(galleryImageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()
                if (imageFile.exists()){
                    imageFile.delete()
                    galleryImageFile.copyTo(imageFile)
                }
            }
        }
    }

    private fun saveEditText(
        editor: SharedPreferences.Editor,
        keyName: String,
        editText: EditText
    ) {
        editor.putString(keyName, editText.text.toString())
    }

    private fun saveRadioButtonChoice(
        sharedPrefs: SharedPreferences,
        editor: SharedPreferences.Editor
    ) {
        val femaleRadioButton: RadioButton = findViewById(R.id.femaleButton)
        val maleRadioButton: RadioButton = findViewById(R.id.maleButton)
        editor.putBoolean("MALE_CHOICE", maleRadioButton.isChecked)
        editor.putBoolean("FEMALE_CHOICE", femaleRadioButton.isChecked)
    }

    private fun loadEditText(sharedPrefs: SharedPreferences, keyName: String): String? {
        return sharedPrefs.getString(keyName, "")
    }

    private fun loadRadioButton(sharedPrefs: SharedPreferences) {
        val femaleRadioButton: RadioButton = findViewById(R.id.femaleButton)
        val maleRadioButton: RadioButton = findViewById(R.id.maleButton)
        val savedMaleChoice = sharedPrefs.getBoolean("MALE_CHOICE", false)
        val savedFemaleChoice = sharedPrefs.getBoolean("FEMALE_CHOICE", false)
        femaleRadioButton.isChecked = savedFemaleChoice
        maleRadioButton.isChecked = savedMaleChoice
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        if (p1 == 0) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
            cameraResult.launch(intent)
        }
        else if (p1 == 1){
            val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
            galleryResult.launch(intent)

        }
    }

}