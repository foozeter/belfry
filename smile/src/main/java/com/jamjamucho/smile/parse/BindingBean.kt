package com.jamjamucho.smile.parse

import android.support.annotation.AnimatorRes
import android.support.annotation.IdRes

data class BindingBean(
    val id: Int,
    @IdRes val target: Int,
    @AnimatorRes val animator: Int)