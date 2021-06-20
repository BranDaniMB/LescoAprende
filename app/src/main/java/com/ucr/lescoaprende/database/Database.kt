package com.ucr.lescoaprende.database

import android.media.Image
import android.provider.MediaStore
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

data class DayTips(val image: Image, val tip: String)
data class WordDetails(val video: MediaStore.Video, val image: Image, val description: String)

class Database() {
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val rtDatabase = Firebase.database
    private lateinit var dayTips: Array<DayTips>
    private lateinit var wordDetails: Array<WordDetails>

    init {
        chargeDayTips()
        chargeWordDetails()
    }

    private fun chargeDayTips() {
        val tipsImageRef: StorageReference? = storageRef.child("consejos")
        val tipsDescriptionRef = rtDatabase.getReference("consejos")

        tipsDescriptionRef.get().addOnSuccessListener {

        }.addOnFailureListener{

        }
    }

    private fun chargeWordDetails() {
        val wordVideoRef: StorageReference? = storageRef.child("video")
        val wordImageRef: StorageReference? = storageRef.child("imagen")
        val wordDescriptionRef = rtDatabase.getReference("video")

        wordDescriptionRef.get().addOnSuccessListener {

        }.addOnFailureListener{

        }
    }
}
