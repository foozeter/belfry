package com.jamjamucho.belfry

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jamjamucho.jam.Jam

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jam = Jam.setup(this, R.layout.activity_main)
    }
}
