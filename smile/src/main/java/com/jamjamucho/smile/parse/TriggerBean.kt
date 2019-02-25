package com.jamjamucho.smile.parse

import android.support.annotation.IdRes

data class TriggerBean(
    @IdRes val trigger: Int,
    val animations: Int,
    val stateName: Int?,
    val necessaryStateCondition: Int?,
    val nextState: Int?,
    val triggeredByClick: Boolean,
    val triggeredByLongClick: Boolean)