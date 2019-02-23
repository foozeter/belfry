package com.jamjamucho.belfry

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jamjamucho.jam.Jam

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.test_layout)
        val jam = Jam.setup(this, R.layout.activity_main)

//        val duration = 300L
//        var flag = true
//
//        val menuIconAnimator_A = ObjectAnimator.ofFloat(animMenuIcon, "progress", 0f ,1f)
//        val menuIconAnimator_B = ObjectAnimator.ofFloat(animMenuIcon, "progress", 1f ,0f)
//        val menuIconAnimator_A2 = ObjectAnimator.ofFloat(animOptionsIcon, "progress", 0f ,1f)
//        val menuIconAnimator_B2 = ObjectAnimator.ofFloat(animOptionsIcon, "progress", 1f ,0f)
//
//        menuIconAnimator_A.duration = duration
//        menuIconAnimator_B.duration = duration
//        menuIconAnimator_A2.duration = duration
//        menuIconAnimator_B2.duration = duration
//
//        button.setOnClickListener {
////            animateMenuIcon(flag, duration)
////            animateOptionIcon(flag, duration)
//
//            menuIconAnimator_A.cancel()
//            menuIconAnimator_B.cancel()
//            menuIconAnimator_A2.cancel()
//            menuIconAnimator_B2.cancel()
//
//            if (flag) {
//                menuIconAnimator_A.start()
//                menuIconAnimator_A2.start()
//            } else {
//                menuIconAnimator_B.start()
//                menuIconAnimator_B2.start()
//            }
//
//            flag = !flag
//        }
    }

//    fun animateOptionIcon(flag: Boolean, duration: Long) {
//
//        val d = duration / 2
//
//        if (flag) {
//
//            closeIcon2.alpha = 0f
//            closeIcon2.scaleX = 0f
//            closeIcon2.scaleY = 0f
//            optionIcon.alpha = 1f
//            optionIcon.scaleY = 1f
//
//            closeIcon2
//                .animate()
//                .scaleX(1f)
//                .scaleY(1f)
//                .alpha(1f)
////                .setDuration(d)
////                .setStartDelay(d)
//                .setDuration(duration)
//                .start()
//
//            optionIcon
//                .animate()
//                .scaleY(0f)
//                .alpha(0f)
////                .setDuration(d)
//                .setDuration(duration)
//                .start()
//
//        } else {
//
//            closeIcon2.alpha = 1f
//            closeIcon2.scaleX = 1f
//            closeIcon2.scaleY = 1f
//            optionIcon.scaleY = 0f
//
//            closeIcon2
//                .animate()
//                .scaleX(0f)
//                .scaleY(0f)
//                .alpha(0f)
////                .setDuration(d)
//                .setDuration(duration)
//                .start()
//            optionIcon
//                .animate()
//                .scaleY(1f)
//                .alpha(1f)
//                .setDuration(duration)
////                .setDuration(d)
////                .setStartDelay(d)
//                .start()
//
//        }
//    }
//
//    fun animateMenuIcon(flag: Boolean, duration: Long) {
//        if (flag) {
//
//            closeIcon.rotation = 45f
//            closeIcon.alpha = 0f
//            menuIcon.rotation = 0f
//            menuIcon.alpha = 1f
//
//            closeIcon
//                .animate()
//                .rotation(0f)
//                .alpha(1f)
//                .setDuration(duration)
//                .start()
//
//            menuIcon
//                .animate()
//                .scaleY(0f)
//                .rotation(-45f)
//                .alpha(0f)
//                .setDuration(duration)
//                .start()
//        } else {
//
//            closeIcon.rotation = 0f
//            closeIcon.alpha = 1f
//            menuIcon.rotation = -45f
//            menuIcon.alpha = 0f
//
//            closeIcon
//                .animate()
//                .rotation(45f)
//                .alpha(0f)
//                .setDuration(duration)
//                .start()
//
//            menuIcon
//                .animate()
//                .scaleY(1f)
//                .rotation(0f)
//                .alpha(1f)
//                .setDuration(duration)
//                .start()
//        }
//    }
}
