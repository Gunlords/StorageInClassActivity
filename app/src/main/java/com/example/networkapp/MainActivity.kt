package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        loadSavedComic()

        showButton.setOnClickListener {
            val id = numberEditText.text.toString()
            if (id.isNotBlank() && id.toIntOrNull() != null) {
                downloadComic(id)
            } else {
                Toast.makeText(this, "Enter a valid comic number", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Fetches comic from web as JSONObject
    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add(
            JsonObjectRequest(url,
                { showComic(it) },
                { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        )
    }

    // Display a comic for a given comic JSON object
    private fun showComic(comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)

        saveComic(comicObject)
    }

    // Implement this function
    private fun saveComic(comicObject: JSONObject) {
        val sharedPrefs = getSharedPreferences("SavedComic", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("title", comicObject.getString("title"))
            putString("alt", comicObject.getString("alt"))
            putString("img", comicObject.getString("img"))
            putString("num", comicObject.getString("num"))
            apply()
        }
    }
    private fun loadSavedComic() {
        val sharedPrefs = getSharedPreferences("SavedComic", MODE_PRIVATE)
        val title = sharedPrefs.getString("title", null)
        val alt = sharedPrefs.getString("alt", null)
        val img = sharedPrefs.getString("img", null)

        if (title != null && alt != null && img != null) {
            titleTextView.text = title
            descriptionTextView.text = alt
            Picasso.get().load(img).into(comicImageView)
            println("Loaded saved comic: $title")
        }
    }
}