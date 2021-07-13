package com.ucr.lescoaprende.database

import android.app.Application
import android.net.Uri
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.ucr.lescoaprende.MainActivity
import java.io.File
import java.net.URI
import java.nio.file.Files

enum class WordClass() {
    KITCHEN, MISCELLANEOUS
}

data class DayTips(var image: Uri?, var tip: String, var description: String)
data class WordDetails(
    var type: WordClass,
    var name: String?,
    var video: String?,
    var image: Uri?,
    var description: String?
)

class Database() : Application() {
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var rtDatabase: FirebaseDatabase
    lateinit var dayTips: ArrayList<DayTips>
    lateinit var wordDetails: ArrayList<WordDetails>
    lateinit var activity: MainActivity

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
        val tipsDescriptionRef = rtDatabase.getReference("tips")

        tipsDescriptionRef.get().addOnSuccessListener {
            dayTips = ArrayList();
            val temp: HashMap<String, String> =
                it.getValue(false) as HashMap<String, String>
            for ((name, description) in temp) {
                dayTips.add(
                    DayTips(
                        null,
                        name,
                        description
                    )
                )

            }
            val rootDir: File = filesDir;
            if (rootDir.isDirectory) {
                val imagesDir = File(rootDir.path + File.separator + "image")
                if (!imagesDir.exists()) {
                    Files.createDirectory(imagesDir.toPath());
                }
                for (tip: DayTips in dayTips) {
                    val file = File(imagesDir.path + File.separator + tip.tip + ".gif")
                    if (file.exists() && file.length() > 0 && file.isFile) {
                        tip.image = Uri.fromFile(file)
                    } else {
                        file.delete();
                        file.createNewFile();
                        val image = tipsImageRef?.child(tip.tip + ".gif");
                        image?.getFile(file)?.addOnSuccessListener {
                            tip.image = Uri.fromFile(file)
                        }?.addOnFailureListener {
                            // Handle any errors
                        }
                    }
                }
            }
            activity.dayTip();
        }.addOnFailureListener {

        }
    }

    fun findWordDetail(value: String): WordDetails? {
        for (word: WordDetails in wordDetails) {
            if (word.name?.equals(value) == true) {
                return word;
            }
        }
        return null;
    }

    private fun chargeWordDetails() {
        val wordVideoRef: StorageReference = storageRef.child("video")
        val wordImageRef: StorageReference = storageRef.child("imagen")
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

            val rootDir: File = filesDir;
            if (rootDir.isDirectory) {
                val videosDir = File(rootDir.path + File.separator + "video")
                if (!videosDir.exists()) {
                    Files.createDirectory(videosDir.toPath());
                }
                for (word: WordDetails in wordDetails) {
                    val file = File(videosDir.path + File.separator + word.name + ".mp4")
                    if (file.exists() && file.length() > 0 && file.isFile) {
                        word.video = file.path;
                    } else {
                        file.delete();
                        file.createNewFile();
                        val video = wordVideoRef.child(word.name + ".mp4");
                        video.getFile(file).addOnSuccessListener {
                            word.video = file.path;
                        }.addOnFailureListener {
                            // Handle any errors
                        }
                    }
                }
                val imagesDir = File(rootDir.path + File.separator + "image")
                if (!imagesDir.exists()) {
                    Files.createDirectory(imagesDir.toPath());
                }
                for (word: WordDetails in wordDetails) {
                    val file = File(imagesDir.path + File.separator + word.name + ".png")
                    if (file.exists() && file.length() > 0 && file.isFile) {
                        word.image = Uri.fromFile(file);
                    } else {
                        file.delete();
                        file.createNewFile();
                        val image = wordImageRef.child(word.name + ".png");
                        image.getFile(file).addOnSuccessListener {
                            word.image = Uri.fromFile(file);
                        }.addOnFailureListener {
                            // Handle any errors
                        }
                    }
                }
            }
        }.addOnFailureListener {

        }
    }
}
