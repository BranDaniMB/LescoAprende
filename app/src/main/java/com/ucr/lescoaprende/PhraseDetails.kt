package com.ucr.lescoaprende

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PhraseDetails : AppCompatActivity() {
    lateinit var phrase: String
    lateinit var phraseDetailsName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phrase_details)
        phrase = intent.extras?.getString("phrase").toString()
        phraseDetailsName = findViewById<TextView>(R.id.PhraseDetailsName)
        phraseDetailsName.text = phrase
    }
}