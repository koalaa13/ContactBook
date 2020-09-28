package com.example.contactbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewManager = LinearLayoutManager(this)
        val contactsList = (0..30).map {
            Contact("Fullname #$it", "Number #$it")
        }
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = ContactAdapter(contactsList) {
                Toast.makeText(
                    this@MainActivity,
                    "Clicked on contact #$it!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}