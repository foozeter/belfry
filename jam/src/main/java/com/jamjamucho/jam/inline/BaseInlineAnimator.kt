package com.jamjamucho.jam.inline

import android.content.Context
import android.util.AttributeSet

abstract class BaseInlineAnimator(
    context: Context, attributeSet: AttributeSet)
    : BaseInline(context, attributeSet) {

//    @IdRes
//    var trigger: Int?
//
//    var allowInterruption: Boolean
//
//    var repeatAtInterruption: Boolean
//
//    var triggeredByClick: Boolean
//
//    var triggeredByLongClick: Boolean
//
//    var stateName: String?
//
//    var necessaryStateCondition: String?
//
//    var nextState: String?
//
//    init {
//
//        val attrs = context.obtainStyledAttributes(
//            attributeSet, R.styleable.BaseInlineAnimator, 0, 0)
//
//        val id = attrs.getResourceId(R.styleable.BaseInlineAnimator_jam_trigger, 0)
//        trigger = if (id > 0) id else null
//
//        allowInterruption = attrs.getBoolean(
//            R.styleable.BaseInlineAnimator_jam_allowInterruption, true)
//
//        repeatAtInterruption = attrs.getBoolean(
//            R.styleable.BaseInlineAnimator_jam_reverseAtInterruption, false)
//
//        triggeredByClick = attrs.getBoolean(
//            R.styleable.BaseInlineAnimator_jam_triggeredByClick, false)
//
//        triggeredByLongClick = attrs.getBoolean(
//            R.styleable.BaseInlineAnimator_jam_triggeredByLongClick, false)
//
//        stateName = attrs.getString(R.styleable.BaseInlineAnimator_jam_stateName)
//
//        necessaryStateCondition = attrs.getString(R.styleable.BaseInlineAnimator_jam_necessaryStateCondition)
//
//        nextState = attrs.getString(R.styleable.BaseInlineAnimator_jam_nextState)
//
//        attrs.recycle()
//    }

}