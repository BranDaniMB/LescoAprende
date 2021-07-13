package com.ucr.lescoaprende

import android.os.Bundle
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ucr.lescoaprende.database.Database
import com.ucr.lescoaprende.database.WordDetails

class PhraseDetails : AppCompatActivity() {
    lateinit var phrase: WordDetails
    private val databaseRef = Database.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phrase_details)
        phrase = databaseRef.findWordDetail(intent.extras?.getString("phrase").toString())!!;
        phrase.name?.let {
            val phraseDetailsName = findViewById<TextView>(R.id.PhraseDetailsName)
            phraseDetailsName.text = phrase.name;
        }

        phrase.video?.let {
            val videoView: VideoView = findViewById(R.id.PhraseDetailsVideo);
            videoView.setVideoPath(phrase.video)
            videoView.setMediaController(MediaController(this));
            videoView.requestFocus();
            videoView.start();
        }

        phrase.image?.let {
            val imageView: ImageView = findViewById(R.id.PhraseDetailsTip);
            Glide.with(this).load(it).into(imageView);
            imageView.setImageURI(it);
        }

        phrase.description?.let {
            val description: TextView = findViewById(R.id.PhraseDetailsTipSpan);
            description.text = it;
        }
    }
}