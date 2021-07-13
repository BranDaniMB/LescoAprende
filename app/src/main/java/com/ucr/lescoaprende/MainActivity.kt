package com.ucr.lescoaprende

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ucr.lescoaprende.database.Database
import com.ucr.lescoaprende.database.DayTips
import com.ucr.lescoaprende.database.WordDetails
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    private lateinit var listenButton: Button;
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent;
    private val databaseRef = Database.instance;

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_LescoAprende)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}

            override fun onBeginningOfSpeech() {
                Toast.makeText(baseContext, "Escuchando...", Toast.LENGTH_SHORT).show();
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {}

            override fun onResults(results: Bundle?) {
                val data: ArrayList<String> =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>;
                analyzeSpeech(data[0]);
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        listenButton = findViewById(R.id.listen);
        listenButton.setOnTouchListener(
            object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    event?.let {
                        if (event.action == MotionEvent.ACTION_UP) {
                            speechRecognizer.stopListening();
                            Toast.makeText(baseContext, "Analizando...", Toast.LENGTH_SHORT).show();
                        } else if (event.action == MotionEvent.ACTION_DOWN) {
                            speechRecognizer.startListening(speechRecognizerIntent);
                        }
                    }
                    return false;
                }
            })
    }

    private fun analyzeSpeech(speechResults: String) {
        val counters: ArrayList<Int> = ArrayList();
        val wordDetails = databaseRef.wordDetails;
        val parts: List<String> = speechResults.split(" ");
        var count: Int;
        for (word: WordDetails in wordDetails) {
            count = 0;
            for (part: String in parts) {
                if (word.name?.indexOf(part, 0, true) != -1) {
                    count += 1;
                }
            }
            counters.add(count);
        }
        println("*---------------------------------------------------------------*")
        println(counters);
        println("*---------------------------------------------------------------*")
        println(parts);
        counters.maxByOrNull { it }?.let {
            if (it > 0) {
                val temp = counters.indexOf(it);
                if (temp > -1) {
                    val intent = Intent(application, PhraseDetails::class.java)
                    intent.putExtra("phrase", wordDetails[temp].name);
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "No hay coincidencias.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(baseContext, "No hay coincidencias.", Toast.LENGTH_SHORT).show();
            }
        } ?: run {
            Toast.makeText(baseContext, "No hay coincidencias.", Toast.LENGTH_SHORT).show();
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy();
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow();
        databaseRef.activity = this;
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                REQ_CODE_SPEECH_INPUT
            )
        }
    }

    fun sendAboutUs(view: View) {
        val intent = Intent(this, AboutUs::class.java)
        startActivity(intent)
    }

    fun sendDictionary(view: View) {
        val intent = Intent(this, Dictionary::class.java)
        startActivity(intent)
    }

    fun dayTip() {
        validateDayTip(window.decorView.rootView)
    }

    private fun validateDayTip(view: View) {
        val sharedPreferences: SharedPreferences? =
            getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        val day = sharedPreferences?.getString(getString(R.string.day_key), null)
        val df: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
        val currentDate = df.format(Calendar.getInstance().time)

        day?.let {
            if (currentDate.compareTo(it) != 0) {
                // Muestro el popup de dennis UwU
                launchTipDayPopup(view);
                with(sharedPreferences.edit()) {
                    putString(getString(R.string.day_key), currentDate)
                    commit()
                }
            }
        } ?: run {
            // Muestro el popup de dennis UwU
            launchTipDayPopup(view);
            with(sharedPreferences!!.edit()) {
                putString(getString(R.string.day_key), currentDate)
                commit()
            }
        }
    }

    private fun launchTipDayPopup(view: View) {
        val inflater: LayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.tip_day_popup, null)
        val sharedPreferences: SharedPreferences? =
            getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        val daily: String? = sharedPreferences?.getString(getString(R.string.day_list_key), null);
        val image = popupView.findViewById<ImageView>(R.id.imgPopup);
        val description = popupView.findViewById<TextView>(R.id.descriptionPopup);

        if (sharedPreferences != null) {
            daily?.let {
                var noMoreLeft = true;
                for (tip: DayTips in databaseRef.dayTips) {
                    if (it.indexOf(tip.tip, 0, false) == -1) {
                        noMoreLeft = false;
                        Glide.with(popupView).load(tip.image).into(image);
                        description.text = tip.description;
                        with(sharedPreferences.edit()) {
                            putString(getString(R.string.day_list_key), it + "&" + tip.tip)
                            commit()
                        }
                    }
                }
                if (noMoreLeft) {
                    val tip = databaseRef.dayTips[0];
                    Glide.with(popupView).load(tip.image).into(image);
                    description.text = tip.description;
                    with(sharedPreferences.edit()) {
                        putString(getString(R.string.day_list_key), tip.tip);
                        commit()
                    }
                }
            } ?: run {
                val tip = databaseRef.dayTips[0];
                Glide.with(popupView).load(tip.image).into(image);
                description.text = tip.description;
                with(sharedPreferences.edit()) {
                    putString(getString(R.string.day_list_key), tip.tip);
                    commit()
                }
            }
        }

        val popupWindows = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindows.showAtLocation(view, Gravity.CENTER, 0, 0)

        val button = popupView.findViewById<Button>(R.id.btnOk)
        button.setOnClickListener { popupWindows.dismiss(); }
    }

    companion object {
        private const val REQ_CODE_SPEECH_INPUT: Int = 100;
    }
}