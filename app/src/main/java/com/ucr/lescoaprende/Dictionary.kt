package com.ucr.lescoaprende

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ucr.lescoaprende.database.Database
import com.ucr.lescoaprende.database.WordClass
import com.ucr.lescoaprende.database.WordDetails
import com.ucr.lescoaprende.objects.DictionaryCustomAdapter

class Dictionary : AppCompatActivity() {
    private lateinit var dictionaryKitchenList: RecyclerView
    private lateinit var dictionaryMiscellaneousList: RecyclerView
    private val DatabaseRef = Database.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        dictionaryKitchenList = findViewById(R.id.dictionaryKitchenList)
        dictionaryMiscellaneousList = findViewById(R.id.dictionaryMiscellaneousList)

        generateLists()
    }

    private fun generateLists() {
        generateListKitchen()
        generateListMiscellaneous()
    }

    private fun generateListKitchen() {
        val temp: ArrayList<WordDetails> = ArrayList()
        for (word in DatabaseRef.wordDetails) {
            if (word.type === WordClass.KITCHEN) {
                temp.add(word);
            }
        }
        dictionaryKitchenList.adapter = DictionaryCustomAdapter(temp)
        dictionaryKitchenList.layoutManager = LinearLayoutManager(this)
    }

    private fun generateListMiscellaneous() {
        val temp: ArrayList<WordDetails> = ArrayList()
        for (word in DatabaseRef.wordDetails) {
            if (word.type === WordClass.MISCELLANEOUS) {
                temp.add(word);
            }
        }
        dictionaryMiscellaneousList.adapter = DictionaryCustomAdapter(temp)
        dictionaryMiscellaneousList.layoutManager = LinearLayoutManager(this)
    }
}