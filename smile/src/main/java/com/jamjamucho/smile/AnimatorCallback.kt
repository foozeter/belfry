package com.jamjamucho.smile

abstract class AnimatorCallback {
    open fun onAnimationEnd(animation: AnimatorHolder) {}
    open fun onAnimationStart(animation: AnimatorHolder) {}
    open fun onAnimationRepeat(animation: AnimatorHolder) {}
    open fun onAnimationCancel(animation: AnimatorHolder) {}
}