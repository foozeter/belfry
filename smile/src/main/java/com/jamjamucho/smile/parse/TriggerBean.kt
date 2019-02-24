package com.jamjamucho.smile.parse

import android.support.annotation.IdRes

data class TriggerBean(
    @IdRes val trigger: Int,
    val animations: String,
    val stateName: String?,
    val necessaryStateCondition: String?,
    val nextState: String?)