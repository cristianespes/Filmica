package com.celapps.filmica

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button

class FilmsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_films)

//        val button : Button = findViewById(R.id.btn_film)
//        button.setOnClickListener {
//            Log.d(FilmsActivity::class.java.canonicalName, "Button was clicked")
//
//            val intentToDetails: Intent = Intent(this, DetailsActivity::class.java)
//            startActivity(intentToDetails)
//        }

        val list = findViewById<RecyclerView>(R.id.list_films)
        list.layoutManager = LinearLayoutManager(this)

        val adapter= FilmsAdapter()
        list.adapter = adapter
        adapter.setFilms(FilmsRepository.films)
    }

    fun showDetails(clickedView: View) {
        val intentToDetails = Intent(this, DetailsActivity::class.java)
        startActivity(intentToDetails)
    }

}