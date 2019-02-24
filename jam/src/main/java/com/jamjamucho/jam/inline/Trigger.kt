package com.jamjamucho.jam.inline

import android.content.Context
import android.support.annotation.IdRes
import android.util.AttributeSet
import com.jamjamucho.jam.R

class Trigger(
    context: Context, attributeSet: AttributeSet)
    : BaseInline(context, attributeSet) {

    @IdRes
    var trigger: Int?
    
    var triggeredByClick: Boolean

    var triggeredByLongClick: Boolean

    var animationName: String?

    var stateName: String?

    var necessaryStateCondition: String?

    var nextState: String?

    init {

        val attrs = context.obtainStyledAttributes(
            attributeSet, R.styleable.Trigger, 0, 0)

        val id = attrs.getResourceId(R.styleable.Trigger_jam_trigger, 0)
        trigger = if (id > 0) id else null

        triggeredByClick = attrs.getBoolean(
            R.styleable.Trigger_jam_triggeredByClick, false)

        triggeredByLongClick = attrs.getBoolean(
            R.styleable.Trigger_jam_triggeredByLongClick, false)

        stateName = attrs.getString(R.styleable.Trigger_jam_stateName)

        necessaryStateCondition = attrs.getString(R.styleable.Trigger_jam_necessaryStateCondition)

        nextState = attrs.getString(R.styleable.Trigger_jam_nextState)

        animationName = attrs.getString(R.styleable.Trigger_jam_animationName)

        attrs.recycle()
    }
}