package com.ucr.lescoaprende

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.FirebaseApp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_LescoAprende)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun sendAboutUs(view: View) {
        val intent = Intent(this, AboutUs::class.java)
        startActivity(intent)
    }

    fun sendDictionary(view: View) {
        val intent = Intent(this, Dictionary::class.java)
        startActivity(intent)
    }

    private fun validateDayTip() {
        val sharedPreferences: SharedPreferences? = getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        val day = sharedPreferences?.getString(getString(R.string.day_key), null)
        val df: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
        val currentDate = df.format(Calendar.getInstance().time)

        day?.let {
            if (currentDate.compareTo(it) != 0) {
                // Muestro el popup de dennis UwU
                with(sharedPreferences.edit()) {
                    putString(getString(R.string.day_key), currentDate)
                    commit()
                }
            }
        }?:run {
            // Muestro el popup de dennis UwU
            with(sharedPreferences!!.edit()) {
                putString(getString(R.string.day_key), currentDate)
                commit()
            }
        }
    }
}