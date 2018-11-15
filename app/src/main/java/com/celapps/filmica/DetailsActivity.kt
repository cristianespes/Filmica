package com.celapps.filmica

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val button: Button = findViewById(R.id.btn_add)

        //val listener: View.OnClickListener = object : View.OnClickListener {
          //  override fun onClick(p0: View?) {
            //    Toast.makeText(this@DetailsActivity, "Added to list", Toast.LENGTH_SHORT).show()
            //}
        //}

        //val listener: (View) -> Unit = { view ->
          //  Toast.makeText(this@DetailsActivity, "Added to list", Toast.LENGTH_SHORT).show()
        //}

        //button.setOnClickListener(listener)

        button.setOnClickListener {
            Toast.makeText(this@DetailsActivity, "Added to list", Toast.LENGTH_SHORT).show()
        }
    }
}
