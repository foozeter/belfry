package com.jamjamucho.belfry

import android.support.transition.ChangeBounds
import android.support.transition.Fade
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.jamjamucho.pumpkin.LayoutState

class MainScreenStates {

    class State1: LayoutState() {
        override fun onMakeState() {

            changes {
                change(R.id.title) { it.visibility = View.VISIBLE }
                change(R.id.title2) { it.visibility = View.INVISIBLE }
            }.withTransition(Fade::class) {
                duration = 500
            }

            change(R.id.button) {
                ViewCompat.offsetTopAndBottom(it, 1000)
            }.withTransition(ChangeBounds::class) {
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
            }

        }
    }

    class State2: LayoutState() {
        override fun onMakeState() {

            changes {
                change(R.id.title) { it.visibility = View.INVISIBLE }
                change(R.id.title2) { it.visibility = View.VISIBLE }
            }.withTransition(Fade::class) {
                duration = 500
            }

            change(R.id.button) {
                ViewCompat.offsetTopAndBottom(it, -1000)
            }.withTransition(ChangeBounds::class) {
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
            }

        }
    }
}