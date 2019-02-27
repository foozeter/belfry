package com.jamjamucho.belfry

import android.support.v4.view.ViewCompat
import android.view.View
import com.jamjamucho.pumpkin.LayoutState

class MainScreenStates {

    class State1: LayoutState() {
        override fun onMakeState() {

            changeSequentially {

                changeFadeOut(R.id.title2) {
                    it.visibility = View.INVISIBLE
                }

                changeBounds(R.id.button) {
                    ViewCompat.offsetTopAndBottom(it, -1000)
                }

                changeFadeIn(R.id.title) {
                    it.visibility = View.VISIBLE
                }

            }.shareTransitionConfig {
                duration = 2000
            }
        }
    }

    class State2: LayoutState() {
        override fun onMakeState() {

            changeSequentially {

                changeFadeOut(R.id.title) {
                    it.visibility = View.INVISIBLE
                }

                changeBounds(R.id.button) {
                    ViewCompat.offsetTopAndBottom(it, 1000)
                }

                changeFadeIn(R.id.title2) {
                    it.visibility = View.VISIBLE
                }

            }.shareTransitionConfig {
                duration = 2000
            }

        }
    }
}