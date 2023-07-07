package com.sri.learning

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val translate: EditText = findViewById(R.id.translate)
        val translated: TextView = findViewById(R.id.translated)
        translated.movementMethod = ScrollingMovementMethod();
        val tButton: MaterialButton = findViewById(R.id.idBtnTranslateLanguage)
        val cButton: MaterialButton = findViewById((R.id.clear))
        val changeButton: Spinner = findViewById(R.id.languages)
        translated.text = ""

        tButton.setOnClickListener {
            val selectedLang: String = changeButton.selectedItem.toString()
            var tLang = "hi"

            if (selectedLang == "Tamil") {
                tLang = "ta"
            } else if (selectedLang == "Kannada") {
                tLang = "kn"
            } else if (selectedLang == "Hindi") {
                tLang = "hi"
            }

            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(tLang)
                .build()

            val englishHindiTranslator = Translation.getClient(options)

            //make sure that our EditText is not empty
            if (translate.text.isNotEmpty()) {

                Handler().postDelayed({
                    if (translated.text.isEmpty()) {
                        Toast.makeText(this, "Downloading Language, Please Wait!", Toast.LENGTH_LONG).show()
                    }
                }, 1000)

                //Downloads the model files required for translation, if they are not already present
                englishHindiTranslator.downloadModelIfNeeded()
                    .addOnSuccessListener {
                        Log.e(TAG, "Download Successful")

                        //Translates the given input from the source language into the target language.
                        englishHindiTranslator.translate(translate.text.toString())
                            .addOnSuccessListener {
                                //Translation successful
                                translated.text = it
                            }
                            .addOnFailureListener {
                                //Error
                                Log.e(TAG, "Error: " + it.localizedMessage)
                            }
                            .addOnFailureListener {
                                Log.e(TAG, "Download Error: " + it.localizedMessage)
                            }
                    }
            } else {
                Toast.makeText(this, "Please Enter a text", Toast.LENGTH_SHORT).show()
            }
            cButton.setOnClickListener {
                translated.text = ""
                translate.text.clear()
                Toast.makeText(this, "Cleared!", Toast.LENGTH_SHORT).show()
            }

            changeButton.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    translated.text = ""
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    translated.text = ""
                }
            }
        }
    }
}