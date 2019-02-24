package com.jamjamucho.belfry

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jamjamucho.smile.AnimatorAssociatingInflater

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_layout)
//        val jam = Jam.setup(this, R.layout.activity_main)
        val associating = AnimatorAssociatingInflater(this)
            .inflate(R.xml.test_animator_associating)

        Log.d("mylog", "$associating")
   }
}
