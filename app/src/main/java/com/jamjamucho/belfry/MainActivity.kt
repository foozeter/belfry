package com.jamjamucho.belfry

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jamjamucho.pumpkin.LayoutStateManager
import kotlinx.android.synthetic.main.test_layout.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_layout)

        val stateManager = LayoutStateManager
            .setupWith(rootView)
            .addState(MainScreenStates.State1())
            .addState(MainScreenStates.State2())
            .build()

        var flag = true

        button.setOnClickListener {
            if (flag) {
                stateManager.go(MainScreenStates.State2::class)
                flag = false
            } else {
                stateManager.go(MainScreenStates.State1::class)
                flag = true
            }
        }

//        setContentView(R.layout.activity_main)
//        val smile = Smile(R.xml.test_animator_associating, rootView)

        // Use the light theme for the navigation bar
        // and get views dawn below it.
//        if (26 <= Build.VERSION.SDK_INT) {
//            window.decorView.systemUiVisibility =
//                    SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
//                    SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//        }

   }
}
