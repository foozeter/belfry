package com.jamjamucho.belfry

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var state: MainScreenState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Use the light theme for the navigation bar
        // and get views dawn below it.
        if (26 <= Build.VERSION.SDK_INT) {
            window.decorView.systemUiVisibility =
//                    SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
                    SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        state = MainScreenState(rootView)

        menuIcon.setOnClickListener {
            when {
                state.isDefaultState() -> state.goToShowingMenuState()
                state.isShowingMenuState() -> state.goToDefaultState()
                state.isShowingOptionsState() -> state.goToShowingMenuState()
            }
        }

        optionsIcon.setOnClickListener {
            when {
                state.isDefaultState() -> state.goToShowingOptionsState()
                state.isShowingMenuState() -> state.goToShowingOptionsState()
                state.isShowingOptionsState() -> state.goToDefaultState()
            }
        }
    }
}
