package com.ucr.lescoaprende.database

import android.app.Application
import android.media.Image
import android.provider.MediaStore
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

enum class WordClass() {
    KITCHEN, MISCELLANEOUS
}

data class DayTips(val image: Image, val tip: String)
data class WordDetails(
    val type: WordClass,
    val name: String?,
    val video: MediaStore.Video?,
    val image: Image?,
    val description: String?
)

class Database() : Application() {
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var rtDatabase: FirebaseDatabase
    lateinit var dayTips: ArrayList<DayTips>
    lateinit var wordDetails: ArrayList<WordDetails>

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        instance = this
        storage = Firebase.storage
        storageRef = storage.reference
        rtDatabase = Firebase.database
        chargeDayTips()
        chargeWordDetails()
    }

    companion object {
        lateinit var instance: Database
            private set
    }

    private fun chargeDayTips() {
        val tipsImageRef: StorageReference? = storageRef.child("consejos")
        val tipsDescriptionRef = rtDatabase.getReference("consejos")

        tipsDescriptionRef.get().addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    private fun chargeWordDetails() {
        val wordVideoRef: StorageReference? = storageRef.child("video")
        val wordImageRef: StorageReference? = storageRef.child("imagen")
        // Obtener palabras de Kitchen
        val wordDescriptionRef = rtDatabase.getReference("video")
        wordDescriptionRef.get().addOnSuccessListener {
            wordDetails = ArrayList();
            val temp: HashMap<String, HashMap<String, HashMap<String, String>>> =
                it.getValue(false) as HashMap<String, HashMap<String, HashMap<String, String>>>
            for ((type, words) in temp) {
                for ((key, word) in words) {
                    wordDetails.add(
                        WordDetails(
                            WordClass.valueOf(type.uppercase()),
                            word["name"]!!,
                            null,
                            null,
                            word["description"]!!
                        )
                    )
                }
            }
        }.addOnFailureListener {

        }
    }
}
