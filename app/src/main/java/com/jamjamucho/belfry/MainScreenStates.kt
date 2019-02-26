package com.jamjamucho.belfry

import android.support.transition.ChangeBounds
import android.support.transition.Fade
import android.support.v4.view.ViewCompat
import android.view.View
import com.jamjamucho.pumpkin.LayoutState

class MainScreenStates {

    class State1: LayoutState() {
        init {

            change(R.id.title, R.id.title2) {
                when (it.id) {
                    R.id.title -> it.visibility = View.VISIBLE
                    R.id.title2 -> it.visibility = View.INVISIBLE
                }
            }.withTransition(Fade::class) {
                duration = 1000
            }

            change(R.id.button) {
                ViewCompat.offsetTopAndBottom(it, 100)
            }.withTransition(ChangeBounds::class) {
                duration = 1000
            }
        }
    }

    class State2: LayoutState() {
        init {

            change(R.id.title, R.id.title2) {
                when (it.id) {
                    R.id.title -> it.visibility = View.INVISIBLE
                    R.id.title2 -> it.visibility = View.VISIBLE
                }
            }.withTransition(Fade::class) {
                duration = 1000
            }

            change(R.id.button) {
                ViewCompat.offsetTopAndBottom(it, -100)
            }.withTransition(ChangeBounds::class) {
                duration = 1000
            }
        }
    }
}